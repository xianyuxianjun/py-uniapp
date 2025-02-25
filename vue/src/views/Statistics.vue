<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import http from '@/utils/http'
import * as echarts from 'echarts'

// 图表容器引用
const categoryChartRef = ref(null)
const trendChartRef = ref(null) 
const rankingChartRef = ref(null)
const satisfactionChartRef = ref(null)
const heatmapRef = ref(null)

// 图表实例
const charts = ref({
  categoryChart: null,
  trendChart: null,
  rankingChart: null,
  satisfactionChart: null,
  heatmap: null
})

// 日期范围
const dateRange = ref([
  new Date(), // 今天
  new Date(Date.now() - 30*24*60*60*1000) // 30天前
])

// 时间单位选择
const timeUnit = ref('day')
const timeUnitOptions = [
  { title: '按天', value: 'day' },
  { title: '按周', value: 'week' },
  { title: '按月', value: 'month' }
]

// 添加日期选择器相关的响应式变量
const menu = ref(false)
const dateRangeText = computed(() => {
  if (!dateRange.value || dateRange.value.length !== 2) return ''
  const [end, start] = dateRange.value
  return `${formatDate(start).split(' ')[0]} ~ ${formatDate(end).split(' ')[0]}`
})

// 格式化日期显示
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} 00:00:00`
}

// 初始化废品分类统计图表
const initCategoryChart = (data) => {
  const chart = echarts.init(categoryChartRef.value)
  charts.value.categoryChart = chart
  
  const option = {
    title: {
      text: '废品分类统计',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        return `${params.name}<br/>重量: ${params.value}kg<br/>金额: ${params.data.amount.toLocaleString('zh-CN')}元`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],  // 改成环形图
        center: ['60%', '50%'],  // 调整图表位置
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {d}%'
        },
        data: data.map(item => ({
          name: item.category_name,
          value: item.total_weight,
          amount: item.total_amount
        }))
      }
    ]
  }
  
  chart.setOption(option)
}

// 初始化订单趋势图表
const initTrendChart = (data) => {
  const chart = echarts.init(trendChartRef.value)
  charts.value.trendChart = chart
  
  const option = {
    title: {
      text: '订单趋势分析',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const date = params[0].axisValue
        const orders = params[0].value
        const amount = params[1].value
        return `日期: ${date}<br/>订单数: ${orders}单<br/>金额: ${amount.toLocaleString('zh-CN')}元`
      }
    },
    legend: {
      data: ['订单数量', '订单金额'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map(item => item.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数量',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单金额(元)',
        position: 'right',
        axisLabel: {
          formatter: (value) => value.toLocaleString('zh-CN')
        }
      }
    ],
    series: [
      {
        name: '订单数量',
        type: 'line',
        smooth: true,
        data: data.map(item => item.order_count)
      },
      {
        name: '订单金额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: data.map(item => item.daily_amount)
      }
    ]
  }
  
  chart.setOption(option)
}

// 初始化回收员排行榜
const initRankingChart = (data) => {
  const chart = echarts.init(rankingChartRef.value)
  charts.value.rankingChart = chart
  
  // 定义渐变色
  const colorStops = [
    { offset: 0, color: '#83bff6' },
    { offset: 0.5, color: '#188df0' },
    { offset: 1, color: '#188df0' }
  ]
  
  const option = {
    title: {
      text: '回收员业绩排行',
      left: 'center',
      top: 10
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params) => {
        const data = params[0]
        return `
          <div style="padding: 3px;">
            <div style="font-weight: bold; margin-bottom: 3px;">${data.name}</div>
            <div>回收金额: ${data.value.toLocaleString('zh-CN')}元</div>
            <div>订单数量: ${data.data.total_orders}单</div>
            <div>平均单价: ${(data.value / data.data.total_orders).toFixed(2)}元/单</div>
          </div>
        `
      }
    },
    grid: {
      top: '15%',
      left: '15%',
      right: '10%',
      bottom: '10%'
    },
    xAxis: {
      type: 'value',
      name: '回收金额(元)',
      nameTextStyle: {
        padding: [0, 0, 0, 20]
      },
      axisLabel: {
        formatter: (value) => value.toLocaleString('zh-CN'),
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#eee'
        }
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: '#ddd'
        }
      }
    },
    yAxis: {
      type: 'category',
      data: data.map(item => item.recycler_name),
      axisLabel: {
        interval: 0,
        margin: 15,
        color: '#666',
        fontWeight: 'bold'
      },
      axisTick: {
        show: false
      },
      axisLine: {
        show: true,
        lineStyle: {
          color: '#ddd'
        }
      }
    },
    series: [
      {
        type: 'bar',
        data: data.map(item => ({
          value: item.total_amount,
          total_orders: item.total_orders
        })),
        label: {
          show: true,
          position: 'right',
          formatter: (params) => params.value.toLocaleString('zh-CN') + '元',
          fontSize: 12,
          color: '#666',
          fontWeight: 'bold'
        },
        itemStyle: {
          borderRadius: [0, 4, 4, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, colorStops)
        },
        barWidth: '60%',
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#a8d1f8' },
              { offset: 0.5, color: '#4ba1f0' },
              { offset: 1, color: '#4ba1f0' }
            ])
          }
        }
      },
      // 添加装饰性小圆点
      {
        type: 'scatter',
        symbolSize: 8,
        data: data.map((item, index) => ({
          value: [-5, index],
          itemStyle: {
            color: '#188df0'
          }
        })),
        silent: true,
        z: 3
      }
    ]
  }
  
  chart.setOption(option)
}

// 初始化满意度分布图表
const initSatisfactionChart = (data) => {
  const chart = echarts.init(satisfactionChartRef.value)
  charts.value.satisfactionChart = chart
  
  const option = {
    title: {
      text: '用户满意度分布',
      left: 'center'
    },
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        return `${params.name}<br/>数量: ${params.value}个<br/>占比: ${params.percent}%`
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle'
    },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          formatter: '{b}: {d}%'
        },
        data: data.map(item => ({
          name: `${item.rating}星`,
          value: item.rating_count
        }))
      }
    ]
  }
  
  chart.setOption(option)
}

// 加载数据
const loadData = async () => {
  try {
    // 使用模拟数据
    const mockData = {
      // 废品分类统计模拟数据
      categoryStats: [
        { category_name: '废纸', total_weight: 1200, total_amount: 2400 },
        { category_name: '塑料', total_weight: 800, total_amount: 1600 },
        { category_name: '金属', total_weight: 500, total_amount: 2500 },
        { category_name: '玻璃', total_weight: 300, total_amount: 600 },
        { category_name: '电子废物', total_weight: 200, total_amount: 1000 }
      ],
      
      // 订单趋势模拟数据
      orderTrend: Array.from({ length: 7 }, (_, i) => ({
        date: new Date(Date.now() - (6 - i) * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        order_count: Math.floor(Math.random() * 50) + 20,
        daily_amount: Math.floor(Math.random() * 5000) + 2000
      })),
      
      // 回收员排行模拟数据
      recyclerRanking: [
        { recycler_name: '张三', total_orders: 120, total_amount: 12000 },
        { recycler_name: '李四', total_orders: 100, total_amount: 10000 },
        { recycler_name: '王五', total_orders: 80, total_amount: 8000 },
        { recycler_name: '赵六', total_orders: 60, total_amount: 6000 },
        { recycler_name: '孙七', total_orders: 40, total_amount: 4000 }
      ],
      
      // 满意度分布模拟数据
      satisfactionStats: [
        { rating: 5, rating_count: 50 },
        { rating: 4, rating_count: 30 },
        { rating: 3, rating_count: 15 },
        { rating: 2, rating_count: 3 },
        { rating: 1, rating_count: 2 }
      ]
    }

    // 使用模拟数据初始化图表
    initCategoryChart(mockData.categoryStats)
    initTrendChart(mockData.orderTrend)
    initRankingChart(mockData.recyclerRanking)
    initSatisfactionChart(mockData.satisfactionStats)

    /* 暂时注释掉实际的API调用
    const startDate = formatDate(dateRange.value[1])
    const endDate = formatDate(dateRange.value[0])

    // 获取废品分类统计
    const categoryRes = await http.get('/api/statistics/category-stats', {
      params: { startDate, endDate }
    })
    if(categoryRes.data.code === 1) {
      initCategoryChart(categoryRes.data.data)
    }

    // 获取订单趋势
    const trendRes = await http.get('/api/statistics/order-trend', {
      params: { startDate, endDate, timeUnit: timeUnit.value }
    })
    if(trendRes.data.code === 1) {
      initTrendChart(trendRes.data.data)
    }

    // 获取回收员排行
    const rankingRes = await http.get('/api/statistics/recycler-ranking')
    if(rankingRes.data.code === 1) {
      initRankingChart(rankingRes.data.data)
    }

    // 获取满意度分布
    const satisfactionRes = await http.get('/api/statistics/satisfaction-stats')
    if(satisfactionRes.data.code === 1) {
      initSatisfactionChart(satisfactionRes.data.data)
    }
    */
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 监听窗口大小变化
const handleResize = () => {
  Object.values(charts.value).forEach(chart => {
    chart?.resize()
  })
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 销毁图表实例
  Object.values(charts.value).forEach(chart => {
    chart?.dispose()
  })
})
</script>

<template>
  <v-container fluid>
    <!-- 筛选条件 -->
    <v-row class="mb-4">
      <v-col cols="12" md="4">
        <v-card>
          <v-card-text>
            <v-row>
              <v-col cols="12">
                <v-menu
                  v-model="menu"
                  :close-on-content-click="false"
                >
                  <template v-slot:activator="{ props }">
                    <v-text-field
                      v-model="dateRangeText"
                      label="日期范围"
                      prepend-icon="mdi-calendar"
                      readonly
                      v-bind="props"
                    ></v-text-field>
                  </template>
                  <v-date-picker
                    v-model="dateRange"
                    range
                    @update:model-value="loadData"
                  ></v-date-picker>
                </v-menu>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="4">
        <v-select
          v-model="timeUnit"
          :items="timeUnitOptions"
          label="时间单位"
          @update:model-value="loadData"
        ></v-select>
      </v-col>
    </v-row>

    <!-- 图表展示区域 -->
    <v-row>
      <!-- 废品分类统计 -->
      <v-col cols="12" md="6">
        <v-card class="mb-4">
          <div ref="categoryChartRef" style="height: 400px;"></div>
        </v-card>
      </v-col>

      <!-- 订单趋势 -->
      <v-col cols="12" md="6">
        <v-card class="mb-4">
          <div ref="trendChartRef" style="height: 400px;"></div>
        </v-card>
      </v-col>

      <!-- 回收员排行 -->
      <v-col cols="12" md="6">
        <v-card class="mb-4">
          <div ref="rankingChartRef" style="height: 400px;"></div>
        </v-card>
      </v-col>

      <!-- 满意度分布 -->
      <v-col cols="12" md="6">
        <v-card class="mb-4">
          <div ref="satisfactionChartRef" style="height: 400px;"></div>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.v-card {
  border-radius: 8px;
}

:deep(.echarts) {
  width: 100%;
  height: 100%;
}
</style> 