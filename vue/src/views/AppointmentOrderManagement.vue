<script setup>
import http from '@/utils/http'
import { onMounted, ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { message } from '@/utils/message'
import { generateUserId } from '@/utils/idGenerator'
import AMapLoader from '@amap/amap-jsapi-loader'
import { mapConfig, initAMap, mapUtils } from '@/config/amap'

// 预约订单列表数据
const orders = ref([])
// 控制添加/编辑对话框的显示
const dialog = ref(false)
// 当前编辑的订单数据
const editedOrder = ref({
  userId: '',
  appointmentTime: '',
  address: '',
  contactName: '',
  contactPhone: '',
  status: 1,
  notes: '',
  longitude: '',
  latitude: '',
  mapAddress: ''
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
  { title: '待确认', value: 1 },
  { title: '已接单', value: 2 },
  { title: '已完成', value: 3 },
  { title: '已取消', value: 4 }
]

// 表格表头配置
const headers = [
  { title: '订单ID', key: 'id', sortable: true },
  { title: '用户ID', key: 'userId', sortable: true },
  { title: '预约时间', key: 'appointmentTime', sortable: true },
  { title: '联系人', key: 'contactName' },
  { title: '联系电话', key: 'contactPhone' },
  { title: '地址', key: 'address' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'actions', sortable: false }
]

// 计算过滤后的订单列表
const filteredOrders = computed(() => {
  return orders.value.filter(order => {
    // 搜索条件：联系人、电话或地址包含搜索关键词
    const searchMatch = search.value === '' || 
      order.contactName?.toLowerCase().includes(search.value.toLowerCase()) ||
      order.contactPhone?.includes(search.value) ||
      order.address?.toLowerCase().includes(search.value.toLowerCase())

    // 状态筛选
    const statusMatch = statusFilter.value === 'all' || order.status === statusFilter.value

    return searchMatch && statusMatch
  })
})

// 获取订单状态显示文本和颜色
const getStatusInfo = (status) => {
  const statusMap = {
    1: { text: '待确认', color: 'warning' },
    2: { text: '已接单', color: 'info' },
    3: { text: '已完成', color: 'success' },
    4: { text: '已取消', color: 'error' }
  }
  return statusMap[status] || { text: '未知', color: 'grey' }
}

// 获取订单列表
function getOrders() {
  loading.value = true
  http.get("/appointmentOrder/list").then(res => {
    orders.value = res.data.data
    loading.value = false
  })
}

// 打开添加订单对话框
function openAddDialog() {
  isEdit.value = false
  dialogTitle.value = '添加预约订单'
  editedOrder.value = {
    userId: '',
    appointmentTime: '',
    address: '',
    contactName: '',
    contactPhone: '',
    status: 1,
    notes: '',
    longitude: '',
    latitude: '',
    mapAddress: ''
  }
  dialog.value = true
}

// 打开编辑订单对话框
function openEditDialog(order) {
  isEdit.value = true
  dialogTitle.value = '编辑预约订单'
  editedOrder.value = { ...order }
  dialog.value = true
}

// 在 script setup 中添加
const searchAddressInput = ref('')

// 确认地址选择
const confirmAddress = () => {
  mapVisible.value = false
  // 可以在这里添加其他确认逻辑
}

// 修改表单验证
const validateForm = () => {
  if (!editedOrder.value.address) {
    message.error('请选择上门地址')
    return false
  }
  // 添加其他验证...
  return true
}

// 修改保存方法
function saveOrder() {
  if (!validateForm()) return
  
  loading.value = true
  const request = isEdit.value
    ? http.post('/appointmentOrder/update', editedOrder.value)
    : http.post('/appointmentOrder', editedOrder.value)
    
  request.then(res => {
    if (res.data.code === 1) {
      dialog.value = false
      getOrders()
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

// 删除订单
function deleteOrder(id) {
  if (!window.confirm('确定要删除该预约订单吗？')) {
    return
  }
  
  loading.value = true
  http.delete(`/appointmentOrder/${id}`).then(res => {
    if (res.data.code === 1) {
      getOrders()
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

// 地图相关
const map = ref(null)
const mapContainer = ref(null)
const marker = ref(null)
const mapVisible = ref(false)

// 根据经纬度更新地址信息
const updateAddressFromLocation = async (lngLat) => {
  try {
    // 确保 lngLat 是正确的格式
    const lng = typeof lngLat.getLng === 'function' ? lngLat.getLng() : lngLat[0]
    const lat = typeof lngLat.getLat === 'function' ? lngLat.getLat() : lngLat[1]

    const result = await mapUtils.getAddressByLocation([lng, lat])
    
    // 更新表单数据
    editedOrder.value.longitude = lng
    editedOrder.value.latitude = lat
    editedOrder.value.mapAddress = result.formattedAddress
    editedOrder.value.address = result.formattedAddress
  } catch (error) {
    console.error('获取地址信息失败：', error)
    message.error('获取地址信息失败')
  }
}

// 搜索地址
const handleAddressSearch = async (address) => {
  try {
    const result = await mapUtils.getLocationByAddress(address)
    const location = result.location
    // 构造经纬度数组
    const lngLat = [location.lng, location.lat]
    map.value.setCenter([location.lng, location.lat])
    marker.value.setPosition([location.lng, location.lat])
    await updateAddressFromLocation(lngLat)
  } catch (error) {
    console.error('搜索地址失败：', error)
    message.error('搜索地址失败')
  }
}

// 初始化地图
const initMap = async () => {
  try {
    const AMap = await initAMap()
    
    // 创建地图实例
    map.value = new AMap.Map(mapContainer.value, {
      zoom: mapConfig.zoom,
      center: mapConfig.center
    })

    // 创建标记点
    marker.value = new AMap.Marker({
      draggable: true,
      map: map.value
    })

    // 监听标记点拖动结束事件
    marker.value.on('dragend', async (e) => {
      const lngLat = e.target.getPosition()
      await updateAddressFromLocation(lngLat)
    })
    
    // 点击地图更新标记点位置
    map.value.on('click', async (e) => {
      marker.value.setPosition(e.lnglat)
      await updateAddressFromLocation(e.lnglat)
    })

    // 尝试获取当前位置并定位
    try {
      const result = await mapUtils.getCurrentLocation(map.value)
      // 构造经纬度数组
      const position = [result.position.lng, result.position.lat]
      map.value.setCenter(position)
      marker.value.setPosition(position)
      await updateAddressFromLocation(position)
    } catch (error) {
      console.warn('获取当前位置失败：', error)
    }

  } catch (e) {
    console.error('地图加载失败：', e)
    message.error('地图加载失败')
  }
}

// 打开地图选择器
const openMapSelector = () => {
  mapVisible.value = true
  // 确保地图容器已经渲染
  setTimeout(() => {
    if (!map.value) {
      initMap()
    }
  }, 100)
}

onMounted(() => {
  getOrders()
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
          添加预约
        </v-btn>
      </v-col>
      
      <!-- 搜索框 -->
      <v-col cols="12" sm="6" md="3">
        <v-text-field
          v-model="search"
          label="搜索预约"
          prepend-inner-icon="mdi-magnify"
          single-line
          hide-details
          variant="outlined"
          density="compact"
        ></v-text-field>
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

    <!-- 订单列表 -->
    <v-card>
      <v-data-table
        :headers="headers"
        :items="filteredOrders"
        :loading="loading"
        hover
      >
        <!-- 预约时间列格式化 -->
        <template v-slot:item.appointmentTime="{ item }">
          {{ new Date(item.appointmentTime).toLocaleString() }}
        </template>

        <!-- 状态列自定义显示 -->
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
          <div class="d-flex align-center">
            <v-btn
              icon
              variant="text"
              color="primary"
              size="small"
              @click="openEditDialog(item)"
              class="mr-2"
            >
              <Icon icon="mdi-pencil" />
            </v-btn>
            <v-btn
              icon
              variant="text"
              color="error"
              size="small"
              @click="deleteOrder(item.id)"
            >
              <Icon icon="mdi-delete" />
            </v-btn>
          </div>
        </template>
      </v-data-table>
    </v-card>

    <!-- 添加/编辑订单对话框 -->
    <v-dialog v-model="dialog" max-width="600">
      <v-card>
        <v-card-title>{{ dialogTitle }}</v-card-title>
        <v-card-text>
          <v-form @submit.prevent="saveOrder">
            <v-row>
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedOrder.userId"
                  label="用户ID"
                  type="number"
                  required
                ></v-text-field>
              </v-col>
              
              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedOrder.appointmentTime"
                  label="预约时间"
                  type="datetime-local"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedOrder.contactName"
                  label="联系人"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12" sm="6">
                <v-text-field
                  v-model="editedOrder.contactPhone"
                  label="联系电话"
                  required
                ></v-text-field>
              </v-col>

              <v-col cols="12">
                <div class="d-flex flex-column">
                  <div class="d-flex align-center mb-2">
                    <v-text-field
                      v-model="editedOrder.address"
                      label="上门地址"
                      readonly
                      required
                      class="flex-grow-1 mr-2"
                    ></v-text-field>
                    <v-btn
                      color="primary"
                      variant="outlined"
                      @click="openMapSelector"
                    >
                      <Icon icon="tabler:map-pin" class="mr-2" />
                      选择地址
                    </v-btn>
                  </div>
                  <div class="d-flex align-center text-caption text-medium-emphasis">
                    <Icon icon="tabler:info-circle" class="mr-1" />
                    请点击"选择地址"按钮在地图上选择准确的上门地址
                  </div>
                </div>
              </v-col>

              <v-col cols="12">
                <v-select
                  v-model="editedOrder.status"
                  :items="statusOptions.filter(item => item.value !== 'all')"
                  label="订单状态"
                  item-title="title"
                  item-value="value"
                  required
                ></v-select>
              </v-col>

              <v-col cols="12">
                <v-textarea
                  v-model="editedOrder.notes"
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
            @click="saveOrder"
            :loading="loading"
            :disabled="!editedOrder.address"
          >
            保存
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 地图选择对话框 -->
    <v-dialog v-model="mapVisible" max-width="800">
      <v-card>
        <v-card-title class="d-flex align-center pa-4">
          <span class="text-h6">选择上门地址</span>
          <v-spacer></v-spacer>
          <v-text-field
            v-model="searchAddressInput"
            label="搜索地址"
            append-inner-icon="mdi-magnify"
            @click:append-inner="handleAddressSearch(searchAddressInput)"
            @keyup.enter="handleAddressSearch(searchAddressInput)"
            hide-details
            density="compact"
            class="mx-4"
            style="max-width: 300px"
          ></v-text-field>
        </v-card-title>
        
        <v-card-text class="pa-4">
          <div ref="mapContainer" style="height: 400px; border-radius: 8px;"></div>
          <div class="mt-4 pa-2 bg-grey-lighten-4 rounded-lg">
            <div class="d-flex align-center mb-2">
              <Icon icon="tabler:map-pin" class="mr-2" color="primary" />
              <strong>当前选择的地址：</strong>
            </div>
            <div>{{ editedOrder.address || '尚未选择地址' }}</div>
            <div class="text-caption text-medium-emphasis mt-1">
              <Icon icon="tabler:info-circle" class="mr-1" />
              可以通过搜索、点击地图或拖动标记点来选择地址
            </div>
          </div>
        </v-card-text>
        
        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
            color="grey-darken-1"
            variant="text"
            @click="mapVisible = false"
          >
            取消
          </v-btn>
          <v-btn
            color="primary"
            variant="text"
            @click="confirmAddress"
            :disabled="!editedOrder.address"
          >
            确认选择
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

/* 添加表格相关样式 */
:deep(.v-data-table) {
  background: transparent;
}

:deep(.v-data-table-header) {
  background-color: rgba(var(--v-theme-primary), 0.05);
}

:deep(.v-data-table-row:hover) {
  background-color: rgba(var(--v-theme-primary), 0.05) !important;
}

/* 表单样式 */
.v-text-field :deep(.v-field--variant-outlined .v-field__outline__start),
.v-select :deep(.v-field--variant-outlined .v-field__outline__start) {
  border-radius: 8px 0 0 8px;
}

.v-text-field :deep(.v-field--variant-outlined .v-field__outline__end),
.v-select :deep(.v-field--variant-outlined .v-field__outline__end) {
  border-radius: 0 8px 8px 0;
}

/* 按钮样式 */
.v-btn {
  text-transform: none;
  letter-spacing: 0.5px;
}

/* 对话框动画 */
.v-dialog-transition-enter-active,
.v-dialog-transition-leave-active {
  transition: all 0.3s ease;
}

.v-dialog-transition-enter-from,
.v-dialog-transition-leave-to {
  transform: scale(0.9);
  opacity: 0;
}

/* 地图容器样式 */
.amap-container {
  width: 100%;
  height: 100%;
}

/* 地图搜索框样式 */
:deep(.v-field.v-field--appended) {
  --v-field-padding-end: 8px;
}
</style> 