<script setup>
import http from '@/utils/http'
import { onMounted, ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { message } from '@/utils/message'

// 公告列表数据
const announcements = ref([])
// 控制添加/编辑对话框的显示
const dialog = ref(false)
// 当前编辑的公告数据
const editedAnnouncement = ref({
  title: '',
  content: '',
  status: 1, // 1：草稿，2：发布，3：下线
  publishDate: new Date().toISOString().slice(0, 16) // 格式化为 YYYY-MM-DDTHH:mm
})
// 对话框标题
const dialogTitle = ref('')
// 是否是编辑模式
const isEdit = ref(false)
// 加载状态
const loading = ref(false)

// 添加搜索和筛选状态
const search = ref('')
const statusFilter = ref('all')

// 状态选项
const statusOptions = [
  { title: '全部状态', value: 'all' },
  { title: '草稿', value: 1 },
  { title: '已发布', value: 2 },
  { title: '已下线', value: 3 }
]

// 计算过滤后的公告列表
const filteredAnnouncements = computed(() => {
  return announcements.value.filter(announcement => {
    // 搜索条件：标题或内容包含搜索关键词
    const searchMatch = search.value === '' || 
      announcement.title?.toLowerCase().includes(search.value.toLowerCase()) ||
      announcement.content?.toLowerCase().includes(search.value.toLowerCase())

    // 状态筛选
    const statusMatch = statusFilter.value === 'all' || announcement.status === statusFilter.value

    return searchMatch && statusMatch
  })
})

// 表格配置
const headers = [
  { title: 'ID', key: 'id', sortable: true },
  { title: '标题', key: 'title', sortable: true },
  { title: '内容', key: 'content' },
  { title: '状态', key: 'status' },
  { title: '发布日期', key: 'publishDate', sortable: true },
  { title: '创建时间', key: 'createdAt', sortable: true },
  { title: '更新时间', key: 'updatedAt', sortable: true },
  { title: '操作', key: 'actions', sortable: false }
]

// 获取公告列表
function getAnnouncements() {
  loading.value = true
  http.get("/announcement/list").then(res => {
    announcements.value = res.data.data
    loading.value = false
  })
}

// 打开添加公告对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加公告'
  editedAnnouncement.value = {
    title: '',
    content: '',
    status: 1,
    publishDate: new Date().toISOString().slice(0, 16)
  }
  dialog.value = true
}

// 打开编辑公告对话框
function openEditDialog(announcement) {
  isEdit.value = true
  dialogTitle.value = '编辑公告'
  editedAnnouncement.value = { 
    ...announcement,
    publishDate: new Date(announcement.publishDate).toISOString().slice(0, 16)
  }
  dialog.value = true
}

// 保存公告（添加/编辑）
function saveAnnouncement() {
  loading.value = true
  const request = isEdit.value
    ? http.post('/announcement/update', editedAnnouncement.value)
    : http.post('/announcement', editedAnnouncement.value)
    
  request.then(res => {
    if (res.data.code === 1) {
      dialog.value = false
      getAnnouncements()
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

// 删除公告
function deleteAnnouncement(id) {
  if (!window.confirm('确定要删除该公告吗？')) {
    return
  }
  
  loading.value = true
  http.delete(`/announcement/${id}`).then(res => {
    if (res.data.code === 1) {
      getAnnouncements()
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

// 获取状态显示文本
function getStatusText(status) {
  const statusMap = {
    1: '草稿',
    2: '已发布',
    3: '已下线'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态显示颜色
function getStatusColor(status) {
  const colorMap = {
    1: 'warning',
    2: 'success',
    3: 'error'
  }
  return colorMap[status] || 'grey'
}

// 修改状态变更方法
function changeStatus(announcement) {
  // 修改状态判断逻辑
  const newStatus = announcement.status === 2 ? 3 : 2 // 如果是已发布则下线，其他状态（草稿或已下线）则发布
  const statusText = newStatus === 2 ? '发布' : '下线'
  
  if (!window.confirm(`确定要${statusText}该公告吗？`)) {
    return
  }
  
  loading.value = true
  http.post('/announcement/update', {
    ...announcement,
    status: newStatus
  }).then(res => {
    if (res.data.code === 1) {
      getAnnouncements()
      message.success(`${statusText}成功`)
    } else {
      message.error(res.data.message || `${statusText}失败`)
    }
  }).catch(() => {
    message.error(`${statusText}失败，请稍后重试`)
  }).finally(() => {
    loading.value = false
  })
}

onMounted(() => {
  getAnnouncements()
})
</script>

<template>
  <v-container>
    <!-- 顶部操作栏 -->
    <v-row class="mb-4">
      <v-col cols="12" sm="6" md="4">
        <v-btn
          color="primary"
          prepend-icon="mdi-plus"
          @click="openAddDialog"
        >
          添加公告
        </v-btn>
      </v-col>
      
      <!-- 搜索框 -->
      <v-col cols="12" sm="6" md="4">
        <v-text-field
          v-model="search"
          label="搜索公告"
          prepend-inner-icon="mdi-magnify"
          single-line
          hide-details
          variant="outlined"
          density="compact"
        ></v-text-field>
      </v-col>

      <!-- 状态筛选 -->
      <v-col cols="12" sm="6" md="4">
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

    <!-- 公告列表 -->
    <v-card>
      <v-data-table
        :headers="headers"
        :items="filteredAnnouncements"
        :loading="loading"
        hover
      >
        <!-- 内容列自定义显示 -->
        <template v-slot:item.content="{ item }">
          <span class="text-truncate" style="max-width: 200px; display: inline-block;">
            {{ item.content }}
          </span>
        </template>

        <!-- 状态列自定义显示 -->
        <template v-slot:item.status="{ item }">
          <v-chip
            :color="getStatusColor(item.status)"
            size="small"
          >
            {{ getStatusText(item.status) }}
          </v-chip>
        </template>

        <!-- 操作列 -->
        <template v-slot:item.actions="{ item }">
          <!-- 修改发布/下线按钮的显示逻辑 -->
          <v-btn
            icon
            variant="text"
            :color="item.status === 2 ? 'error' : 'success'"
            size="small"
            @click="changeStatus(item)"
            :title="item.status === 2 ? '下线' : '发布'"
          >
            <Icon :icon="item.status === 2 ? 'mdi-arrow-down-circle' : 'mdi-arrow-up-circle'" />
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
            @click="deleteAnnouncement(item.id)"
            title="删除"
          >
            <Icon icon="mdi-delete" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑公告对话框 -->
    <v-dialog v-model="dialog" max-width="600">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="saveAnnouncement">
            <v-text-field
              v-model="editedAnnouncement.title"
              label="标题"
              required
            ></v-text-field>
            
            <v-textarea
              v-model="editedAnnouncement.content"
              label="内容"
              required
              rows="5"
            ></v-textarea>

            <v-select
              v-model="editedAnnouncement.status"
              label="状态"
              :items="[
                { title: '草稿', value: 1 },
                { title: '发布', value: 2 },
                { title: '下线', value: 3 }
              ]"
              required
            ></v-select>

            <v-text-field
              v-model="editedAnnouncement.publishDate"
              label="发布时间"
              type="datetime-local"
              required
            ></v-text-field>
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
            @click="saveAnnouncement"
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