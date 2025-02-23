<template>
  <v-dialog
    :model-value="modelValue"
    :max-width="maxWidth"
    :persistent="persistent"
    @update:model-value="$emit('update:modelValue', $event)"
    class="app-dialog"
    :transition="transition"
  >
    <v-card class="rounded-lg dialog-card">
      <!-- 标题栏 -->
      <v-card-title class="dialog-title pa-4">
        <div class="d-flex align-center">
          <!-- 标题图标 -->
          <v-avatar
            size="32"
            :color="mode === 'edit' ? 'warning' : 'primary'"
            class="mr-3 title-icon"
          >
            <Icon 
              :icon="mode === 'edit' ? 'tabler:edit' : 'tabler:plus'" 
              width="20" 
              height="20"
            />
          </v-avatar>
          
          <!-- 标题文本 -->
          <span class="text-h6">{{ title }}</span>
        </div>

        <!-- 关闭按钮 -->
        <v-btn
          v-if="!persistent"
          icon
          variant="text"
          size="small"
          @click="handleClose"
          class="close-btn"
        >
          <Icon icon="tabler:x" width="20" height="20" />
        </v-btn>
      </v-card-title>

      <v-divider></v-divider>

      <!-- 内容区域 -->
      <v-card-text class="dialog-content pa-6">
        <slot></slot>
      </v-card-text>

      <!-- 操作按钮区域 -->
      <v-card-actions class="dialog-actions pa-4">
        <v-spacer></v-spacer>
        
        <!-- 取消按钮 -->
        <v-btn
          variant="outlined"
          color="grey"
          @click="handleClose"
          class="action-btn mr-3"
          :disabled="loading"
        >
          取消
        </v-btn>

        <!-- 确认按钮 -->
        <v-btn
          color="primary"
          @click="handleConfirm"
          class="action-btn"
          :loading="loading"
          :disabled="loading"
        >
          {{ confirmText }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { Icon } from '@iconify/vue'

// 定义组件属性
const props = defineProps({
  // 控制对话框显示/隐藏
  modelValue: {
    type: Boolean,
    required: true
  },
  // 对话框标题
  title: {
    type: String,
    required: true
  },
  // 对话框模式：新增/编辑
  mode: {
    type: String,
    default: 'add',
    validator: (value) => ['add', 'edit'].includes(value)
  },
  // 确认按钮文本
  confirmText: {
    type: String,
    default: '确认'
  },
  // 最大宽度
  maxWidth: {
    type: [String, Number],
    default: 600
  },
  // 是否显示加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 是否阻止点击外部关闭
  persistent: {
    type: Boolean,
    default: false
  },
  // 过渡动画
  transition: {
    type: String,
    default: 'dialog-transition'
  }
})

// 定义事件
const emit = defineEmits([
  'update:modelValue',
  'close',
  'confirm'
])

// 处理关闭
const handleClose = () => {
  emit('close')
  emit('update:modelValue', false)
}

// 处理确认
const handleConfirm = () => {
  emit('confirm')
}
</script>

<style scoped>
.app-dialog {
  backdrop-filter: blur(10px);
}

.dialog-card {
  overflow: hidden;
  border: 1px solid rgb(var(--v-theme-primary), 0.1);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  background: rgb(var(--v-theme-surface)) !important;
}

.dialog-title {
  position: relative;
  background: linear-gradient(145deg, 
    rgba(var(--v-theme-primary), 0.05), 
    rgba(var(--v-theme-primary), 0.1)
  );
  color: rgb(var(--v-theme-on-surface));
}

.title-icon {
  transition: all 0.3s ease;
}

.title-icon :deep(.v-avatar__content) {
  color: rgb(var(--v-theme-on-primary));
}

.close-btn {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  opacity: 0.7;
  transition: all 0.3s ease;
  color: rgb(var(--v-theme-on-surface));
}

.close-btn:hover {
  opacity: 1;
  transform: translateY(-50%) rotate(90deg);
  background: rgba(var(--v-theme-on-surface), 0.05);
}

.dialog-content {
  max-height: calc(90vh - 180px);
  overflow-y: auto;
  color: rgb(var(--v-theme-on-surface));
}

.dialog-content::-webkit-scrollbar {
  width: 8px;
}

.dialog-content::-webkit-scrollbar-track {
  background: rgba(var(--v-theme-primary), 0.05);
  border-radius: 4px;
}

.dialog-content::-webkit-scrollbar-thumb {
  background: rgba(var(--v-theme-primary), 0.2);
  border-radius: 4px;
}

.dialog-content::-webkit-scrollbar-thumb:hover {
  background: rgba(var(--v-theme-primary), 0.3);
}

.dialog-actions {
  background: rgba(var(--v-theme-surface-variant), 0.5);
  border-top: 1px solid rgba(var(--v-theme-on-surface), 0.05);
}

.action-btn {
  min-width: 100px;
  letter-spacing: 1px;
  text-transform: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(var(--v-theme-primary), 0.2);
}

/* 动画效果 */
:deep(.dialog-transition-enter-active),
:deep(.dialog-transition-leave-active) {
  transition: all 0.3s ease;
}

:deep(.dialog-transition-enter-from),
:deep(.dialog-transition-leave-to) {
  opacity: 0;
  transform: scale(0.9);
}

/* 夜间模式特定样式 */
:deep(.v-theme--dark) {
  .dialog-card {
    background: rgb(var(--v-theme-surface-variant)) !important;
    border-color: rgba(var(--v-theme-on-surface), 0.05);
  }

  .dialog-title {
    background: linear-gradient(145deg, 
      rgba(var(--v-theme-surface), 0.8), 
      rgba(var(--v-theme-surface-variant), 0.9)
    );
  }

  .close-btn:hover {
    background: rgba(var(--v-theme-on-surface), 0.1);
  }

  .dialog-content::-webkit-scrollbar-track {
    background: rgba(var(--v-theme-on-surface), 0.05);
  }

  .dialog-content::-webkit-scrollbar-thumb {
    background: rgba(var(--v-theme-on-surface), 0.1);
  }

  .dialog-content::-webkit-scrollbar-thumb:hover {
    background: rgba(var(--v-theme-on-surface), 0.15);
  }

  .dialog-actions {
    background: rgba(var(--v-theme-surface), 0.8);
    border-top-color: rgba(var(--v-theme-on-surface), 0.08);
  }

  .action-btn {
    &.v-btn--outlined {
      border-color: rgba(var(--v-theme-on-surface), 0.12);
      color: rgba(var(--v-theme-on-surface), 0.87);

      &:hover {
        border-color: rgba(var(--v-theme-on-surface), 0.2);
        background: rgba(var(--v-theme-on-surface), 0.05);
      }
    }
  }
}
</style> 