import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useTheme } from 'vuetify'
import { themePresets, getThemeConfig } from '../config/theme'

// 从 localStorage 获取保存的主题设置
const getSavedTheme = () => {
  const saved = localStorage.getItem('theme-settings')
  if (saved) {
    try {
      return JSON.parse(saved)
    } catch (e) {
      console.error('解析主题设置失败:', e)
    }
  }
  return { themeName: 'default', isDark: false }
}

export const useThemeStore = defineStore('theme', () => {
  const vuetifyTheme = useTheme()
  const savedTheme = getSavedTheme()
  
  const currentTheme = ref(savedTheme.themeName)
  const isDark = ref(savedTheme.isDark)

  // 初始化主题
  const initTheme = () => {
    toggleDark(isDark.value)
    changeTheme(currentTheme.value)
  }

  // 切换暗黑模式
  const toggleDark = (value) => {
    isDark.value = value
    vuetifyTheme.global.name.value = value ? 'dark' : 'light'
    saveThemeSettings()
  }

  // 切换主题色
  const changeTheme = (themeName) => {
    if (!themePresets[themeName]) return
    
    currentTheme.value = themeName
    const config = getThemeConfig(themeName)
    
    // 更新亮色主题
    Object.entries(config.themes.light.colors).forEach(([key, value]) => {
      vuetifyTheme.themes.value.light.colors[key] = value
    })
    
    // 更新暗色主题
    Object.entries(config.themes.dark.colors).forEach(([key, value]) => {
      vuetifyTheme.themes.value.dark.colors[key] = value
    })

    saveThemeSettings()
  }

  // 保存主题设置到 localStorage
  const saveThemeSettings = () => {
    const settings = {
      themeName: currentTheme.value,
      isDark: isDark.value
    }
    localStorage.setItem('theme-settings', JSON.stringify(settings))
  }

  // 初始化主题
  initTheme()

  return {
    currentTheme,
    isDark,
    toggleDark,
    changeTheme,
    saveThemeSettings
  }
}) 