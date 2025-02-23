<template>
  <div class="data-table-wrapper">
    <!-- 顶部操作栏 -->
    <v-card class="mb-6 rounded-lg">
      <v-card-text>
        <v-row align="center">
          <!-- 搜索框 -->
          <v-col cols="12" sm="4" v-if="showSearch">
            <v-text-field
              v-model="searchValue"
              prepend-inner-icon="mdi-magnify"
              :label="searchLabel"
              density="comfortable"
              variant="outlined"
              hide-details
              class="rounded-lg search-field"
              @update:model-value="handleSearch"
            ></v-text-field>
          </v-col>

          <!-- 过滤器 -->
          <v-col cols="12" sm="4" v-if="showFilter">
            <slot name="filter"></slot>
          </v-col>

          <!-- 工具栏操作按钮 -->
          <v-col :cols="12" :sm="toolbarColSize" class="text-sm-right">
            <slot name="toolbar-actions"></slot>
          </v-col>
        </v-row>
      </v-card-text>
    </v-card>

    <!-- 数据表格 -->
    <v-card class="rounded-lg">
      <v-data-table
        v-model:page="page"
        v-model:items-per-page="itemsPerPage"
        :headers="headers"
        :items="items"
        :loading="loading"
        :items-per-page-options="itemsPerPageOptions"
        :search="searchValue"
        hover
      >
        <!-- 动态插槽 -->
        <template v-for="(_, name) in $slots" :key="name" v-slot:[name]="slotData">
          <slot :name="name" v-bind="slotData"></slot>
        </template>

        <!-- 加载中状态 -->
        <template v-slot:loading>
          <v-skeleton-loader
            type="table-row-divider@6"
            class="px-4"
          ></v-skeleton-loader>
        </template>

        <!-- 空数据状态 -->
        <template v-slot:no-data>
          <div class="d-flex flex-column align-center py-8">
            <v-icon icon="mdi-database-off" size="48" color="grey-lighten-1" class="mb-4" />
            <div class="text-grey">暂无数据</div>
          </div>
        </template>

        <!-- 自定义分页器 -->
        <template v-slot:bottom>
          <div class="d-flex align-center justify-center py-4">
            <v-container class="custom-pagination px-0">
              <v-row justify="center" align="center" no-gutters>
                <v-btn
                  :disabled="page === 1"
                  icon="mdi-chevron-left"
                  variant="text"
                  color="primary"
                  class="mx-2 nav-btn"
                  @click="handlePageChange(page - 1)"
                ></v-btn>

                <div class="page-numbers">
                  <v-btn
                    v-for="n in displayedPages"
                    :key="n"
                    :class="{ 'page-active': page === n }"
                    class="page-btn mx-1"
                    variant="text"
                    @click="handlePageChange(n)"
                  >
                    {{ n }}
                  </v-btn>
                </div>

                <v-btn
                  :disabled="page === pageCount"
                  icon="mdi-chevron-right"
                  variant="text"
                  color="primary"
                  class="mx-2 nav-btn"
                  @click="handlePageChange(page + 1)"
                ></v-btn>
              </v-row>
            </v-container>
          </div>
        </template>
      </v-data-table>
    </v-card>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import debounce from 'lodash/debounce'

// Props 定义
const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  headers: {
    type: Array,
    required: true
  },
  items: {
    type: Array,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  },
  showSearch: {
    type: Boolean,
    default: false
  },
  showFilter: {
    type: Boolean,
    default: false
  },
  showItemsPerPage: {
    type: Boolean,
    default: true
  },
  searchLabel: {
    type: String,
    default: '搜索'
  },
  itemsPerPageOptions: {
    type: Array,
    default: () => [5, 10, 15, 20]
  },
  onSearch: {
    type: Function,
    default: null
  }
})
// Emits 定义
const emit = defineEmits([
  'update:page',
  'update:itemsPerPage',
  'update:search'
])

// 状态变量
const page = ref(1)
const itemsPerPage = ref(10)
const searchValue = ref('')

// 计算工具栏列宽
const toolbarColSize = computed(() => {
  let size = 12
  if (props.showSearch) size -= 4
  if (props.showFilter) size -= 4
  return size
})

// 计算总页数
const pageCount = computed(() => Math.ceil(props.items.length / itemsPerPage.value))

// 计算显示的页码
const displayedPages = computed(() => {
  const total = pageCount.value
  const current = page.value
  const delta = 2 // 当前页前后显示的页数
  
  let pages = []
  
  if (total <= 5) {
    pages = Array.from({ length: total }, (_, i) => i + 1)
  } else {
    const left = Math.max(1, current - delta)
    const right = Math.min(total, current + delta)
    
    pages = Array.from(
      { length: right - left + 1 },
      (_, i) => left + i
    )
  }
  
  return pages
})

// 处理搜索
const handleSearch = debounce((value) => {
  if (props.onSearch) {
    props.onSearch(value)
  }
  emit('update:search', value)
  page.value = 1 // 重置页码
}, 300)

// 处理页码变化
const handlePageChange = (newPage) => {
  page.value = newPage
  emit('update:page', newPage)
}

// 监听每页条数变化
watch(itemsPerPage, (newVal) => {
  emit('update:itemsPerPage', newVal)
  page.value = 1 // 重置页码
})
</script>

<style scoped>
.data-table-wrapper {
  height: 100%;
}

/* 表格容器样式 */
:deep(.v-data-table) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 25px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

/* 表格头部样式 */
:deep(.v-data-table-header) {
  background: linear-gradient(145deg, rgba(var(--v-theme-primary), 0.05), rgba(var(--v-theme-primary), 0.1));
}

:deep(.v-data-table-header th) {
  font-size: 0.875rem !important;
  font-weight: 600 !important;
  color: var(--v-theme-primary) !important;
  text-transform: none !important;
  letter-spacing: 0 !important;
  padding: 12px 16px !important;
  white-space: nowrap !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
}

:deep(.v-data-table-header__content) {
  width: 100% !important;
  white-space: nowrap !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  display: block !important;
}

:deep(.v-data-table-header-cell__content) {
  width: 100% !important;
  white-space: nowrap !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  display: block !important;
}

:deep(.v-data-table-header-cell__wrapper) {
  width: 100% !important;
  white-space: nowrap !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  display: block !important;
}

/* 表格行样式 */
:deep(.v-data-table-row) {
  transition: all 0.2s ease;
}

:deep(.v-data-table-row:hover) {
  background-color: rgba(var(--v-theme-primary), 0.05) !important;
  transform: translateY(-1px);
}

/* 表格单元格样式 */
:deep(.v-data-table .v-data-table__td) {
  padding: 12px 16px !important;
  font-size: 0.875rem;
}

/* 搜索框样式 */
.search-field {
  max-width: 300px;
}

:deep(.v-field__input) {
  padding: 8px 16px !important;
  font-size: 0.875rem;
  background: rgba(var(--v-theme-primary), 0.05);
  border-radius: 12px;
  transition: all 0.3s ease;
}

:deep(.v-field__input:hover) {
  background: rgba(var(--v-theme-primary), 0.08);
}

:deep(.v-field__input:focus) {
  background: white;
  box-shadow: 0 0 0 1px var(--v-theme-primary);
}

/* 分页器样式 */
.custom-pagination {
  max-width: 600px;
}

.page-numbers {
  display: flex;
  align-items: center;
}

.page-btn {
  min-width: 36px !important;
  width: 36px !important;
  height: 36px !important;
  padding: 0 !important;
  margin: 0 4px;
  border-radius: 50% !important;
  font-weight: 500;
  color: rgba(0, 0, 0, 0.87);
  background: transparent;
  letter-spacing: 0;
}

.page-btn:hover {
  background: rgba(0, 184, 148, 0.1) !important;
}

.page-active {
  background: linear-gradient(145deg, #00B894, #00CEC9) !important;
  color: white !important;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 184, 148, 0.2);
}

.nav-btn {
  background: transparent !important;
  border-radius: 50% !important;
}

.nav-btn:hover {
  background: rgba(0, 184, 148, 0.1) !important;
}

.nav-btn:disabled {
  opacity: 0.5 !important;
  background: transparent !important;
}

.nav-btn :deep(.v-btn__content) {
  color: #00B894;
}

.nav-btn:disabled :deep(.v-btn__content) {
  color: rgba(0, 0, 0, 0.38);
}

/* 加载状态样式 */
:deep(.v-data-table-loader) {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
}

/* 空状态样式 */
:deep(.v-data-table__empty-wrapper) {
  padding: 48px;
  text-align: center;
  color: rgba(var(--v-theme-on-surface), 0.6);
}
</style> 