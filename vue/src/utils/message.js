import { createApp } from 'vue'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import AppMessage from '@/components/common/AppMessage.vue'
import 'vuetify/styles'

// 消息队列
let messageQueue = []
const MAX_COUNT = 5

// 创建消息实例
function createMessage(options) {
  // 限制最大显示数量
  if (messageQueue.length >= MAX_COUNT) {
    const oldestMessage = messageQueue[0]
    oldestMessage.destroy()
    messageQueue.shift()
  }

  // 创建一个div容器
  const container = document.createElement('div')
  document.body.appendChild(container)

  // 创建 Vuetify 实例
  const vuetify = createVuetify({
    components,
    directives
  })

  // 创建消息实例
  const messageInstance = createApp(AppMessage, {
    ...options,
    onDestroy: () => {
      // 从队列中移除
      const index = messageQueue.findIndex(item => item.container === container)
      if (index > -1) {
        messageQueue.splice(index, 1)
      }

      // 销毁实例并移除DOM节点
      messageInstance.unmount()
      document.body.removeChild(container)

      // 调用用户传入的销毁回调
      if (options.onDestroy) {
        options.onDestroy()
      }
    }
  })

  // 使用 Vuetify
  messageInstance.use(vuetify)

  // 挂载实例
  messageInstance.mount(container)

  // 添加到队列
  messageQueue.push({
    container,
    destroy: () => {
      messageInstance.unmount()
      document.body.removeChild(container)
    }
  })
}

// 消息类型配置
const messageTypes = {
  success: {
    type: 'success',
    timeout: 3000
  },
  info: {
    type: 'info',
    timeout: 3000
  },
  warning: {
    type: 'warning',
    timeout: 4000
  },
  error: {
    type: 'error',
    timeout: 5000
  }
}

// 消息工具对象
export const message = {
  success(text, options = {}) {
    createMessage({
      text,
      ...messageTypes.success,
      ...options
    })
  },
  info(text, options = {}) {
    createMessage({
      text,
      ...messageTypes.info,
      ...options
    })
  },
  warning(text, options = {}) {
    createMessage({
      text,
      ...messageTypes.warning,
      ...options
    })
  },
  error(text, options = {}) {
    createMessage({
      text,
      ...messageTypes.error,
      ...options
    })
  },
  // 清除所有消息
  clear() {
    messageQueue.forEach(item => item.destroy())
    messageQueue = []
  }
} 