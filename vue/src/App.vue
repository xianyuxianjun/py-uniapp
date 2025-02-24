<template>
  <!-- 根据路由meta信息选择布局 -->
  <component :is="route.meta.layout === 'blank' ? BlankLayout : MainLayout">
    <router-view></router-view>
  </component>
</template>

<script setup>
import { useRoute } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import BlankLayout from '@/layouts/BlankLayout.vue'

const route = useRoute()
</script>

<style>
/* 全局字体设置 */
:root {
  --font-family-sans: -apple-system, "PingFang SC", "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif;
  --font-family-heading: "PingFang SC", "Microsoft YaHei", -apple-system, "Helvetica Neue", Helvetica, Arial, sans-serif;
}

html {
  font-family: var(--font-family-sans);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 标题字体 */
.v-application .text-h1,
.v-application .text-h2,
.v-application .text-h3,
.v-application .text-h4,
.v-application .text-h5,
.v-application .text-h6 {
  font-family: var(--font-family-heading) !important;
  letter-spacing: -0.02em;
}

/* 正文字体 */
.v-application .text-body-1,
.v-application .text-body-2,
.v-application .text-subtitle-1,
.v-application .text-subtitle-2 {
  font-family: var(--font-family-sans) !important;
  letter-spacing: 0.01em;
}
</style>
