import AMapLoader from '@amap/amap-jsapi-loader'

// 高德地图配置
const MAP_CONFIG = {
  // Web端 key
  webKey: '0210f911ba182d239efe8b932b30251a',
  // 小程序 key
  mpKey: '你的小程序key', // 需要替换成你的小程序 key
  version: '2.0',
  plugins: ['AMap.Geocoder', 'AMap.Geolocation']
}

// 安全密钥配置
window._AMapSecurityConfig = {
  securityJsCode: '069745f7e44523af11b802b4b61f40c9'
}

let mapInstance = null
let geocoder = null
let geolocation = null

// 初始化地图
export const initAMap = async () => {
  if (mapInstance) return mapInstance
  
  try {
    mapInstance = await AMapLoader.load({
      key: MAP_CONFIG.key,
      version: MAP_CONFIG.version,
      plugins: MAP_CONFIG.plugins
    })
    
    // 初始化地理编码服务
    geocoder = new mapInstance.Geocoder({
      radius: 1000,
      extensions: 'all'
    })
    
    // 初始化定位服务
    geolocation = new mapInstance.Geolocation({
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0,
      convert: true,
      showButton: false,
      showMarker: false,
      showCircle: false,
      panToLocation: true,
      zoomToAccuracy: true
    })
    
    return mapInstance
  } catch (error) {
    console.error('地图加载失败：', error)
    throw error
  }
}

// 获取地图实例
export const getMapInstance = () => mapInstance 

// 获取当前位置
export const getCurrentLocation = () => {
  return new Promise((resolve, reject) => {
    uni.getLocation({
      type: 'gcj02',
      isHighAccuracy: true,
      success: (res) => {
        resolve({
          latitude: res.latitude,
          longitude: res.longitude
        })
      },
      fail: (err) => {
        console.error('获取位置失败：', err)
        reject(new Error('获取位置失败'))
      }
    })
  })
}

// 根据经纬度获取地址信息
export const getAddressFromLocation = (latitude, longitude) => {
  return new Promise((resolve, reject) => {
    // #ifdef H5
    if (!geocoder) {
      reject(new Error('地理编码服务未初始化'))
      return
    }
    
    geocoder.getAddress([longitude, latitude], (status, result) => {
      if (status === 'complete' && result.regeocode) {
        resolve({
          address: result.regeocode.formattedAddress,
          name: result.regeocode.addressComponent.street || result.regeocode.addressComponent.district
        })
      } else {
        reject(new Error('获取地址失败'))
      }
    })
    // #endif
    
    // #ifdef MP-WEIXIN
    uni.request({
      url: `https://restapi.amap.com/v3/geocode/regeo`,
      data: {
        key: MAP_CONFIG.key,
        location: `${longitude},${latitude}`,
        extensions: 'all'
      },
      success: (res) => {
        if (res.data.status === '1' && res.data.regeocode) {
          resolve({
            address: res.data.regeocode.formatted_address,
            name: res.data.regeocode.addressComponent.street || res.data.regeocode.addressComponent.district
          })
        } else {
          reject(new Error('获取地址失败'))
        }
      },
      fail: reject
    })
    // #endif
  })
}

// 打开导航
export const openNavigation = (latitude, longitude, name, address) => {
  return new Promise((resolve, reject) => {
    uni.openLocation({
      latitude: Number(latitude),
      longitude: Number(longitude),
      name: name,
      address: address,
      success: resolve,
      fail: reject
    })
  })
}

// 获取路线规划信息
export const getRouteInfo = (fromLng, fromLat, toLng, toLat) => {
    return new Promise((resolve, reject) => {
        // #ifdef MP-WEIXIN
        wx.request({
            url: `https://restapi.amap.com/v3/direction/walking`,
            data: {
                key: MAP_CONFIG.mpKey,
                origin: `${fromLng},${fromLat}`,
                destination: `${toLng},${toLat}`,
                extensions: 'base'
            },
            success: (res) => {
                if (res.data.status === '1' && res.data.route) {
                    try {
                        const path = res.data.route.paths[0]
                        const points = []
                        
                        // 解析所有步骤的路线点
                        path.steps.forEach(step => {
                            if (step.polyline) {
                                const coordinates = step.polyline.split(';')
                                coordinates.forEach(coord => {
                                    const [lng, lat] = coord.split(',').map(Number)
                                    if (!isNaN(lng) && !isNaN(lat)) {
                                        points.push({
                                            longitude: lng,
                                            latitude: lat
                                        })
                                    }
                                })
                            }
                        })

                        // 确保至少有起点和终点
                        if (points.length === 0) {
                            points.push(
                                { longitude: Number(fromLng), latitude: Number(fromLat) },
                                { longitude: Number(toLng), latitude: Number(toLat) }
                            )
                        }

                        resolve({
                            points: points,
                            distance: path.distance > 1000 
                                ? `${(path.distance/1000).toFixed(1)}公里` 
                                : `${path.distance}米`,
                            duration: path.duration > 60 
                                ? `${Math.floor(path.duration/60)}分钟` 
                                : `${path.duration}秒`
                        })
                    } catch (error) {
                        console.error('路线数据解析失败：', error)
                        // 降级处理：只返回起点和终点
                        resolve({
                            points: [
                                { longitude: Number(fromLng), latitude: Number(fromLat) },
                                { longitude: Number(toLng), latitude: Number(toLat) }
                            ],
                            distance: '计算中',
                            duration: '计算中'
                        })
                    }
                } else {
                    console.error('路线规划API返回错误：', res.data)
                    // 降级处理
                    resolve({
                        points: [
                            { longitude: Number(fromLng), latitude: Number(fromLat) },
                            { longitude: Number(toLng), latitude: Number(toLat) }
                        ],
                        distance: '计算中',
                        duration: '计算中'
                    })
                }
            },
            fail: reject
        })
        // #endif

        // #ifdef H5
        uni.request({
            url: `https://restapi.amap.com/v3/direction/walking`,
            data: {
                key: MAP_CONFIG.webKey,
                origin: `${fromLng},${fromLat}`,
                destination: `${toLng},${toLat}`,
                extensions: 'base'
            },
            success: (res) => {
                if (res.data.status === '1' && res.data.route) {
                    try {
                        const path = res.data.route.paths[0]
                        const points = []
                        
                        // 解析所有步骤的路线点
                        path.steps.forEach(step => {
                            if (step.polyline) {
                                const coordinates = step.polyline.split(';')
                                coordinates.forEach(coord => {
                                    const [lng, lat] = coord.split(',').map(Number)
                                    if (!isNaN(lng) && !isNaN(lat)) {
                                        points.push({
                                            longitude: lng,
                                            latitude: lat
                                        })
                                    }
                                })
                            }
                        })

                        // 确保至少有起点和终点
                        if (points.length === 0) {
                            points.push(
                                { longitude: Number(fromLng), latitude: Number(fromLat) },
                                { longitude: Number(toLng), latitude: Number(toLat) }
                            )
                        }

                        resolve({
                            points: points,
                            distance: path.distance > 1000 
                                ? `${(path.distance/1000).toFixed(1)}公里` 
                                : `${path.distance}米`,
                            duration: path.duration > 60 
                                ? `${Math.floor(path.duration/60)}分钟` 
                                : `${path.duration}秒`
                        })
                    } catch (error) {
                        console.error('路线数据解析失败：', error)
                        // 降级处理：只返回起点和终点
                        resolve({
                            points: [
                                { longitude: Number(fromLng), latitude: Number(fromLat) },
                                { longitude: Number(toLng), latitude: Number(toLat) }
                            ],
                            distance: '计算中',
                            duration: '计算中'
                        })
                    }
                } else {
                    console.error('路线规划API返回错误：', res.data)
                    // 降级处理
                    resolve({
                        points: [
                            { longitude: Number(fromLng), latitude: Number(fromLat) },
                            { longitude: Number(toLng), latitude: Number(toLat) }
                        ],
                        distance: '计算中',
                        duration: '计算中'
                    })
                }
            },
            fail: reject
        })
        // #endif
    })
} 