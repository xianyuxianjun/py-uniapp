<script setup>
import http from '@/utils/http'
import { onMounted, ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { message } from '@/utils/message'
import { generateUserId } from '@/utils/idGenerator'

// 商品列表数据
const products = ref([])
// 控制添加/编辑对话框的显示
const dialog = ref(false)
// 当前编辑的商品数据
const editedProduct = ref({
  productName: '',
  description: '',
  price: '',
  stockQuantity: '',
  imageUrl: '',
  status: 1
})
// 对话框标题
const dialogTitle = ref('')
// 是否是编辑模式
const isEdit = ref(false)
// 加载状态
const loading = ref(false)
// 文件上传引用
const fileInput = ref(null)
// 本地图片预览URL
const localImageUrl = ref('')

// 添加搜索和筛选状态
const search = ref('')
const statusFilter = ref('all')

// 状态选项
const statusOptions = [
  { title: '全部状态', value: 'all' },
  { title: '在售', value: 1 },
  { title: '下架', value: 2 }
]

// 计算过滤后的商品列表
const filteredProducts = computed(() => {
  return products.value.filter(product => {
    // 搜索条件：商品名称或描述包含搜索关键词
    const searchMatch = search.value === '' || 
      product.productName?.toLowerCase().includes(search.value.toLowerCase()) ||
      product.description?.toLowerCase().includes(search.value.toLowerCase())

    // 状态筛选
    const statusMatch = statusFilter.value === 'all' || product.status === statusFilter.value

    return searchMatch && statusMatch
  })
})

// 获取状态信息
const getStatusInfo = (status) => {
  const statusMap = {
    1: { text: '在售', color: 'success' },
    2: { text: '下架', color: 'error' }
  }
  return statusMap[status] || { text: '未知', color: 'grey' }
}

// 表格配置
const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: '图片', key: 'imageUrl', sortable: false, align: 'center' },
  { title: '商品名称', key: 'productName', sortable: true },
  { title: '价格(元)', key: 'price', sortable: true },
  { title: '库存', key: 'stockQuantity', sortable: true },
  { title: '状态', key: 'status', sortable: true },
  { title: '描述', key: 'description' },
  { title: '操作', key: 'actions', sortable: false }
]

// 获取商品列表
function getProducts() {
  loading.value = true
  http.get("/product/list").then(res => {
    products.value = res.data.data
    loading.value = false
  })
}

// 打开添加商品对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加商品'
  editedProduct.value = {
    id: generateUserId(), // 生成临时ID用于图片关联
    productName: '',
    description: '',
    price: '',
    stockQuantity: '',
    imageUrl: '',
    status: 1
  }
  localImageUrl.value = '' // 清空本地预览
  dialog.value = true
}

// 打开编辑商品对话框
function openEditDialog(product) {
  isEdit.value = true
  dialogTitle.value = '编辑商品'
  editedProduct.value = { ...product }
  localImageUrl.value = '' // 清空本地预览
  dialog.value = true
}

// 处理图片上传
function handleImageUpload(event) {
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
  
  // 创建本地预览URL
  localImageUrl.value = URL.createObjectURL(file)
  
  // 创建FormData对象
  const formData = new FormData()
  formData.append('file', file)
  formData.append('relatedType', 'product')
  formData.append('relatedId', editedProduct.value.id)
  
  loading.value = true
  http.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(res => {
    loading.value = false
    if (res.data.code === 1) {
      // 上传成功后，记录文件ID，但使用本地预览URL显示
      editedProduct.value.fileId = res.data.data
      message.success('图片上传成功')
    } else {
      message.error(res.data.message || '图片上传失败')
      localImageUrl.value = '' // 清空本地预览
    }
  }).catch(err => {
    loading.value = false
    message.error('图片上传失败：' + err.message)
    localImageUrl.value = '' // 清空本地预览
  })
}

// 保存商品（添加/编辑）
function saveProduct() {
  // 表单验证
  if (!editedProduct.value.productName) {
    message.error('请输入商品名称')
    return
  }
  
  if (!editedProduct.value.price) {
    message.error('请输入商品价格')
    return
  }
  
  if (!editedProduct.value.stockQuantity) {
    message.error('请输入商品库存')
    return
  }
  
  // 如果有本地预览图片，则使用fileId构建imageUrl
  if (localImageUrl.value && editedProduct.value.fileId) {
    editedProduct.value.imageUrl = `/file/view/${editedProduct.value.fileId}`
  }
  
  loading.value = true
  const request = isEdit.value
    ? http.post('/product/update', editedProduct.value)
    : http.post('/product', editedProduct.value)
      
  request.then(res => {
    loading.value = false
    if (res.data.code === 1) {
      dialog.value = false
      getProducts()
      message.success(isEdit.value ? '更新成功' : '添加成功')
      // 释放本地预览URL
      if (localImageUrl.value) {
        URL.revokeObjectURL(localImageUrl.value)
        localImageUrl.value = ''
      }
    } else {
      message.error(res.data.message || (isEdit.value ? '更新失败' : '添加失败'))
    }
  }).catch(err => {
    loading.value = false
    message.error((isEdit.value ? '更新' : '添加') + '失败：' + err.message)
  })
}

// 删除商品
function deleteProduct(id) {
  if (!confirm('确定要删除这个商品吗？')) return
  
  loading.value = true
  http.delete(`/product/${id}`).then(res => {
    loading.value = false
    if (res.data.code === 1) {
      getProducts()
      message.success('删除成功')
    } else {
      message.error(res.data.message || '删除失败')
    }
  }).catch(err => {
    loading.value = false
    message.error('删除失败：' + err.message)
  })
}

// 更新商品状态（上架/下架）
function updateStatus(product) {
  const newStatus = product.status === 1 ? 2 : 1
  const statusText = newStatus === 1 ? '上架' : '下架'
  
  loading.value = true
  http.post(`/product/status/${product.id}?status=${newStatus}`).then(res => {
    loading.value = false
    if (res.data.code === 1) {
      product.status = newStatus
      message.success(`${statusText}成功`)
    } else {
      message.error(res.data.message || `${statusText}失败`)
    }
  }).catch(err => {
    loading.value = false
    message.error(`${statusText}失败：` + err.message)
  })
}

// 在组件挂载时获取商品列表
onMounted(() => {
  getProducts()
})
</script>

<template>
  <v-container>
    <v-card class="mb-4">
      <v-card-title class="d-flex align-center">
        <h2 class="text-h5">商品管理</h2>
        <v-spacer></v-spacer>
        
        <!-- 搜索框 -->
        <v-text-field
          v-model="search"
          append-inner-icon="mdi-magnify"
          label="搜索商品"
          hide-details
          density="compact"
          class="mr-4"
          style="max-width: 300px"
        ></v-text-field>
        
        <!-- 状态筛选 -->
        <v-select
          v-model="statusFilter"
          :items="statusOptions"
          label="状态筛选"
          hide-details
          density="compact"
          class="mr-4"
          style="max-width: 150px"
        ></v-select>
        
        <!-- 添加按钮 -->
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="openAddDialog"
        >
          添加商品
        </v-btn>
      </v-card-title>
      
      <!-- 商品列表表格 -->
      <v-data-table
        :headers="headers"
        :items="filteredProducts"
        :loading="loading"
        class="elevation-1"
      >
        <!-- 图片列 -->
        <template v-slot:item.imageUrl="{ item }">
          <v-avatar size="40">
            <v-img
              :src="item.imageUrl"
              :alt="item.productName"
              cover
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
        
        <!-- 状态列 -->
        <template v-slot:item.status="{ item }">
          <v-chip
            :color="getStatusInfo(item.status).color"
            size="small"
          >
            {{ getStatusInfo(item.status).text }}
          </v-chip>
        </template>
        
        <!-- 操作列 -->
        <template v-slot:item.actions="{ item }">
          <!-- 上架/下架按钮 -->
          <v-btn
            icon
            variant="text"
            :color="item.status === 1 ? 'error' : 'success'"
            size="small"
            @click="updateStatus(item)"
            :title="item.status === 1 ? '下架' : '上架'"
          >
            <Icon :icon="item.status === 1 ? 'mdi-arrow-down-bold' : 'mdi-arrow-up-bold'" />
          </v-btn>
          
          <!-- 编辑按钮 -->
          <v-btn
            icon
            variant="text"
            color="primary"
            size="small"
            @click="openEditDialog(item)"
            title="编辑"
          >
            <Icon icon="mdi-pencil" />
          </v-btn>
          
          <!-- 删除按钮 -->
          <v-btn
            icon
            variant="text"
            color="error"
            size="small"
            @click="deleteProduct(item.id)"
            title="删除"
          >
            <Icon icon="mdi-delete" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑商品对话框 -->
    <v-dialog v-model="dialog" max-width="600">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="saveProduct">
            <!-- 图片预览和上传 -->
            <div class="d-flex justify-center align-center mb-4">
              <v-avatar size="100" class="elevation-2">
                <v-img
                  :src="localImageUrl || editedProduct.imageUrl"
                  :alt="editedProduct.productName"
                  cover
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
                @click="fileInput.click()"
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
              v-model="editedProduct.productName"
              label="商品名称"
              required
            ></v-text-field>
            
            <v-text-field
              v-model="editedProduct.price"
              label="价格"
              type="number"
              step="0.01"
              suffix="元"
              required
            ></v-text-field>

            <v-text-field
              v-model="editedProduct.stockQuantity"
              label="库存"
              type="number"
              required
            ></v-text-field>

            <v-select
              v-model="editedProduct.status"
              label="状态"
              :items="[
                { title: '在售', value: 1 },
                { title: '下架', value: 2 }
              ]"
              required
            ></v-select>

            <v-textarea
              v-model="editedProduct.description"
              label="商品描述"
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
            @click="saveProduct"
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