# 创建系统设置页面
<template>
  <div class="settings">
    <!-- 设置卡片 -->
    <v-card class="mb-6 rounded-lg">
      <v-card-title class="text-h5 font-weight-bold pa-6">
        <Icon icon="tabler:settings" class="mr-2" />
        系统设置
      </v-card-title>

      <v-card-text>
        <v-container>
          <!-- 主题设置 -->
          <v-row>
            <v-col cols="12" sm="6">
              <v-card variant="outlined" class="rounded-lg">
                <v-card-title class="text-subtitle-1">
                  <Icon icon="tabler:palette" class="mr-2" />
                  主题设置
                </v-card-title>
                <v-card-text>
                  <v-switch
                    v-model="darkMode"
                    label="夜间模式"
                    color="primary"
                    hide-details
                    class="mb-4"
                  >
                    <template v-slot:prepend>
                      <Icon :icon="darkMode ? 'tabler:moon' : 'tabler:sun'" />
                    </template>
                  </v-switch>

                  <v-select
                    v-model="selectedTheme"
                    :items="themeOptions"
                    label="主题"
                    variant="outlined"
                    density="comfortable"
                    class="mb-4"
                  >
                    <template v-slot:prepend-inner>
                      <Icon icon="tabler:color-swatch" />
                    </template>
                  </v-select>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- 通知设置 -->
            <v-col cols="12" sm="6">
              <v-card variant="outlined" class="rounded-lg">
                <v-card-title class="text-subtitle-1">
                  <Icon icon="tabler:bell" class="mr-2" />
                  通知设置
                </v-card-title>
                <v-card-text>
                  <v-switch
                    v-model="emailNotification"
                    label="邮件通知"
                    color="primary"
                    hide-details
                    class="mb-4"
                  >
                    <template v-slot:prepend>
                      <Icon icon="tabler:mail" />
                    </template>
                  </v-switch>

                  <v-switch
                    v-model="smsNotification"
                    label="短信通知"
                    color="primary"
                    hide-details
                    class="mb-4"
                  >
                    <template v-slot:prepend>
                      <Icon icon="tabler:message" />
                    </template>
                  </v-switch>
                </v-card-text>
              </v-card>
            </v-col>

            <!-- 系统信息 -->
            <v-col cols="12">
              <v-card variant="outlined" class="rounded-lg">
                <v-card-title class="text-subtitle-1">
                  <Icon icon="tabler:info-circle" class="mr-2" />
                  系统信息
                </v-card-title>
                <v-card-text>
                  <v-row>
                    <v-col cols="12" sm="6" md="4">
                      <div class="text-subtitle-2 mb-1">系统版本</div>
                      <div class="text-body-2 mb-4">v1.0.0</div>
                      
                      <div class="text-subtitle-2 mb-1">最后更新</div>
                      <div class="text-body-2">2024-01-15</div>
                    </v-col>
                    
                    <v-col cols="12" sm="6" md="4">
                      <div class="text-subtitle-2 mb-1">服务器状态</div>
                      <v-chip
                        color="success"
                        size="small"
                        class="mb-4"
                      >
                        运行中
                      </v-chip>
                      
                      <div class="text-subtitle-2 mb-1">数据库状态</div>
                      <v-chip
                        color="success"
                        size="small"
                      >
                        已连接
                      </v-chip>
                    </v-col>
                    
                    <v-col cols="12" sm="6" md="4">
                      <div class="text-subtitle-2 mb-1">系统负载</div>
                      <v-progress-linear
                        model-value="35"
                        color="primary"
                        height="8"
                        rounded
                        class="mb-4"
                      ></v-progress-linear>
                      
                      <div class="text-subtitle-2 mb-1">内存使用</div>
                      <v-progress-linear
                        model-value="60"
                        color="primary"
                        height="8"
                        rounded
                      ></v-progress-linear>
                    </v-col>
                  </v-row>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
      </v-card-text>

      <!-- 保存按钮 -->
      <v-card-actions class="pa-6">
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          variant="flat"
          @click="saveSettings"
        >
          <Icon icon="tabler:device-floppy" class="mr-2" />
          保存设置
        </v-btn>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useThemeStore } from '@/stores/theme'

const themeStore = useThemeStore()

// 主题设置
const darkMode = ref(themeStore.isDark)
const selectedTheme = ref(themeStore.currentTheme)

// 主题选项
const themeOptions = [
  { title: '默认主题', value: 'default' },
  { title: '蓝色主题', value: 'blue' },
  { title: '绿色主题', value: 'green' },
  { title: '紫色主题', value: 'purple' },
  { title: '橙色主题', value: 'orange' }
]

// 通知设置
const emailNotification = ref(true)
const smsNotification = ref(false)

// 监听主题变化
watch(darkMode, (value) => {
  themeStore.toggleDark(value)
})

watch(selectedTheme, (value) => {
  themeStore.changeTheme(value)
})

// 保存设置
const saveSettings = () => {
  // 保存主题设置
  themeStore.toggleDark(darkMode.value)
  themeStore.changeTheme(selectedTheme.value)

  // 保存通知设置
  const settings = {
    emailNotification: emailNotification.value,
    smsNotification: smsNotification.value
  }
  localStorage.setItem('notification-settings', JSON.stringify(settings))
  
  // 显示成功提示
  alert('设置已保存')
}
</script>

<style scoped>
.settings {
  padding: 16px;
}

/* 主题预览 */
.theme-preview {
  background: rgba(var(--v-theme-primary), 0.05);
  border: 1px solid rgba(var(--v-theme-primary), 0.1);
  border-radius: 8px;
}

.color-block {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.color-block:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.color-block.primary {
  background-color: rgb(var(--v-theme-primary));
}

.color-block.secondary {
  background-color: rgb(var(--v-theme-secondary));
}

.color-block.accent {
  background-color: rgb(var(--v-theme-accent));
}

/* 卡片样式 */
:deep(.v-card) {
  transition: all 0.3s ease;
  border: 1px solid rgba(var(--v-theme-primary), 0.1);
}

:deep(.v-card:hover) {
  border-color: rgba(var(--v-theme-primary), 0.2);
  box-shadow: 0 4px 15px rgba(var(--v-theme-primary), 0.1) !important;
}

/* 开关样式 */
:deep(.v-switch) {
  margin-bottom: 16px;
}

:deep(.v-switch__track) {
  opacity: 0.2;
}

:deep(.v-switch__thumb) {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 选择器样式 */
:deep(.v-select) {
  .v-field__input {
    padding: 8px 16px;
    background: rgba(var(--v-theme-primary), 0.05);
    border-radius: 8px;
  }
  
  .v-field__input:hover {
    background: rgba(var(--v-theme-primary), 0.08);
  }
  
  .v-field__input:focus {
    background: white;
    box-shadow: 0 0 0 1px rgb(var(--v-theme-primary));
  }
}

/* 按钮样式 */
:deep(.v-btn) {
  text-transform: none;
  letter-spacing: 0;
  font-weight: 500;
  border-radius: 8px;
  
  &.v-btn--variant-flat {
    background: linear-gradient(45deg, rgb(var(--v-theme-primary)), rgb(var(--v-theme-primary-darken-1)));
    box-shadow: 0 2px 4px rgba(var(--v-theme-primary), 0.2);
    
    &:hover {
      box-shadow: 0 4px 8px rgba(var(--v-theme-primary), 0.3);
      transform: translateY(-1px);
    }
  }
}

/* 进度条样式 */
:deep(.v-progress-linear) {
  border-radius: 4px;
  overflow: hidden;
  
  .v-progress-linear__background {
    opacity: 0.2;
  }
  
  .v-progress-linear__determinate {
    background: linear-gradient(45deg, rgb(var(--v-theme-primary)), rgb(var(--v-theme-primary-darken-1)));
  }
}
</style> 