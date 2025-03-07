<template>
  <v-app-bar 
    elevation="0" 
    class="app-bar-blur"
  >
    <v-app-bar-title class="text-primary font-weight-bold">
      {{ title }}
    </v-app-bar-title>
    <v-spacer></v-spacer>
    
    <!-- 顶部工具栏按钮 -->
    <v-btn 
      icon 
      class="mr-2 notification-btn"
      elevation="1"
      color="primary"
    >
      <v-badge
        dot
        color="error"
        class="notification-badge"
      >
        <Icon icon="tabler:bell" width="28" height="28" />
      </v-badge>
    </v-btn>
    
    <v-menu
      v-model="profileMenu"
      :close-on-content-click="false"
      location="bottom end"
      offset="10"
    >
      <template v-slot:activator="{ props }">
        <v-btn 
          icon 
          class="ml-2 profile-btn"
          elevation="1"
          color="primary"
          v-bind="props"
        >
          <Icon icon="tabler:user" width="28" height="28" />
        </v-btn>
      </template>

      <v-card min-width="200" class="rounded-lg">
        <v-list>
          <v-list-item
            v-slot:prepend
            title="个人信息"
            @click="handleProfileClick"
          >
            <Icon icon="tabler:user" width="28" height="28" />
          </v-list-item>
          <v-divider></v-divider>
          <v-list-item
            v-slot:prepend
            title="退出登录"
            @click="handleLogout"
          >
            <Icon icon="tabler:logout" width="28" height="28" />
          </v-list-item>
        </v-list>
      </v-card>
    </v-menu>
  </v-app-bar>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Icon } from '@iconify/vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  }
})

const router = useRouter()
const profileMenu = ref(false)

const handleProfileClick = () => {
  router.push('/profile')
  profileMenu.value = false
}

const handleLogout = () => {
  // 清除用户信息
  localStorage.removeItem('userInfo')
  localStorage.removeItem('userId')
  // 使用路由跳转到登录页
  router.push('/login')
  profileMenu.value = false
}
</script>

<style scoped>
.app-bar-blur {
  backdrop-filter: blur(12px);
  background: rgba(255, 255, 255, 0.85) !important;
  border-bottom: 1px solid rgba(0, 184, 148, 0.1);
}

.notification-btn, .profile-btn {
  transition: all 0.3s ease;
  background: linear-gradient(145deg, rgba(0, 184, 148, 0.1), rgba(0, 206, 201, 0.1));
  border-radius: 16px;
  border: 1px solid rgba(0, 184, 148, 0.1);
}

.notification-btn:hover, .profile-btn:hover {
  transform: scale(1.1) rotate(5deg);
  background: linear-gradient(145deg, rgba(0, 184, 148, 0.2), rgba(0, 206, 201, 0.2));
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.2);
}

.notification-badge {
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}

:deep(.iconify) {
  width: 28px;
  height: 28px;
}

.notification-btn :deep(.iconify),
.profile-btn :deep(.iconify) {
  width: 28px;
  height: 28px;
}
</style> 