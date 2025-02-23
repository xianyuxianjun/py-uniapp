// 菜单配置
export const menuItems = [

  {
    value: 'user-management',
    title: '用户管理',
    icon: 'tabler:users'
  }
]

// 默认路由
export const defaultRoute = 'settings'

// 获取页面标题
export const getPageTitle = (path) => {
  const currentItem = menuItems.find(item => item.value === path)
  return currentItem ? currentItem.title : ''
}

// 系统配置
export const systemConfig = {
  title: '旧物回收平台',
  logo: '/logo.png',
  version: '1.0.0'
}
