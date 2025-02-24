// 高德地图配置文件

import AMapLoader from '@amap/amap-jsapi-loader'

// 安全密钥配置
window._AMapSecurityConfig = {
  securityJsCode: '069745f7e44523af11b802b4b61f40c9',
}

// 地图配置
export const mapConfig = {
  key: '0210f911ba182d239efe8b932b30251a',
  version: '2.0',
  plugins: ['AMap.Geocoder', 'AMap.Geolocation', 'AMap.PlaceSearch', 'AMap.AutoComplete'],
  // 默认中心点（北京）
  center: [116.397428, 39.90923],
  // 默认缩放级别
  zoom: 13
}

let AMap = null

// 初始化地图
export const initAMap = async () => {
  if (AMap) return AMap
  
  try {
    AMap = await AMapLoader.load({
      key: mapConfig.key,
      version: mapConfig.version,
      plugins: mapConfig.plugins
    })
    return AMap
  } catch (error) {
    console.error('高德地图加载失败：', error)
    throw error
  }
}

// 地图工具函数
export const mapUtils = {
  // 获取当前位置
  getCurrentLocation: (map) => {
    return new Promise((resolve, reject) => {
      if (!AMap) {
        reject(new Error('地图未初始化'))
        return
      }
      
      const geolocation = new AMap.Geolocation({
        enableHighAccuracy: true,
        timeout: 10000,
        zoomToAccuracy: true
      })
      
      map.addControl(geolocation)
      geolocation.getCurrentPosition((status, result) => {
        if (status === 'complete') {
          resolve(result)
        } else {
          reject(new Error('获取位置失败'))
        }
      })
    })
  },

  // 地址转经纬度
  getLocationByAddress: (address) => {
    return new Promise((resolve, reject) => {
      if (!AMap) {
        reject(new Error('地图未初始化'))
        return
      }
      
      const geocoder = new AMap.Geocoder()
      geocoder.getLocation(address, (status, result) => {
        if (status === 'complete' && result.info === 'OK') {
          resolve(result.geocodes[0])
        } else {
          reject(new Error('地址解析失败'))
        }
      })
    })
  },

  // 经纬度转地址
  getAddressByLocation: (lngLat) => {
    return new Promise((resolve, reject) => {
      if (!AMap) {
        reject(new Error('地图未初始化'))
        return
      }
      
      const geocoder = new AMap.Geocoder()
      geocoder.getAddress(lngLat, (status, result) => {
        if (status === 'complete' && result.info === 'OK') {
          resolve(result.regeocode)
        } else {
          reject(new Error('逆地址解析失败'))
        }
      })
    })
  }
} 