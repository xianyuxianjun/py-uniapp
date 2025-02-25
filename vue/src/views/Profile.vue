<template>
  <div class="profile">
    <v-container>
      <v-row>
        <!-- 左侧个人信息卡片 -->
        <v-col cols="12" md="4">
          <v-card class="profile-card rounded-lg">
            <div class="profile-header pa-6 text-center">
              <v-avatar size="120" class="profile-avatar mb-4">
                <v-img
                    :src="userProfile.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${userProfile.username}`"
                    :alt="userProfile.username"
                    cover
                >
                  <template v-slot:placeholder>
                    <div class="d-flex align-center justify-center fill-height">
                      <span class="text-h3 font-weight-medium white--text">
                        {{ userProfile.username?.charAt(0) }}
                      </span>
                    </div>
                  </template>
                </v-img>
              </v-avatar>
              <h2 class="text-h5 font-weight-bold mb-2">{{ userProfile.username }}</h2>
              <v-chip
                  :color="getRoleColor(userProfile.role)"
                  size="small"
                  class="text-caption mb-4"
              >
                {{ userProfile.role }}
              </v-chip>
            </div>
            <v-divider></v-divider>
            <v-list>
              <v-list-item
                  prepend-icon="mdi-email"
                  :title="userProfile.email"
                  subtitle="邮箱"
              ></v-list-item>
              <v-list-item
                  prepend-icon="mdi-phone"
                  :title="userProfile.phone || '未设置'"
                  subtitle="电话"
              ></v-list-item>
              <v-list-item
                  prepend-icon="mdi-gender-male-female"
                  :title="userProfile.gender || '未设置'"
                  subtitle="性别"
              ></v-list-item>
            </v-list>
          </v-card>
        </v-col>

        <!-- 右侧内容区域 -->
        <v-col cols="12" md="8">
          <!-- 编辑资料卡片 -->
          <v-card class="rounded-lg">
            <v-card-title class="d-flex align-center pa-6">
              <span class="text-h6">编辑个人资料</span>
              <v-spacer></v-spacer>
              <v-btn
                  color="primary"
                  variant="elevated"
                  :loading="loading"
                  @click="saveProfile"
                  prepend-icon="mdi-content-save"
              >
                保存更改
              </v-btn>
            </v-card-title>
            <v-card-text class="pa-6">
              <v-form ref="form" v-model="valid">
                <v-row>
                  <!-- 基本信息 -->
                  <v-col cols="12" sm="6">
                    <v-text-field
                        v-model="editedProfile.username"
                        label="用户名"
                        variant="outlined"
                        density="comfortable"
                        prepend-inner-icon="mdi-account"
                        disabled
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                        v-model="editedProfile.email"
                        label="邮箱"
                        variant="outlined"
                        density="comfortable"
                        prepend-inner-icon="mdi-email"
                        :rules="[
                        v => !!v || '邮箱不能为空',
                        v => /.+@.+\..+/.test(v) || '请输入有效的邮箱地址'
                      ]"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                        v-model="editedProfile.phone"
                        label="电话"
                        variant="outlined"
                        density="comfortable"
                        prepend-inner-icon="mdi-phone"
                        :rules="[v => !v || /^1[3-9]\d{9}$/.test(v) || '请输入有效的手机号码']"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-select
                        v-model="editedProfile.gender"
                        label="性别"
                        :items="['男', '女', '其他']"
                        variant="outlined"
                        density="comfortable"
                        prepend-inner-icon="mdi-gender-male-female"
                    ></v-select>
                  </v-col>

                  <!-- 头像设置 -->
                  <v-col cols="12">
                    <div class="text-subtitle-1 font-weight-medium mb-4 mt-2 primary--text">
                      头像设置
                    </div>
                  </v-col>
                  <v-col cols="12" class="d-flex align-center">
                    <v-avatar size="80" class="edit-avatar mr-6">
                      <v-img
                          :src="editedProfile.avatar || `https://api.dicebear.com/7.x/avataaars/svg?seed=${editedProfile.username}`"
                          :alt="editedProfile.username"
                          cover
                      ></v-img>
                    </v-avatar>
                    <div class="avatar-actions">
                      <v-btn
                          color="primary"
                          variant="outlined"
                          class="mr-3"
                          prepend-icon="mdi-refresh"
                          @click="regenerateAvatar"
                      >
                        重新生成头像
                      </v-btn>
                    </div>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- 操作提示 -->
    <v-snackbar
        v-model="snackbar.show"
        :color="snackbar.color"
        :timeout="3000"
    >
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn
            color="white"
            variant="text"
            @click="snackbar.show = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from 'vue'
import http from '@/utils/http'
import {message} from '@/utils/message'

// 用户资料
const userProfile = ref({
  id: '',
  username: '',
  email: '',
  phone: '',
  avatar: '',
  status: '',
  role: '',
  gender: ''
})

// 编辑表单
const editedProfile = ref({})
const form = ref(null)
const valid = ref(true)

// 状态变量
const loading = ref(false)
const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
})

// 获取用户资料
const fetchUserProfile = async () => {
  try {
    const userId = localStorage.getItem('userId')
    const res = await http.get('/auth/getProfile', {
      params: {id: userId}
    })

    if (res.data.code === 1) {
      userProfile.value = res.data.data
      editedProfile.value = {...res.data.data}
    } else {
      message.error('获取用户资料失败')
    }
  } catch (error) {
    console.error('获取用户资料失败:', error)
    message.error('获取用户资料失败，请稍后重试')
  }
}

// 保存个人资料
const saveProfile = async () => {
  if (!form.value.validate()) return

  loading.value = true
  try {
    const updateData = {
      id: userProfile.value.id,
      email: editedProfile.value.email,
      phone: editedProfile.value.phone,
      avatar: editedProfile.value.avatar,
      gender: editedProfile.value.gender
    }

    const res = await http.post('/auth/updateProfile', updateData)

    if (res.data.code === 1) {
      Object.assign(userProfile.value, editedProfile.value)
      message.success('个人资料已更新')
    } else {
      message.error(res.data.message || '更新失败')
    }
  } catch (error) {
    console.error('更新个人资料失败:', error)
    message.error('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

// 重新生成头像
const regenerateAvatar = () => {
  const newAvatar = `https://api.dicebear.com/7.x/avataaars/svg?seed=${editedProfile.value.username}-${Date.now()}`
  editedProfile.value.avatar = newAvatar
}

// 获取角色对应的颜色
const getRoleColor = (role) => {
  const colors = {
    'admin': 'deep-purple',
    'recycler': 'indigo',
    'user': 'grey'
  }
  return colors[role] || 'grey'
}

// 在组件挂载时获取用户资料
onMounted(() => {
  fetchUserProfile()
})
</script>

<style scoped>
/* 保持原有样式不变 */
</style>
