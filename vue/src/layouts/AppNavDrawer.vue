<template>
    <!-- 导航抽屉 -->
    <v-navigation-drawer
      :model-value="modelValue"
      @update:model-value="$emit('update:modelValue', $event)"
      :rail="rail"
      permanent
      @click.stop
      class="elevation-3 drawer-gradient"
      :class="{ 'drawer-expanded': !rail, 'drawer-rail': rail }"
      width="280"
      :rail-width="72"
    >
      <!-- 顶部Logo区域 -->
      <div class="px-4 py-6 d-flex align-center logo-container">
        <v-slide-x-transition>
          <v-avatar
            v-if="rail"
            size="40"
            class="mx-auto elevation-2 rail-logo"
            @click.stop="toggleRail"
          >
            <v-img 
              src="/logo.png" 
              alt="Logo" 
              class="logo-pulse"
              :width="40"
              :height="40"
              cover
              eager
              :aspect-ratio="1"
            />
          </v-avatar>
        </v-slide-x-transition>
        
        <v-slide-x-transition>
          <div v-if="!rail" class="d-flex align-center">
            <v-avatar
              size="40"
              class="me-3 elevation-2"
            >
              <v-img 
                :src="systemConfig.logo" 
                alt="Logo" 
                class="logo-pulse"
                :width="40"
                :height="40"
                cover
                eager
                :aspect-ratio="1"
              />
            </v-avatar>
            <span class="text-h6 font-weight-bold white--text logo-text">{{systemConfig.title}}</span>
          </div>
        </v-slide-x-transition>
  
        <v-spacer></v-spacer>
        
        <v-scale-transition>
          <v-btn
            v-if="!rail"
            variant="text"
            class="toggle-btn"
            @click.stop="toggleRail"
            color="white"
          >
            <Icon icon="tabler:chevron-left" width="28" height="28" />
          </v-btn>
        </v-scale-transition>
      </div>
  
      <v-divider class="border-opacity-25"></v-divider>
  
      <!-- 导航菜单 -->
      <v-list class="mt-2" :class="{ 'rail-list': rail }">
        <v-slide-y-transition group>
          <v-list-item
            v-for="(item, index) in menuItems"
            :key="item.value"
            :prepend-icon="item.icon"
            :title="item.title"
            :value="item.value"
            color="white"
            class="menu-item mb-1"
            :class="{ 
              'px-2': rail, 
              'menu-item-active': activeItem === item.value,
              'menu-item-rail': rail
            }"
            :style="{ transitionDelay: `${index * 50}ms` }"
            @click.stop="handleMenuClick(item)"
          >
            <template v-slot:prepend>
              <Icon :icon="item.icon" width="28" height="28" />
            </template>
            <template v-slot:append>
              <v-scale-transition>
                <v-icon 
                  v-if="activeItem === item.value && !rail" 
                  color="white" 
                  icon="mdi-circle-small"
                  class="active-indicator"
                ></v-icon>
              </v-scale-transition>
            </template>
          </v-list-item>
        </v-slide-y-transition>
      </v-list>
  
      <!-- 底部设置区域 -->
      <template v-slot:append>
        <v-divider class="border-opacity-25"></v-divider>
        <v-list :class="{ 'rail-list': rail }">
          <v-list-item
            v-slot:prepend
            title="设置"
            value="settings"
            color="white"
            class="menu-item"
            :class="{ 
              'px-2': rail,
              'menu-item-rail': rail
            }"
            @click.stop="handleMenuClick({value: 'settings', title: '设置'})"
          >
            <Icon icon="tabler:settings" width="28" height="28" />
          </v-list-item>
        </v-list>
      </template>
    </v-navigation-drawer>
  </template>
  
  <script setup>
  import { ref, watch } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { Icon } from '@iconify/vue'
  import { systemConfig } from '@/config/menu'
  
  const props = defineProps({
    modelValue: {
      type: Boolean,
      required: true
    },
    rail: {
      type: Boolean,
      required: true
    },
    menuItems: {
      type: Array,
      required: true
    }
  })
  
  const emit = defineEmits(['update:modelValue', 'update:rail'])
  
  const router = useRouter()
  const route = useRoute()
  
  // 控制导航抽屉的状态
  const activeItem = ref('dashboard')
  
  // 监听路由变化
  watch(() => route.path, (newPath) => {
    const path = newPath.split('/')[1] // 获取路径的第一个部分
    activeItem.value = path || 'dashboard'
  }, { immediate: true })
  
  // 处理菜单点击
  const handleMenuClick = (item) => {
    activeItem.value = item.value
    router.push(`/${item.value}`)
  }
  
  // 切换导航栏展开/折叠状态
  const toggleRail = () => {
    emit('update:rail', !props.rail)
  }
  </script>
  
  <style scoped>
  /* 保持原有的渐变背景等样式 */
  .drawer-gradient {
    background: linear-gradient(145deg, var(--v-theme-primary), var(--v-theme-primary-darken-1));
    transition: all 0.3s ease;
  }
  
  .drawer-expanded {
    box-shadow: 0 0 20px rgba(var(--v-theme-primary), 0.15);
  }
  
  .drawer-rail {
    border-right: none;
  }
  
  /* 优化Logo区域样式 */
  .logo-container {
    transition: padding 0.3s ease;
    height: 64px;
    overflow: hidden;
    white-space: nowrap;
  }
  
  .logo-text {
    opacity: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 180px;
    transition: opacity 0.3s ease, transform 0.3s ease;
    transform-origin: left center;
  }
  
  .drawer-rail .logo-text {
    opacity: 0;
    transform: scale(0);
    width: 0;
    margin: 0;
  }
  
  .rail-logo {
    cursor: pointer;
    transition: transform 0.3s ease;
  }
  
  .rail-logo:hover {
    transform: scale(1.1) rotate(5deg);
  }
  
  /* 优化菜单项样式 */
  .menu-item {
    margin: 4px 8px;
    border-radius: 16px;
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    backdrop-filter: blur(8px);
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.1);
    height: 48px;
    display: flex;
    align-items: center;
  }
  
  .menu-item::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background: rgba(255, 255, 255, 0.2);
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .menu-item:hover::before {
    transform: translateX(0);
  }
  
  .menu-item:hover {
    transform: translateX(4px);
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
  
  .menu-item-active {
    background: rgba(255, 255, 255, 0.3);
    box-shadow: 0 4px 20px rgba(255, 255, 255, 0.25);
    border: 1px solid rgba(255, 255, 255, 0.4);
    position: relative;
    overflow: hidden;
  }
  
  /* 修改激活项的光晕效果 */
  .menu-item-active::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    width: 4px;
    height: 60%;
    /* 使用 !important 确保渐变色优先级 */
    background: linear-gradient(
      to bottom,
      var(--v-theme-primary) 0%,
      var(--v-theme-secondary) 100%
    ) !important;
    border-radius: 0 4px 4px 0;
    transform: translateY(-50%);
    /* 修改光晕颜色，使用主题色 */
    box-shadow: 0 0 15px var(--v-theme-primary);
  }
  
  /* 修改发光动画 */
  @keyframes glowingBar {
    0% { 
      box-shadow: 0 0 15px rgba(var(--v-theme-primary), 0.6);
      opacity: 0.8;
    }
    50% { 
      box-shadow: 0 0 20px rgba(var(--v-theme-primary), 0.8);
      opacity: 1;
    }
    100% { 
      box-shadow: 0 0 15px rgba(var(--v-theme-primary), 0.6);
      opacity: 0.8;
    }
  }
  
  .menu-item-active::after {
    animation: glowingBar 2s ease-in-out infinite;
  }
  
  /* 修改收缩状态下激活项的样式 */
  .drawer-rail .menu-item-active {
    background: rgba(255, 255, 255, 0.35);
    transform: scale(1.1);
  }
  
  .drawer-rail .menu-item-active::after {
    display: none;
  }
  
  /* 添加激活项的内容样式 */
  .menu-item-active :deep(.v-list-item__content) {
    font-weight: 600;
    letter-spacing: 0.5px;
  }
  
  /* 添加激活项的图标样式 */
  .menu-item-active :deep(.iconify) {
    filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.6));
    transform: scale(1.15);
  }
  
  /* 添加激活项的悬浮效果 */
  .menu-item-active:hover {
    background: rgba(255, 255, 255, 0.4);
    transform: translateX(4px);
  }
  
  .drawer-rail .menu-item-active:hover {
    transform: scale(1.15);
  }
  
  /* 添加脉冲动画效果 */
  @keyframes activeItemPulse {
    0% { box-shadow: 0 4px 20px rgba(255, 255, 255, 0.25); }
    50% { box-shadow: 0 4px 25px rgba(255, 255, 255, 0.4); }
    100% { box-shadow: 0 4px 20px rgba(255, 255, 255, 0.25); }
  }
  
  .menu-item-active {
    animation: activeItemPulse 2s infinite;
  }
  
  /* 收缩状态下的菜单项样式 */
  .drawer-rail .menu-item {
    border-radius: 20px;
    width: 52px;
    height: 52px;
    margin: 8px auto;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.15);
    border: 1px solid rgba(255, 255, 255, 0.2);
  }
  
  .drawer-rail .menu-item:hover {
    transform: scale(1.1);
    background: rgba(255, 255, 255, 0.3);
    box-shadow: 0 4px 15px rgba(0, 184, 148, 0.25);
  }
  
  .rail-list :deep(.v-list-item__prepend) {
    margin-inline-start: 0;
    min-width: 48px;
    justify-content: center;
  }
  
  .rail-list :deep(.v-list-item) {
    padding: 0;
  }
  
  .menu-item-rail :deep(.v-list-item__content) {
    opacity: 0;
    width: 0;
    padding: 0;
    margin: 0;
    transition: all 0.3s ease;
  }
  
  .rail-list :deep(.v-list-item__prepend > .v-icon) {
    font-size: 22px;
    margin-inline-end: 0;
  }
  
  /* 图标样式优化 */
  :deep(.iconify) {
    width: 24px;
    height: 24px;
    transition: all 0.3s ease;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
    stroke-width: 1.5px;
  }
  
  .menu-item:hover :deep(.iconify) {
    transform: scale(1.15);
    filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.15));
  }
  
  .menu-item-active :deep(.iconify) {
    filter: drop-shadow(0 4px 8px rgba(255, 255, 255, 0.25));
    transform: scale(1.1);
  }
  
  /* 列表项内边距调整 */
  .v-navigation-drawer :deep(.v-list-item__prepend) {
    padding-inline-start: 12px;
  }
  
  .v-navigation-drawer :deep(.v-list-item__append) {
    padding-inline-end: 12px;
  }
  
  .v-navigation-drawer :deep(.v-list-item) {
    padding-inline: 16px;
  }
  
  /* 动画效果 */
  @keyframes fadeIn {
    from { opacity: 0; transform: translateX(-20px); }
    to { opacity: 1; transform: translateX(0); }
  }
  
  @keyframes pulse {
    0% { transform: scale(1); }
    50% { transform: scale(1.05); }
    100% { transform: scale(1); }
  }
  
  :deep(.v-list-item__content) {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    transition: all 0.3s ease;
  }
  
  .drawer-rail :deep(.v-list-item__content) {
    opacity: 0;
    width: 0;
    padding: 0;
    margin: 0;
  }
  </style> 