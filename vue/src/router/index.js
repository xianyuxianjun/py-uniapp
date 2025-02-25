import { createRouter, createWebHistory } from 'vue-router'
import { defaultRoute } from '@/config/menu'
import AppLayout from '@/components/layout/AppLayout.vue'
import { message } from '@/utils/message'
const routes = [
  {
    path: '/login',
    component: () => import('@/views/Login.vue'),
    meta: { layout: 'blank' }  // 使用空白布局
  },
  {
    path: '/',
    component: AppLayout,     // 使用主布局
    children: [
      {
        path: '',
        redirect: defaultRoute
      },
      {
        path: 'user-management',
        component: () => import('@/views/UserManagement.vue')
      },
      {
        path:'profile',
        component:()=>import('@/views/Profile.vue')
      },
      {
        path:'settings',
        component:()=>import('@/views/Settings.vue')
      },
      {
        path:'waste-category',
        component:()=>import('@/views/WasteCategoryManagement.vue')
      },
      {
        path:'appointment-order',
        component:()=>import('@/views/AppointmentOrderManagement.vue')
      },
      {
        path:'announcement',
        component:()=>import('@/views/AnnouncementManagement.vue')
      },
      {
        path:'order-review',
        component:()=>import('@/views/OrderReviewManagement.vue')
      },
      {
        path: 'recycling-record',
        component: () => import('@/views/RecyclingRecordManagement.vue')
      },
      {
        path: 'statistics',
        component: () => import('@/views/Statistics.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  const userId = localStorage.getItem('userId')
  if (to.path !== '/login' && !userId) {
    message.error('请先登录')
    next('/login')
  } else {
    next()
  }
})

export default router
