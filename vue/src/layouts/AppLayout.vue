<template>
    <v-app>
      <!-- 导航抽屉 -->
      <app-nav-drawer
        :model-value="drawer"
        @update:model-value="drawer = $event"
        :rail="rail"
        @update:rail="handleRailUpdate"
        :menu-items="menuItems"
      />

      <!-- 顶部应用栏 -->
      <app-top-bar
        :title="currentPageTitle"
      />

      <!-- 主要内容区域 -->
      <v-main class="bg-background">
        <v-container fluid class="pa-6">
          <router-view v-slot="{ Component }">
            <v-fade-transition>
              <component :is="Component" />
            </v-fade-transition>
          </router-view>
        </v-container>
      </v-main>
    </v-app>
  </template>

  <script setup>
  import { ref, computed } from 'vue'
  import { useRoute } from 'vue-router'
  import AppNavDrawer from './AppNavDrawer.vue'
  import AppTopBar from './AppTopBar.vue'
  import { menuItems, getPageTitle } from "@/config/menu.js"

  const route = useRoute()
  const drawer = ref(true)
  const rail = ref(false)

  // 计算当前页面标题
  const currentPageTitle = computed(() => {
    const path = route.path.split('/')[1] || 'dashboard'
    return getPageTitle(path)
  })

  // 处理导航栏折叠状态更新
  const handleRailUpdate = (value) => {
    rail.value = value
  }
  </script>

  <style scoped>
  .bg-background {
    background: linear-gradient(145deg, rgba(0, 184, 148, 0.05), rgba(0, 206, 201, 0.05));
    position: relative;
  }

  .bg-background::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: radial-gradient(circle at top right, rgba(0, 184, 148, 0.1), transparent 70%),
                radial-gradient(circle at bottom left, rgba(0, 206, 201, 0.1), transparent 70%);
    pointer-events: none;
  }

  /* 简单的淡入淡出动画 */
  .fade-enter-active,
  .fade-leave-active {
    transition: opacity 0.15s ease;
  }

  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
  }
  </style>
