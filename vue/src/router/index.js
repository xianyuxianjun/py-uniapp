import { createRouter, createWebHistory } from 'vue-router'
import { defaultRoute } from '@/config/menu'

const routes = [
  {
    path: '/',
    redirect: defaultRoute
  },
  {
    path:'/profile',
    component:()=>import('@/views/Profile.vue')
  },
  {
    path:'/settings',
    component:()=>import('@/views/Settings.vue')
  },
  {
    path:'/user-management',
    component:()=>import('@/views/UserManagement.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
