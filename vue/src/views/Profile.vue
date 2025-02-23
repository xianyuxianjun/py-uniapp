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
                  :src="userProfile.avatar"
                  :alt="userProfile.name"
                  cover
                >
                  <template v-slot:placeholder>
                    <div class="d-flex align-center justify-center fill-height" :style="{ backgroundColor: userProfile.avatarColor }">
                      <span class="text-h3 font-weight-medium white--text">
                        {{ userProfile.name.charAt(0) }}
                      </span>
                    </div>
                  </template>
                </v-img>
              </v-avatar>
              <h2 class="text-h5 font-weight-bold mb-2">{{ userProfile.name }}</h2>
              <v-chip
                :color="getRoleColor(userProfile.role)"
                size="small"
                class="text-caption mb-4"
              >
                {{ userProfile.role }}
              </v-chip>
              <p class="text-body-2 text-medium-emphasis">
                {{ userProfile.bio }}
              </p>
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
                :title="userProfile.phone"
                subtitle="电话"
              ></v-list-item>
              <v-list-item
                prepend-icon="mdi-map-marker"
                :title="userProfile.location"
                subtitle="所在地"
              ></v-list-item>
            </v-list>
          </v-card>
        </v-col>

        <!-- 右侧内容区域 -->
        <v-col cols="12" md="8">
          <!-- 编辑资料卡片 -->
          <v-card class="rounded-lg mb-6">
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
                  <v-col cols="12">
                    <div class="text-subtitle-1 font-weight-medium mb-4 primary--text">
                      基本信息
                    </div>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model="editedProfile.name"
                      label="姓名"
                      variant="outlined"
                      density="comfortable"
                      prepend-inner-icon="mdi-account"
                      :rules="[v => !!v || '姓名不能为空']"
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-text-field
                      v-model="editedProfile.username"
                      label="用户名"
                      variant="outlined"
                      density="comfortable"
                      prepend-inner-icon="mdi-account-circle"
                      disabled
                    ></v-text-field>
                  </v-col>
                  <v-col cols="12">
                    <v-textarea
                      v-model="editedProfile.bio"
                      label="个人简介"
                      variant="outlined"
                      density="comfortable"
                      rows="3"
                      prepend-inner-icon="mdi-text"
                      :rules="[v => v.length <= 200 || '个人简介不能超过200字']"
                    ></v-textarea>
                  </v-col>

                  <!-- 联系方式 -->
                  <v-col cols="12">
                    <div class="text-subtitle-1 font-weight-medium mb-4 mt-2 primary--text">
                      联系方式
                    </div>
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
                  <v-col cols="12">
                    <v-text-field
                      v-model="editedProfile.location"
                      label="所在地"
                      variant="outlined"
                      density="comfortable"
                      prepend-inner-icon="mdi-map-marker"
                    ></v-text-field>
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
                        :src="editedProfile.avatar"
                        :alt="editedProfile.name"
                        cover
                      >
                        <template v-slot:placeholder>
                          <div class="d-flex align-center justify-center fill-height" :style="{ backgroundColor: editedProfile.avatarColor }">
                            <span class="text-h4 font-weight-medium white--text">
                              {{ editedProfile.name.charAt(0) }}
                            </span>
                          </div>
                        </template>
                      </v-img>
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
                      <v-btn
                        color="primary"
                        variant="outlined"
                        prepend-icon="mdi-palette"
                        @click="changeAvatarColor"
                      >
                        更换颜色
                      </v-btn>
                    </div>
                  </v-col>
                </v-row>
              </v-form>
            </v-card-text>
          </v-card>

          <!-- 安全设置卡片 -->
          <v-card class="rounded-lg">
            <v-card-title class="pa-6">
              <span class="text-h6">安全设置</span>
            </v-card-title>
            <v-card-text class="pa-6">
              <v-row>
                <v-col cols="12">
                  <v-btn
                    color="primary"
                    variant="outlined"
                    prepend-icon="mdi-lock"
                    class="mr-3"
                    @click="changePasswordDialog = true"
                  >
                    修改密码
                  </v-btn>
                  <v-btn
                    color="primary"
                    variant="outlined"
                    prepend-icon="mdi-cellphone-key"
                    @click="bindPhoneDialog = true"
                  >
                    绑定手机
                  </v-btn>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- 修改密码对话框 -->
    <v-dialog v-model="changePasswordDialog" max-width="500px">
      <v-card class="rounded-lg">
        <v-card-title class="text-h6 pa-6">修改密码</v-card-title>
        <v-card-text class="pa-6">
          <v-form ref="passwordForm" v-model="passwordValid">
            <v-text-field
              v-model="passwordForm.oldPassword"
              label="当前密码"
              type="password"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-lock"
              :rules="[v => !!v || '请输入当前密码']"
            ></v-text-field>
            <v-text-field
              v-model="passwordForm.newPassword"
              label="新密码"
              type="password"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-lock-plus"
              :rules="[
                v => !!v || '请输入新密码',
                v => v.length >= 8 || '密码长度至少为8位',
                v => /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(v) || '密码必须包含大小写字母和数字'
              ]"
            ></v-text-field>
            <v-text-field
              v-model="passwordForm.confirmPassword"
              label="确认新密码"
              type="password"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-lock-check"
              :rules="[
                v => !!v || '请确认新密码',
                v => v === passwordForm.newPassword || '两次输入的密码不一致'
              ]"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions class="pa-6">
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="outlined"
            @click="changePasswordDialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            class="ml-3"
            :loading="loading"
            @click="changePassword"
          >
            确认修改
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 绑定手机对话框 -->
    <v-dialog v-model="bindPhoneDialog" max-width="500px">
      <v-card class="rounded-lg">
        <v-card-title class="text-h6 pa-6">绑定手机</v-card-title>
        <v-card-text class="pa-6">
          <v-form ref="phoneForm" v-model="phoneValid">
            <v-text-field
              v-model="phoneForm.phone"
              label="手机号码"
              variant="outlined"
              density="comfortable"
              prepend-inner-icon="mdi-cellphone"
              :rules="[
                v => !!v || '请输入手机号码',
                v => /^1[3-9]\d{9}$/.test(v) || '请输入有效的手机号码'
              ]"
            ></v-text-field>
            <div class="d-flex align-center">
              <v-text-field
                v-model="phoneForm.code"
                label="验证码"
                variant="outlined"
                density="comfortable"
                prepend-inner-icon="mdi-numeric"
                class="flex-grow-1 mr-3"
                :rules="[v => !!v || '请输入验证码']"
              ></v-text-field>
              <v-btn
                color="primary"
                variant="outlined"
                :disabled="!canSendCode"
                @click="sendCode"
              >
                {{ codeButtonText }}
              </v-btn>
            </div>
          </v-form>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions class="pa-6">
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="outlined"
            @click="bindPhoneDialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            variant="elevated"
            class="ml-3"
            :loading="loading"
            @click="bindPhone"
          >
            确认绑定
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

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
import { ref, computed } from 'vue'

// 用户资料
const userProfile = ref({
  username: 'admin',
  name: '管理员',
  email: 'admin@example.com',
  phone: '13800138000',
  role: '管理员',
  bio: '热爱动物，致力于为流浪动物提供更好的生活。',
  location: '北京市朝阳区',
  avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin&backgroundColor=b6e3f4',
  avatarColor: '#7E57C2'
})

// 编辑表单
const editedProfile = ref({ ...userProfile.value })
const form = ref(null)
const valid = ref(true)

// 修改密码
const changePasswordDialog = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordValid = ref(true)

// 绑定手机
const bindPhoneDialog = ref(false)
const phoneForm = ref({
  phone: '',
  code: ''
})
const phoneValid = ref(true)
const countdown = ref(0)
const canSendCode = computed(() => countdown.value === 0)
const codeButtonText = computed(() => countdown.value > 0 ? `${countdown.value}秒后重试` : '发送验证码')

// 状态变量
const loading = ref(false)
const snackbar = ref({
  show: false,
  text: '',
  color: 'success'
})

// 获取角色对应的颜色
const getRoleColor = (role) => {
  const colors = {
    '管理员': 'deep-purple',
    '工作人员': 'indigo',
    '志愿者': 'cyan-darken-1',
    '普通用户': 'grey'
  }
  return colors[role] || 'grey'
}

// 保存个人资料
const saveProfile = async () => {
  if (!form.value.validate()) return

  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    Object.assign(userProfile.value, editedProfile.value)
    showSnackbar('个人资料已更新', 'success')
  } catch (error) {
    showSnackbar('保存失败，请重试', 'error')
  } finally {
    loading.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordForm.value.validate()) return

  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSnackbar('密码修改成功', 'success')
    changePasswordDialog.value = false
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (error) {
    showSnackbar('密码修改失败，请重试', 'error')
  } finally {
    loading.value = false
  }
}

// 发送验证码
const sendCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(phoneForm.value.phone)) {
    showSnackbar('请输入有效的手机号码', 'error')
    return
  }

  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value === 0) {
        clearInterval(timer)
      }
    }, 1000)
    showSnackbar('验证码已发送', 'success')
  } catch (error) {
    showSnackbar('验证码发送失败，请重试', 'error')
  } finally {
    loading.value = false
  }
}

// 绑定手机
const bindPhone = async () => {
  if (!phoneForm.value.validate()) return

  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    showSnackbar('手机绑定成功', 'success')
    bindPhoneDialog.value = false
    phoneForm.value = { phone: '', code: '' }
  } catch (error) {
    showSnackbar('手机绑定失败，请重试', 'error')
  } finally {
    loading.value = false
  }
}

// 重新生成头像
const regenerateAvatar = () => {
  editedProfile.value.avatar = `https://api.dicebear.com/7.x/avataaars/svg?seed=${editedProfile.value.username}-${Date.now()}&backgroundColor=${encodeURIComponent(editedProfile.value.avatarColor || getRandomColor())}`
}

// 更换头像背景色
const changeAvatarColor = () => {
  const colors = ['b6e3f4', 'c0aede', 'ffd5dc', 'd1f4d9', 'f9d6ac']
  editedProfile.value.avatarColor = `#${colors[Math.floor(Math.random() * colors.length)]}`
  regenerateAvatar()
}

// 显示提示信息
const showSnackbar = (text, color = 'success') => {
  snackbar.value = {
    show: true,
    text,
    color
  }
}
</script>

<style scoped>
.profile {
  padding: 16px;
}

.profile-card {
  position: sticky;
  top: 24px;
}

.profile-header {
  background: linear-gradient(145deg, rgba(0, 184, 148, 0.05), rgba(0, 206, 201, 0.05));
}

.profile-avatar {
  border: 4px solid rgba(0, 184, 148, 0.2);
  transition: all 0.3s ease;
}

.profile-avatar:hover {
  border-color: rgba(0, 184, 148, 0.4);
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.2);
}

.edit-avatar {
  border: 3px solid rgba(0, 184, 148, 0.2);
  transition: all 0.3s ease;
}

.edit-avatar:hover {
  border-color: rgba(0, 184, 148, 0.4);
  transform: scale(1.05);
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.2);
}

/* 表单样式 */
.v-text-field :deep(.v-field--variant-outlined .v-field__outline__start),
.v-select :deep(.v-field--variant-outlined .v-field__outline__start) {
  border-radius: 12px 0 0 12px;
}

.v-text-field :deep(.v-field--variant-outlined .v-field__outline__end),
.v-select :deep(.v-field--variant-outlined .v-field__outline__end) {
  border-radius: 0 12px 12px 0;
}

.v-text-field :deep(.v-field--variant-outlined:hover .v-field__outline__start),
.v-select :deep(.v-field--variant-outlined:hover .v-field__outline__start),
.v-text-field :deep(.v-field--variant-outlined:hover .v-field__outline__end),
.v-select :deep(.v-field--variant-outlined:hover .v-field__outline__end) {
  border-color: rgba(0, 184, 148, 0.5);
}

/* 卡片样式 */
.v-card {
  transition: all 0.3s ease;
}

.v-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 184, 148, 0.1) !important;
}

/* 按钮样式 */
.v-btn {
  text-transform: none;
  letter-spacing: 0.5px;
}

/* 动画效果 */
.v-dialog-transition-enter-active,
.v-dialog-transition-leave-active {
  transition: all 0.3s ease;
}

.v-dialog-transition-enter-from,
.v-dialog-transition-leave-to {
  transform: scale(0.9);
  opacity: 0;
}
</style> 