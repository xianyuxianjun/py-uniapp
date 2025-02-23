<template>
  <v-snackbar
    v-model="show"
    :color="type"
    :timeout="timeout"
    location="top"
    class="app-message"
    :style="style"
  >
    <div class="d-flex align-center">
      <!-- 消息图标 -->
      <v-icon
        :icon="getIcon"
        size="20"
        class="mr-2"
      ></v-icon>
      
      <!-- 消息内容 -->
      <span class="message-text">{{ text }}</span>
      
      <!-- 关闭按钮 -->
      <v-btn
        variant="text"
        icon="mdi-close"
        size="small"
        @click="show = false"
        class="ml-auto close-btn"
      ></v-btn>
    </div>
  </v-snackbar>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

// 定义props
const props = defineProps({
  // 消息文本
  text: {
    type: String,
    required: true
  },
  // 消息类型: success, info, warning, error
  type: {
    type: String,
    default: 'success',
    validator: (value) => ['success', 'info', 'warning', 'error'].includes(value)
  },
  // 显示时长(ms)
  timeout: {
    type: Number,
    default: 3000
  },
  // 销毁回调
  onDestroy: {
    type: Function,
    default: () => {}
  }
})

// 控制显示状态
const show = ref(true)

// 监听显示状态变化
watch(show, (newVal) => {
  if (!newVal) {
    // 延迟调用销毁回调，等待过渡动画结束
    setTimeout(() => {
      props.onDestroy()
    }, 300)
  }
})

// 根据类型计算图标
const getIcon = computed(() => {
  const icons = {
    success: 'mdi-check-circle',
    info: 'mdi-information',
    warning: 'mdi-alert',
    error: 'mdi-alert-circle'
  }
  return icons[props.type]
})

// 根据类型计算样式
const style = computed(() => {
  const colors = {
    success: 'linear-gradient(145deg, #2ECC71, #27AE60)',
    info: 'linear-gradient(145deg, #3498DB, #2980B9)',
    warning: 'linear-gradient(145deg, #F1C40F, #F39C12)',
    error: 'linear-gradient(145deg, #E74C3C, #C0392B)'
  }
  return {
    '--message-bg': colors[props.type]
  }
})
</script>

<style scoped>
.app-message {
  margin-top: 20px !important;
}

:deep(.v-snackbar__wrapper) {
  min-width: 300px !important;
  max-width: 500px !important;
  padding: 12px 16px !important;
  background: var(--message-bg) !important;
  border-radius: 8px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
  transform-origin: top center;
  animation: message-slide-down 0.3s ease;
}

:deep(.v-snackbar__content) {
  padding: 0 !important;
}

.message-text {
  font-size: 14px;
  line-height: 1.4;
  font-weight: 500;
  word-break: break-word;
}

.close-btn {
  opacity: 0.7;
  transition: opacity 0.3s ease;
  margin-left: 12px !important;
}

.close-btn:hover {
  opacity: 1;
}

@keyframes message-slide-down {
  from {
    transform: translateY(-20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style> 