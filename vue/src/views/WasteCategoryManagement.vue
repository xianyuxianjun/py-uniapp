<script setup>
import http from '@/utils/http'
import { onMounted, ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { message } from '@/utils/message'
import { generateUserId } from '@/utils/idGenerator'

// 废品分类列表数据
const categories = ref([])
// 控制添加/编辑对话框的显示
const dialog = ref(false)
// 当前编辑的分类数据
const editedCategory = ref({
  categoryName: '',
  price: '',
  imageUrl: '',
  description: '',
  unit: 'kg'
})
// 对话框标题
const dialogTitle = ref('')
// 是否是编辑模式
const isEdit = ref(false)
// 加载状态
const loading = ref(false)

// 添加搜索和筛选状态
const search = ref('')

// 修改表格的 headers 配置
const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: '图片', key: 'imageUrl', sortable: false, align: 'center' },
  { title: '分类名称', key: 'categoryName', sortable: true },
  { title: '价格(元)', key: 'price' },
  { title: '计量单位', key: 'unit' },
  { title: '描述', key: 'description' },
  { title: '操作', key: 'actions', sortable: false }
]

// 计算过滤后的分类列表
const filteredCategories = computed(() => {
  return categories.value.filter(category => {
    // 搜索条件：分类名称或描述包含搜索关键词
    const searchMatch = search.value === '' || 
      category.categoryName?.toLowerCase().includes(search.value.toLowerCase()) ||
      category.description?.toLowerCase().includes(search.value.toLowerCase())
    return searchMatch
  })
})

// 获取分类列表
function getCategories() {
  loading.value = true
  http.get("/wasteCategory/list").then(res => {
    categories.value = res.data.data
    loading.value = false
  })
}

// 打开添加分类对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加分类'
  editedCategory.value = {
    id: generateUserId(),
    categoryName: '',
    price: '',
    imageUrl: '',
    description: '',
    unit: 'kg'
  }
  dialog.value = true
}

// 打开编辑分类对话框
function openEditDialog(category) {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  editedCategory.value = { ...category }
  dialog.value = true
}

// 保存分类（添加/编辑）
function saveCategory() {
  loading.value = true
  const request = isEdit.value
    ? http.post('/wasteCategory/update', editedCategory.value)
    : http.post('/wasteCategory', editedCategory.value)
    
  request.then(res => {
    if (res.data.code === 1) {
      dialog.value = false
      getCategories()
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

// 删除分类
function deleteCategory(id) {
  if (!window.confirm('确定要删除该分类吗？')) {
    return
  }
  
  loading.value = true
  http.delete(`/wasteCategory/${id}`).then(res => {
    if (res.data.code === 1) {
      getCategories()
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

// 处理图片上传
async function handleImageUpload(event) {
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

  // 创建预览URL
  const previewUrl = URL.createObjectURL(file)
  // 先更新预览
  editedCategory.value.imageUrl = previewUrl

  loading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('relatedId', String(editedCategory.value.id))
    formData.append('relatedType', 'category')

    const res = await http.post('/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (res.data.code === 1) {
      message.success('图片上传成功')
    } else {
      message.error(res.data.message || '图片上传失败')
      editedCategory.value.imageUrl = null
    }
  } catch (error) {
    message.error('图片上传失败，请稍后重试')
    console.error('上传错误:', error)
    editedCategory.value.imageUrl = null
  } finally {
    loading.value = false
    URL.revokeObjectURL(previewUrl)
  }
}

onMounted(() => {
  getCategories()
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
          添加分类
        </v-btn>
      </v-col>
      
      <!-- 搜索框 -->
      <v-col cols="12" sm="6" md="4">
        <v-text-field
          v-model="search"
          label="搜索分类"
          prepend-inner-icon="mdi-magnify"
          single-line
          hide-details
          variant="outlined"
          density="compact"
        ></v-text-field>
      </v-col>
    </v-row>

    <!-- 分类列表 -->
    <v-card>
      <v-data-table
        :headers="headers"
        :items="filteredCategories"
        :loading="loading"
        hover
      >
        <!-- 图片列自定义显示 -->
        <template v-slot:item.imageUrl="{ item }">
          <v-avatar size="40">
            <v-img
              :src="item.imageUrl"
              :alt="item.categoryName"
            >
              <template v-slot:placeholder>
                <v-avatar
                  color="grey-lighten-2"
                  size="40"
                >
                  <v-icon icon="mdi-image" />
                </v-avatar>
              </template>
            </v-img>
          </v-avatar>
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
            @click="deleteCategory(item.id)"
          >
            <Icon icon="mdi-delete" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑分类对话框 -->
    <v-dialog v-model="dialog" max-width="500">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="saveCategory">
            <!-- 图片预览和上传 -->
            <div class="d-flex justify-center align-center mb-4">
              <v-avatar size="100" class="mb-2">
                <v-img
                  :src="editedCategory.imageUrl"
                  :alt="editedCategory.categoryName"
                >
                  <template v-slot:placeholder>
                    <v-avatar
                      color="grey-lighten-2"
                      size="100"
                    >
                      <v-icon icon="mdi-image" size="48" />
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
                上传图片
              </v-btn>
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleImageUpload"
              >
            </div>

            <v-text-field
              v-model="editedCategory.categoryName"
              label="分类名称"
              required
            ></v-text-field>
            
            <v-text-field
              v-model="editedCategory.price"
              label="价格"
              type="number"
              suffix="元"
              required
            ></v-text-field>

            <v-select
              v-model="editedCategory.unit"
              label="计量单位"
              :items="['kg', '个', '箱']"
              required
            ></v-select>

            <v-textarea
              v-model="editedCategory.description"
              label="描述"
              rows="3"
            ></v-textarea>
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
            @click="saveCategory"
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

.v-avatar {
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.v-avatar:hover {
  border-color: var(--v-theme-primary);
  transform: scale(1.05);
}
</style> 