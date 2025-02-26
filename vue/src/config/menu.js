// 菜单配置
export const menuItems = [
  {
    value: 'user-management',
    title: '用户管理',
    icon: 'tabler:users'
  },
  // 添加废品分类管理菜单项
  {
    value: 'waste-category',
    title: '废品分类管理',
    icon: 'tabler:recycle'
  },
  // 添加预约订单管理菜单项
  {
    value: 'appointment-order',
    title: '预约订单管理',
    icon: 'tabler:calendar-time'
  },
  {
    value: 'announcement',
    title: '公告管理',
    icon: 'tabler:news'
  },
  {
    value: 'order-review',
    title: '评价管理',
    icon: 'tabler:star'
  },
  {
    value: 'recycling-record',
    title: '回收记录',
    icon: 'tabler:clipboard-list'
  },
  {
    value: 'product',
    title: '商品管理',
    icon: 'tabler:shopping-cart'
  },
  {
    value: 'statistics',
    title: '数据分析',
    icon: 'tabler:chart-bar'
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
