<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<text class="title">我的单子</text>
		</view>
		
		<!-- 订单列表 -->
		<scroll-view class="order-list" scroll-y refresher-enabled 
			:refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			
			<!-- 订单卡片 -->
			<view class="order-items" v-if="orders.length > 0">
				<view class="order-item" v-for="order in orders" :key="order.id" @tap="viewOrderDetail(order)">
					<view class="order-header">
						<text class="order-id">订单号：{{order.id}}</text>
						<text class="order-status" :class="'status-' + order.status">
							{{getStatusText(order.status)}}
						</text>
					</view>
					
					<view class="order-content">
						<!-- 预约时间 -->
						<view class="info-row">
							<uni-icons type="calendar" size="16" color="#666"></uni-icons>
							<text class="label">预约时间：</text>
							<text class="value">{{formatDate(order.appointmentTime)}}</text>
						</view>
						
						<!-- 联系人信息 -->
						<view class="info-row">
							<uni-icons type="person" size="16" color="#666"></uni-icons>
							<text class="label">联系人：</text>
							<text class="value">{{order.contactName}}</text>
							<text class="phone">{{order.contactPhone}}</text>
						</view>
						
						<!-- 地址信息 -->
						<view class="info-row address">
							<uni-icons type="location" size="16" color="#666"></uni-icons>
							<text class="label">地址：</text>
							<text class="value">{{order.address}}</text>
						</view>
						
						<!-- 备注信息 -->
						<view class="info-row" v-if="order.notes">
							<uni-icons type="info" size="16" color="#666"></uni-icons>
							<text class="label">备注：</text>
							<text class="value">{{order.notes}}</text>
						</view>
					</view>
					
					<view class="order-footer">
						<view class="time-info">
							<text class="created-time">{{formatTime(order.createdAt)}}</text>
						</view>
						<view class="action-btns">
							<button class="action-btn complete" 
								v-if="order.status === 2"
								@tap.stop="handleCompleteOrder(order.id)"
							>完成订单</button>
							<button class="action-btn navigate" 
								@tap.stop="handleNavigate(order)"
							>导航</button>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-else>
				<image src="/static/images/empty-order.png" mode="aspectFit"></image>
				<text>暂无接单</text>
				<text class="sub-text">快去接单赚钱吧~</text>
			</view>
		</scroll-view>
		
		<uni-popup ref="popup" type="center">
			<complete-order-form :orderId="currentOrderId" @success="onFormSuccess" @cancel="onFormCancel" />
		</uni-popup>
	</view>
</template>

<script>
import CompleteOrderForm from '@/components/complete-order-form/complete-order-form.vue'

export default {
	components: {
		CompleteOrderForm
	},
	
	data() {
		return {
			statusBarHeight: 0,
			orders: [],
			refreshing: false,
			userInfo: null,
			currentOrderId: null
		}
	},
	
	onLoad() {
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.userInfo = uni.getStorageSync('userInfo')
		this.loadOrders()
	},
	
	methods: {
		// 加载订单列表
		async loadOrders() {
			if (!this.userInfo) return
			
			try {
				const res = await uni.request({
					url: `http://localhost:8080/appointmentOrder/recycler/list/${this.userInfo.id}`,
					method: 'GET'
				})
				
				if (res.data.code === 1) {
					this.orders = res.data.data
				} else {
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				}
			} catch (e) {
				console.error(e)
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				})
			}
		},
		
		// 下拉刷新
		async onRefresh() {
			this.refreshing = true
			await this.loadOrders()
			this.refreshing = false
		},
		
		// 格式化日期
		formatDate(dateStr) {
			if (!dateStr) return ''
			const date = new Date(dateStr)
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 格式化时间
		formatTime(dateStr) {
			if (!dateStr) return ''
			const date = new Date(dateStr)
			return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				1: '待确认',
				2: '进行中',
				3: '已完成',
				4: '已取消'
			}
			return statusMap[status] || '未知状态'
		},
		
		// 查看订单详情
		viewOrderDetail(order) {
			uni.navigateTo({
				url: `/pages/order-detail/index?id=${order.id}`
			})
		},
		
		// 完成订单
		handleCompleteOrder(orderId) {
			this.currentOrderId = orderId
			this.$refs.popup.open()
		},
		
		// 表单提交成功
		onFormSuccess() {
			this.$refs.popup.close()
			this.loadOrders()
		},
		
		// 表单取消
		onFormCancel() {
			this.$refs.popup.close()
		},
		
		// 导航到订单地址
		handleNavigate(order) {
			if (!order.latitude || !order.longitude) {
				uni.showToast({
					title: '无法获取地址坐标',
					icon: 'none'
				})
				return
			}
			
			uni.openLocation({
				latitude: Number(order.latitude),
				longitude: Number(order.longitude),
				name: order.contactName,
				address: order.address,
				fail: () => {
					uni.showToast({
						title: '导航失败',
						icon: 'none'
					})
				}
			})
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

.nav-bar {
	position: sticky;
	top: 0;
	z-index: 100;
	background: #fff;
	height: 44px;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 15px;
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
	
	.title {
		font-size: 16px;
		font-weight: 600;
		color: #333;
	}
}

.order-list {
	padding: 15px;
}

.order-item {
	background: #fff;
	border-radius: 12px;
	padding: 15px;
	margin-bottom: 15px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
	
	.order-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12px;
		
		.order-id {
			font-size: 14px;
			color: #666;
		}
		
		.order-status {
			font-size: 14px;
			font-weight: 500;
			
			&.status-1 {
				color: #2979ff;
			}
			
			&.status-2 {
				color: #ff9800;
			}
			
			&.status-3 {
				color: #4caf50;
			}
			
			&.status-4 {
				color: #999;
			}
		}
	}
	
	.order-content {
		.info-row {
			display: flex;
			align-items: center;
			margin-bottom: 10px;
			
			.label {
				font-size: 14px;
				color: #666;
				margin: 0 5px;
			}
			
			.value {
				font-size: 14px;
				color: #333;
			}
			
			.phone {
				margin-left: 10px;
				font-size: 14px;
				color: #666;
			}
			
			&.address {
				align-items: flex-start;
				
				.value {
					flex: 1;
					line-height: 1.4;
				}
			}
		}
	}
	
	.order-footer {
		margin-top: 15px;
		padding-top: 15px;
		border-top: 1px solid #f0f0f0;
		display: flex;
		justify-content: space-between;
		align-items: center;
		
		.time-info {
			.created-time {
				font-size: 14px;
				color: #999;
			}
		}
		
		.action-btns {
			display: flex;
			gap: 12px;
			
			.action-btn {
				margin: 0;
				padding: 0 20px;
				height: 36px;
				line-height: 36px;
				font-size: 14px;
				border-radius: 18px;
				position: relative;
				overflow: hidden;
				transition: all 0.3s ease;
				
				&.complete {
					color: #fff;
					background: linear-gradient(135deg, #4CAF50, #45a049);
					box-shadow: 0 2px 6px rgba(76, 175, 80, 0.3);
					
					&:active {
						transform: translateY(1px);
						box-shadow: 0 1px 3px rgba(76, 175, 80, 0.3);
					}
				}
				
				&.navigate {
					color: #fff;
					background: linear-gradient(135deg, #2979ff, #1565C0);
					box-shadow: 0 2px 6px rgba(41, 121, 255, 0.3);
					display: flex;
					align-items: center;
					justify-content: center;
					
					&::before {
						content: '';
						width: 16px;
						height: 16px;
						margin-right: 4px;
						background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="white"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/></svg>') no-repeat center;
						background-size: contain;
					}
					
					&:active {
						transform: translateY(1px);
						box-shadow: 0 1px 3px rgba(41, 121, 255, 0.3);
					}
				}
				
				&:disabled {
					opacity: 0.6;
					cursor: not-allowed;
				}
				
				&::after {
					border: none;
				}
			}
		}
	}
}

.empty-state {
	padding: 60px 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	
	image {
		width: 120px;
		height: 120px;
		margin-bottom: 15px;
	}
	
	text {
		font-size: 16px;
		color: #333;
		
		&.sub-text {
			font-size: 14px;
			color: #999;
			margin-top: 5px;
		}
	}
}
</style> 