<script setup>
import http from '@/utils/http'
import { onMounted, ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { message } from '@/utils/message'

// 评价列表数据
const reviews = ref([])
// 控制添加/编辑对话框的显示
const dialog = ref(false)
// 当前编辑的评价数据
const editedReview = ref({
  orderId: '',
  customerId: '',
  rating: 5,
  comment: '',
})
// 对话框标题
const dialogTitle = ref('')
// 是否是编辑模式
const isEdit = ref(false)
// 加载状态
const loading = ref(false)

// 添加搜索和筛选状态
const search = ref('')
const ratingFilter = ref('all')

// 评分选项
const ratingOptions = [
  { title: '全部评分', value: 'all' },
  { title: '1星', value: 1 },
  { title: '2星', value: 2 },
  { title: '3星', value: 3 },
  { title: '4星', value: 4 },
  { title: '5星', value: 5 }
]

// 计算过滤后的评价列表
const filteredReviews = computed(() => {
  return reviews.value.filter(review => {
    // 搜索条件：评价内容包含搜索关键词
    const searchMatch = search.value === '' || 
      review.comment?.toLowerCase().includes(search.value.toLowerCase())

    // 评分筛选
    const ratingMatch = ratingFilter.value === 'all' || review.rating === ratingFilter.value

    return searchMatch && ratingMatch
  })
})

// 表格配置
const headers = [
  { title: '评价ID', key: 'reviewId', sortable: true },
  { title: '订单ID', key: 'orderId', sortable: true },
  { title: '用户ID', key: 'customerId', sortable: true },
  { title: '评分', key: 'rating', sortable: true },
  { title: '评价内容', key: 'comment' },
  { title: '评价时间', key: 'createdAt', sortable: true },
  { title: '更新时间', key: 'updatedAt', sortable: true },
  { title: '操作', key: 'actions', sortable: false }
]

// 获取评价列表
function getReviews() {
  loading.value = true
  http.get("/orderReview/list").then(res => {
    reviews.value = res.data.data
    loading.value = false
  })
}

// 打开添加评价对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加评价'
  editedReview.value = {
    orderId: '',
    customerId: '',
    rating: 5,
    comment: '',
  }
  dialog.value = true
}

// 打开编辑评价对话框
function openEditDialog(review) {
  isEdit.value = true
  dialogTitle.value = '编辑评价'
  editedReview.value = { ...review }
  dialog.value = true
}

// 保存评价（添加/编辑）
function saveReview() {
  loading.value = true
  const request = isEdit.value
    ? http.post('/orderReview/update', editedReview.value)
    : http.post('/orderReview', editedReview.value)
    
  request.then(res => {
    if (res.data.code === 1) {
      dialog.value = false
      getReviews()
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

// 删除评价
function deleteReview(id) {
  if (!window.confirm('确定要删除该评价吗？')) {
    return
  }
  
  loading.value = true
  http.delete(`/orderReview/${id}`).then(res => {
    if (res.data.code === 1) {
      getReviews()
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

onMounted(() => {
  getReviews()
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
          添加评价
        </v-btn>
      </v-col>
      
      <!-- 搜索框 -->
      <v-col cols="12" sm="6" md="3">
        <v-text-field
          v-model="search"
          label="搜索评价"
          prepend-inner-icon="mdi-magnify"
          single-line
          hide-details
          variant="outlined"
          density="compact"
        ></v-text-field>
      </v-col>

      <!-- 评分筛选 -->
      <v-col cols="12" sm="6" md="3">
        <v-select
          v-model="ratingFilter"
          :items="ratingOptions"
          label="评分筛选"
          hide-details
          variant="outlined"
          density="compact"
        ></v-select>
      </v-col>
    </v-row>

    <!-- 评价列表 -->
    <v-card>
      <v-data-table
        :headers="headers"
        :items="filteredReviews"
        :loading="loading"
        hover
      >
        <!-- 评分列自定义显示 -->
        <template v-slot:item.rating="{ item }">
          <v-rating
            v-model="item.rating"
            readonly
            density="compact"
            size="small"
          ></v-rating>
        </template>

        <!-- 时间列格式化 -->
        <template v-slot:item.createdAt="{ item }">
          {{ new Date(item.createdAt).toLocaleString() }}
        </template>
        <template v-slot:item.updatedAt="{ item }">
          {{ new Date(item.updatedAt).toLocaleString() }}
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
            @click="deleteReview(item.reviewId)"
          >
            <Icon icon="mdi-delete" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑评价对话框 -->
    <v-dialog v-model="dialog" max-width="500">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="saveReview">
            <v-text-field
              v-model="editedReview.orderId"
              label="订单ID"
              type="number"
              required
            ></v-text-field>

            <v-text-field
              v-model="editedReview.customerId"
              label="用户ID"
              type="number"
              required
            ></v-text-field>

            <div class="mb-4">
              <label class="text-subtitle-2">评分</label>
              <v-rating
                v-model="editedReview.rating"
                hover
                class="mt-2"
              ></v-rating>
            </div>

            <v-textarea
              v-model="editedReview.comment"
              label="评价内容"
              required
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
            @click="saveReview"
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