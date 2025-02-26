import AMapLoader from '@amap/amap-jsapi-loader'

// 高德地图配置
const MAP_CONFIG = {
  key: '0210f911ba182d239efe8b932b30251a',
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
    // #ifdef H5
    if (!geolocation) {
      reject(new Error('定位服务未初始化'))
      return
    }
    
    geolocation.getCurrentPosition((status, result) => {
      if (status === 'complete') {
        const { position, addressComponent = {}, formattedAddress = '' } = result
        resolve({
          latitude: position.lat,
          longitude: position.lng,
          address: formattedAddress,
          // 添加默认值处理，避免 undefined 错误
          name: addressComponent.street || addressComponent.district || '未知地点'
        })
      } else {
        reject(new Error(result.message))
      }
    })
    // #endif
    
    // #ifdef MP-WEIXIN
    uni.getLocation({
      type: 'gcj02',
      isHighAccuracy: true, // 开启高精度定位
      success: async (res) => {
        try {
          const addressInfo = await getAddressFromLocation(res.latitude, res.longitude)
          resolve({
            latitude: res.latitude,
            longitude: res.longitude,
            address: addressInfo.address,
            name: addressInfo.name
          })
        } catch (error) {
          // 如果获取地址失败，至少返回经纬度信息
          resolve({
            latitude: res.latitude,
            longitude: res.longitude,
            address: '位置获取失败',
            name: '未知地点'
          })
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
    // #endif
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