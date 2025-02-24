<script setup>
import http from '@/utils/http'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from '@/utils/message'

const router = useRouter()
const isLogin = ref(true) // 控制显示登录还是注册

// 表单数据
const loginForm = ref({
  email: '',
  passwordHash: '',
})

const registerForm = ref({
  username: '',
  email: '',
  passwordHash: '',
  confirmPasswordHash: ''
})    

const loading = ref(false)
const showPassword = ref(false)

// 表单验证规则
const rules = {
  email: [
    v => !!v || '邮箱不能为空',
    v => /.+@.+\..+/.test(v) || '请输入有效的邮箱地址'
  ],
  passwordHash: [
    v => !!v || '密码不能为空',
    v => v.length >= 6 || '密码长度不能小于6位'
  ],
  username: [v => !!v || '用户名不能为空'],
  confirmPasswordHash: [
    v => !!v || '确认密码不能为空',
    v => v === registerForm.value.passwordHash || '两次输入的密码不一致'
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginForm.value.email || !loginForm.value.passwordHash) {
    message.error('请填写完整的登录信息')
    return
  }

  loading.value = true
  try {
    const res = await http.post('/auth/login', loginForm.value)
    if (res.data.code === 1) {
      message.success('登录成功')
      localStorage.setItem('userId', res.data.data.id)
      router.push('/')
    } else {
      message.error(res.data.message || '登录失败')
    }
  } catch (error) {
    message.error('登录失败，请稍后重试')
  } finally {
    loading.value = falsei
  }
}

// 处理注册
const handleRegister = async () => {
  if (registerForm.value.passwordHash !== registerForm.value.confirmPasswordHash) {
    message.error('两次输入的密码不一致')
    return
  }

  loading.value = true
  try {
    const res = await http.post('/auth/register', {
      username: registerForm.value.username,
      email: registerForm.value.email,
      passwordHash: registerForm.value.passwordHash
    })
    
    if (res.data.code === 1) {
      message.success('注册成功，请登录')
      isLogin.value = true
      registerForm.value = {
        username: '',
        email: '',
        passwordHash: '',
        confirmPasswordHash: ''
      }
    } else {
      message.error(res.data.message || '注册失败')
    }
  } catch (error) {
    message.error('注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <!-- 左侧表单区域 -->
      <div class="card-side form" :class="{ 'move-right': !isLogin }">
        <h2>{{ !isLogin ? '注册' : '登录' }}</h2>
        
        <!-- 登录表单 -->
        <v-form v-if="isLogin" @submit.prevent="handleLogin" class="auth-form">
          <v-text-field
            v-model="loginForm.email"
            label="邮箱"
            type="email"
            :rules="rules.email"
            variant="outlined"
            bg-color="grey-lighten-4"
            density="comfortable"
            hide-details="auto"
            prepend-inner-icon="mdi-email-outline"
          ></v-text-field>

          <v-text-field
            v-model="loginForm.passwordHash"
            label="密码"
            :type="showPassword ? 'text' : 'password'"
            :rules="rules.passwordHash"
            variant="outlined"
            bg-color="grey-lighten-4"
            density="comfortable"
            hide-details="auto"
            prepend-inner-icon="mdi-lock-outline"
            :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            @click:append-inner="showPassword = !showPassword"
          ></v-text-field>

          <v-btn
            color="primary"
            type="submit"
            block
            :loading="loading"
            size="large"
          >
            登录
          </v-btn>
        </v-form>

        <!-- 注册表单 -->
        <v-form v-else @submit.prevent="handleRegister" class="auth-form">
          <v-text-field
            v-model="registerForm.username"
            label="用户名"
            :rules="rules.username"
            variant="outlined"
            bg-color="grey-lighten-4"
            density="comfortable"
            hide-details="auto"
            prepend-inner-icon="mdi-account-outline"
          ></v-text-field>

          <v-text-field
            v-model="registerForm.email"
            label="邮箱"
            type="email"
            :rules="rules.email"
            variant="outlined"
            bg-color="grey-lighten-4"
            density="comfortable"
            hide-details="auto"
            prepend-inner-icon="mdi-email-outline"
          ></v-text-field>

          <v-text-field
            v-model="registerForm.passwordHash"
            label="密码"
            :type="showPassword ? 'text' : 'password'"
            :rules="rules.passwordHash"
            variant="outlined"
            bg-color="grey-lighten-4"
            density="comfortable"
            hide-details="auto"
            prepend-inner-icon="mdi-lock-outline"
            :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
            @click:append-inner="showPassword = !showPassword"
          ></v-text-field>

          <v-text-field
            v-model="registerForm.confirmPasswordHash"
            label="确认密码"
            :type="showPassword ? 'text' : 'password'"
            :rules="rules.confirmPasswordHash"
            variant="outlined"
            bg-color="grey-lighten-4"
            density="comfortable"
            hide-details="auto"
            prepend-inner-icon="mdi-lock-check-outline"
          ></v-text-field>

          <v-btn
            color="primary"
            type="submit"
            block
            :loading="loading"
            size="large"
          >
            注册
          </v-btn>
        </v-form>
      </div>

      <!-- 右侧信息区域 -->
      <div class="card-side info" :class="{ 'move-left': !isLogin }">
        <h2>{{ !isLogin ? '已有账号?' : '没有账号?' }}</h2>
        <p>{{ !isLogin ? '立即登录开始使用我们的服务' : '注册一个新账号开始体验' }}</p>
        <v-btn
          variant="outlined"
          color="white"
          @click="isLogin = !isLogin"
          class="switch-btn"
        >
          {{ !isLogin ? '登录' : '注册' }}
        </v-btn>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-size: 400% 400%;
  animation: gradientBG 15s ease infinite;
}

@keyframes gradientBG {
  0% { background-position: 0% 50% }
  50% { background-position: 100% 50% }
  100% { background-position: 0% 50% }
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  width: 900px;
  min-height: 550px;
  display: flex;
  position: relative;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transform: translateY(0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 45px rgba(0, 0, 0, 0.25);
}

.card-side {
  width: 50%;
  padding: 50px;
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.info {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  position: absolute;
  left: 0;
  height: 100%;
  background-image: 
    radial-gradient(circle at 50% 50%, rgba(255,255,255,0.1) 0%, transparent 60%),
    linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.info.move-left {
  transform: translateX(100%);
}

.form {
  background: white;
  position: absolute;
  right: 0;
}

.form.move-right {
  transform: translateX(-100%);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-top: 40px;
  max-width: 320px;
  margin-left: auto;
  margin-right: auto;
}

h2 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 24px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.info h2 {
  font-size: 36px;
  -webkit-text-fill-color: white;
  margin-bottom: 16px;
}

p {
  margin-bottom: 36px;
  opacity: 0.9;
  font-size: 16px;
  line-height: 1.6;
}

.switch-btn {
  border: 2px solid rgba(255, 255, 255, 0.8);
  font-weight: 500;
  padding: 12px 36px;
  font-size: 16px;
  border-radius: 30px;
  transition: all 0.3s ease;
  text-transform: none;
  letter-spacing: 0.5px;
}

.switch-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

:deep(.v-field) {
  border-radius: 12px;
  transition: all 0.3s ease;
  background: #f5f7fa !important;
}

:deep(.v-field:hover) {
  background: #eef1f6 !important;
}

:deep(.v-field--focused) {
  background: white !important;
  box-shadow: 0 0 0 1px #667eea;
}

:deep(.v-field__input) {
  padding: 12px 16px;
  font-size: 15px;
}

:deep(.v-field__prepend-inner) {
  opacity: 0.7;
  margin-right: 12px;
  color: #667eea;
}

:deep(.v-field__append-inner) {
  opacity: 0.7;
  margin-left: 12px;
  cursor: pointer;
  color: #667eea;
}

:deep(.v-label) {
  font-size: 15px;
  opacity: 0.8;
}

.v-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.v-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .login-card {
    width: 100%;
    min-height: 100vh;
    border-radius: 0;
    flex-direction: column;
  }

  .card-side {
    width: 100%;
    padding: 30px;
  }

  .info {
    position: relative;
    padding: 40px 20px;
  }

  .auth-form {
    margin-top: 20px;
  }

  h2 {
    font-size: 28px;
  }

  .info h2 {
    font-size: 32px;
  }

  .info.move-left {
    transform: translateY(-100%);
  }

  .form.move-right {
    transform: translateY(100%);
  }
}
</style> 