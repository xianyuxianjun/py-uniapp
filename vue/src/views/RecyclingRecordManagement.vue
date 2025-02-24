<script setup>
import http from '@/utils/http'
import { onMounted, ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { message } from '@/utils/message'

// 回收记录列表数据
const records = ref([])
// 控制添加/编辑对话框的显示
const dialog = ref(false)
// 当前编辑的记录数据
const editedRecord = ref({
  orderId: '',
  recyclerId: '',
  customerId: '',
  categoryId: '',
  weight: '',
  unitPrice: '',
  amount: '',
  pickupTime: new Date().toISOString().slice(0, 16), // 格式化为 YYYY-MM-DDTHH:mm
  pickupAddress: '',
  notes: ''
})
// 对话框标题
const dialogTitle = ref('')
// 是否是编辑模式
const isEdit = ref(false)
// 加载状态
const loading = ref(false)

// 添加搜索状态
const search = ref('')

// 表格配置
const headers = [
  { title: '记录ID', key: 'id', sortable: true },
  { title: '订单ID', key: 'orderId', sortable: true },
  { title: '回收员ID', key: 'recyclerId', sortable: true },
  { title: '客户ID', key: 'customerId', sortable: true },
  { title: '废品分类ID', key: 'categoryId', sortable: true },
  { title: '重量', key: 'weight', sortable: true },
  { title: '单价(元)', key: 'unitPrice', sortable: true },
  { title: '金额(元)', key: 'amount', sortable: true },
  { title: '回收时间', key: 'pickupTime', sortable: true },
  { title: '回收地址', key: 'pickupAddress' },
  { title: '操作', key: 'actions', sortable: false }
]

// 计算过滤后的记录列表
const filteredRecords = computed(() => {
  return records.value.filter(record => {
    // 搜索条件：地址或备注包含搜索关键词
    const searchMatch = search.value === '' || 
      record.pickupAddress?.toLowerCase().includes(search.value.toLowerCase()) ||
      record.notes?.toLowerCase().includes(search.value.toLowerCase())
    return searchMatch
  })
})

// 获取回收记录列表
function getRecords() {
  loading.value = true
  http.get("/recyclingRecord/list").then(res => {
    records.value = res.data.data
    loading.value = false
  })
}

// 打开添加记录对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加回收记录'
  editedRecord.value = {
    orderId: '',
    recyclerId: '',
    customerId: '',
    categoryId: '',
    weight: '',
    unitPrice: '',
    amount: '',
    pickupTime: new Date().toISOString().slice(0, 16),
    pickupAddress: '',
    notes: ''
  }
  dialog.value = true
}

// 打开编辑记录对话框
function openEditDialog(record) {
  isEdit.value = true
  dialogTitle.value = '编辑回收记录'
  editedRecord.value = { 
    ...record,
    pickupTime: new Date(record.pickupTime).toISOString().slice(0, 16)
  }
  dialog.value = true
}

// 计算金额
function calculateAmount() {
  if (editedRecord.value.weight && editedRecord.value.unitPrice) {
    editedRecord.value.amount = (
      parseFloat(editedRecord.value.weight) * parseFloat(editedRecord.value.unitPrice)
    ).toFixed(2)
  }
}

// 保存记录（添加/编辑）
function saveRecord() {
  loading.value = true
  const request = isEdit.value
    ? http.post('/recyclingRecord/update', editedRecord.value)
    : http.post('/recyclingRecord', editedRecord.value)
    
  request.then(res => {
    if (res.data.code === 1) {
      dialog.value = false
      getRecords()
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

// 删除记录
function deleteRecord(id) {
  if (!window.confirm('确定要删除该回收记录吗？')) {
    return
  }
  
  loading.value = true
  http.delete(`/recyclingRecord/${id}`).then(res => {
    if (res.data.code === 1) {
      getRecords()
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
  getRecords()
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
          添加记录
        </v-btn>
      </v-col>
      
      <!-- 搜索框 -->
      <v-col cols="12" sm="6" md="4">
        <v-text-field
          v-model="search"
          label="搜索记录"
          prepend-inner-icon="mdi-magnify"
          single-line
          hide-details
          variant="outlined"
          density="compact"
        ></v-text-field>
      </v-col>
    </v-row>

    <!-- 记录列表 -->
    <v-card>
      <v-data-table
        :headers="headers"
        :items="filteredRecords"
        :loading="loading"
        hover
      >
        <!-- 时间列格式化 -->
        <template v-slot:item.pickupTime="{ item }">
          {{ new Date(item.pickupTime).toLocaleString() }}
        </template>

        <!-- 金额列格式化 -->
        <template v-slot:item.amount="{ item }">
          ¥{{ parseFloat(item.amount).toFixed(2) }}
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
            @click="deleteRecord(item.id)"
          >
            <Icon icon="mdi-delete" />
          </v-btn>
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑记录对话框 -->
    <v-dialog v-model="dialog" max-width="600">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="saveRecord">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.orderId"
                  label="订单ID"
                  type="number"
                  required
                ></v-text-field>
              </v-col>
              
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.recyclerId"
                  label="回收员ID"
                  type="number"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.customerId"
                  label="客户ID"
                  type="number"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.categoryId"
                  label="废品分类ID"
                  type="number"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.weight"
                  label="重量"
                  type="number"
                  step="0.01"
                  required
                  @input="calculateAmount"
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.unitPrice"
                  label="单价"
                  type="number"
                  step="0.01"
                  suffix="元"
                  required
                  @input="calculateAmount"
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.amount"
                  label="金额"
                  type="number"
                  step="0.01"
                  suffix="元"
                  readonly
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedRecord.pickupTime"
                  label="回收时间"
                  type="datetime-local"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12">
                <v-text-field
                  v-model="editedRecord.pickupAddress"
                  label="回收地址"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12">
                <v-textarea
                  v-model="editedRecord.notes"
                  label="备注信息"
                  rows="3"
                ></v-textarea>
              </v-col>
            </v-row>
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
            @click="saveRecord"
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