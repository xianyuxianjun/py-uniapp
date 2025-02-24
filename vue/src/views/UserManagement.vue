<script setup>
import http from '@/utils/http'
import { onMounted, ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { message } from '@/utils/message'
import { generateUserId } from '@/utils/idGenerator'

// 用户列表数据
const users = ref([])
// 控制添加/编辑对话框的显示
const dialog = ref(false)
// 当前编辑的用户数据
const editedUser = ref({
  username: '',
  email: '',
  phone: '',
  gender: '',
  role: 'user',
  status: 'active'
})
// 对话框标题
const dialogTitle = ref('')
// 是否是编辑模式
const isEdit = ref(false)
// 加载状态
const loading = ref(false)

// 添加搜索和筛选状态
const search = ref('')
const roleFilter = ref('all')
const statusFilter = ref('all')

// 角色选项
const roleOptions = [
  { title: '全部角色', value: 'all' },
  { title: '管理员', value: 'admin' },
  { title: '用户', value: 'user' },
  { title: '回收员', value: 'recycler' }
]

// 状态选项
const statusOptions = [
  { title: '全部状态', value: 'all' },
  { title: '启用', value: 'active' },
  { title: '禁用', value: 'inactive' }
]

// 计算过滤后的用户列表
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    // 搜索条件：用户名、邮箱或电话包含搜索关键词
    const searchMatch = search.value === '' || 
      user.username?.toLowerCase().includes(search.value.toLowerCase()) ||
      user.email?.toLowerCase().includes(search.value.toLowerCase()) ||
      user.phone?.includes(search.value)

    // 角色筛选
    const roleMatch = roleFilter.value === 'all' || user.role === roleFilter.value

    // 状态筛选
    const statusMatch = statusFilter.value === 'all' || user.status === statusFilter.value

    return searchMatch && roleMatch && statusMatch
  })
})

// 修改表格的 headers 配置
const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: '头像', key: 'avatar', sortable: false, align: 'center' },
  { title: '用户名', key: 'username', sortable: true },
  { title: '邮箱', key: 'email' },
  { title: '电话', key: 'phone' },
  { title: '性别', key: 'gender' },
  { title: '角色', key: 'role' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'actions', sortable: false }
]

// 获取用户列表
function getUsers() {
  loading.value = true
  http.get("/user/list").then(res => {
    users.value = res.data.data
    loading.value = false
  })
}

// 打开添加用户对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加用户'
  editedUser.value = {
    id: generateUserId(),
    username: '',
    email: '',
    phone: '',
    gender: '',
    role: 'user',
    status: 'active'
  }
  dialog.value = true
}

// 打开编辑用户对话框
function openEditDialog(user) {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  editedUser.value = { ...user }
  dialog.value = true
}

// 保存用户（添加/编辑）
function saveUser() {
  loading.value = true
  const request = isEdit.value
    ? http.post('/user/update', editedUser.value)
    : http.post('/user', editedUser.value)
    
  request.then(res => {
    if (res.data.code === 1) {
      dialog.value = false
      getUsers()
      message.success(isEdit.value ? '更新成功' : '添加成功')
    } else {
      message.error(res.data.message || (isEdit.value ? '更新失败' : '添加失败'))
    }
  }).catch(() => {
    message.error('操作失败，请稍后重试')
  }).finally(() => {
    loading.value = false
  })
}

// 删除用户
function deleteUser(id) {
  if (!window.confirm('确定要删除该用户吗？')) {
    return
  }
  
  loading.value = true
  http.delete(`/user/${id}`).then(res => {
    if (res.data.code === 1) {
      getUsers()
      message.success('删除成功')
    } else {
      message.error(res.data.message || '删除失败')
    }
  }).catch(() => {
    message.error('删除失败，请稍后重试')
  }).finally(() => {
    loading.value = false
  })
}

// 添加默认头像处理函数
function getDefaultAvatar(username) {
  return `https://ui-avatars.com/api/?name=${encodeURIComponent(username)}&background=random`
}

// 处理头像上传
async function handleAvatarUpload(event) {
  const file = event.target.files[0]
  if (!file) return
  
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    message.error('请上传图片文件')
    return
  }
  
  // 检查文件大小（限制为2MB）
  if (file.size > 2 * 1024 * 1024) {
    message.error('图片大小不能超过2MB')
    return
  }

  // 确保有有效的用户ID
  if (!editedUser.value.id) {
    message.error('用户ID无效')
    return
  }

  // 创建预览URL
  const previewUrl = URL.createObjectURL(file)
  // 先更新预览
  editedUser.value.avatar = previewUrl

  loading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('relatedId', String(editedUser.value.id))
    formData.append('relatedType', 'avatar')

    const res = await http.post('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.data.code === 1) {
      message.success('头像上传成功')
      // 不需要立即刷新列表，保持预览URL
      // getUsers() // 移除这行
    } else {
      message.error(res.data.message || '头像上传失败')
      // 上传失败时恢复原头像
      editedUser.value.avatar = null
    }
  } catch (error) {
    message.error('头像上传失败，请稍后重试')
    console.error('上传错误:', error)
    // 上传失败时恢复原头像
    editedUser.value.avatar = null
  } finally {
    loading.value = false
    // 清理预览URL，避免内存泄漏
    URL.revokeObjectURL(previewUrl)
  }
}

onMounted(() => {
  getUsers()
})
</script>

<template>
  <v-container>
    <!-- 顶部操作栏 -->
    <v-row class="mb-4">
      <v-col cols="12" sm="6" md="3">
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="openAddDialog"
        >
          添加用户
        </v-btn>
      </v-col>
      
      <!-- 搜索框 -->
      <v-col cols="12" sm="6" md="3">
        <v-text-field
          v-model="search"
          label="搜索用户"
          prepend-inner-icon="mdi-magnify"
          single-line
          hide-details
          variant="outlined"
          density="compact"
        ></v-text-field>
      </v-col>

      <!-- 角色筛选 -->
      <v-col cols="12" sm="6" md="3">
        <v-select
          v-model="roleFilter"
          :items="roleOptions"
          label="角色筛选"
          hide-details
          variant="outlined"
          density="compact"
        ></v-select>
      </v-col>

      <!-- 状态筛选 -->
      <v-col cols="12" sm="6" md="3">
        <v-select
          v-model="statusFilter"
          :items="statusOptions"
          label="状态筛选"
          hide-details
          variant="outlined"
          density="compact"
        ></v-select>
      </v-col>
    </v-row>

    <!-- 用户列表 -->
    <v-card>
      <v-data-table
        :headers="headers"
        :items="filteredUsers"
        :loading="loading"
        hover
      >
        <!-- 头像列自定义显示 -->
        <template v-slot:item.avatar="{ item }">
          <v-avatar size="40">
            <v-img
              :src="item.avatar || getDefaultAvatar(item.username)"
              :alt="item.username"
            >
              <template v-slot:placeholder>
                <v-avatar
                  color="grey-lighten-2"
                  size="40"
                >
                  <v-icon icon="mdi-account" />
                </v-avatar>
              </template>
            </v-img>
          </v-avatar>
        </template>

        <!-- 状态列自定义显示 -->
        <template v-slot:item.status="{ item }">
          <v-chip
            :color="item.status === 'active' ? 'success' : 'error'"
            size="small"
          >
            {{ item.status === 'active' ? '启用' : '禁用' }}
          </v-chip>
        </template>

        <!-- 操作列 -->
        <template v-slot:item.actions="{ item }">
          <v-btn
            icon
            variant="text"
            color="primary"
            size="small"
            @click="openEditDialog(item)"
          >
            <Icon icon="mdi-pencil" />
          </v-btn>
          <v-btn
            icon
            variant="text"
            color="error"
            size="small"
            @click="deleteUser(item.id)"
          >
            <Icon icon="mdi-delete" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑用户对话框 -->
    <v-dialog v-model="dialog" max-width="500">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="saveUser">
            <!-- 修改头像预览和上传 -->
            <div class="d-flex justify-center align-center mb-4">
              <v-avatar size="100" class="mb-2">
                <v-img
                  :src="editedUser.avatar || getDefaultAvatar(editedUser.username)"
                  :alt="editedUser.username"
                >
                  <template v-slot:placeholder>
                    <v-avatar
                      color="grey-lighten-2"
                      size="100"
                    >
                      <v-icon icon="mdi-account" size="48" />
                    </v-avatar>
                  </template>
                </v-img>
              </v-avatar>
            </div>
            
            <!-- 添加文件上传按钮 -->
            <div class="d-flex justify-center mb-4">
              <v-btn
                color="primary"
                prepend-icon="mdi-camera"
                @click="$refs.fileInput.click()"
                :loading="loading"
              >
                更换头像
              </v-btn>
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleAvatarUpload"
              >
            </div>

            <!-- 或者手动输入URL -->
            <v-text-field
              v-model="editedUser.avatar"
              label="或直接输入头像URL"
              placeholder="请输入头像图片链接"
              class="mb-4"
              hint="支持直接输入图片链接或上传本地图片"
              persistent-hint
            ></v-text-field>

            <v-text-field
              v-model="editedUser.username"
              label="用户名"
              required
            ></v-text-field>
            <v-text-field
              v-model="editedUser.email"
              label="邮箱"
              type="email"
              required
            ></v-text-field>
            <v-text-field
              v-model="editedUser.phone"
              label="电话"
            ></v-text-field>
            <v-select
              v-model="editedUser.gender"
              label="性别"
              :items="['男', '女']"
            ></v-select>
            <v-select
              v-model="editedUser.role"
              label="角色"
              :items="[
                { title: '管理员', value: 'admin' },
                { title: '用户', value: 'user' },
                { title: '回收员', value: 'recycler' }
              ]"
            ></v-select>
            <v-select
              v-model="editedUser.status"
              label="状态"
              :items="[
                { title: '启用', value: 'active' },
                { title: '禁用', value: 'inactive' }
              ]"
            ></v-select>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="dialog = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            variant="text"
            @click="saveUser"
            :loading="loading"
          >
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.v-data-table {
  border-radius: 8px;
}

/* 添加头像相关样式 */
.v-avatar {
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.v-avatar:hover {
  border-color: var(--v-theme-primary);
  transform: scale(1.05);
}

/* 添加头像上传相关样式 */
.avatar-upload {
  position: relative;
  cursor: pointer;
}

.avatar-upload:hover .upload-overlay {
  opacity: 1;
}

.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

/* 添加搜索和筛选相关样式 */
.v-text-field,
.v-select {
  margin-top: 0;
}

@media (max-width: 960px) {
  .v-btn {
    margin-bottom: 16px;
  }
}
</style>
