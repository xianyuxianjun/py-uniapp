<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="left" @click="navigateBack">
				<uni-icons type="left" size="20" color="#333"></uni-icons>
			</view>
			<text class="title">我的订单</text>
		</view>
		
		<!-- 订单列表 -->
		<scroll-view class="order-list" scroll-y @scrolltolower="loadMore" refresher-enabled @refresherrefresh="refresh" :refresher-triggered="refreshing">
			<template v-if="orders.length > 0">
				<view class="order-item" v-for="order in orders" :key="order.id">
					<view class="order-header">
						<text class="order-time">{{ formatDate(order.createdAt) }}</text>
						<text :class="['order-status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</text>
					</view>
					
					<view class="order-content">
						<view class="info-item">
							<text class="label">预约时间</text>
							<text class="value">{{ formatDate(order.appointmentTime) }}</text>
						</view>
						
						<view class="info-item">
							<text class="label">联系人</text>
							<text class="value">{{ order.contactName }}</text>
						</view>
						
						<view class="info-item">
							<text class="label">联系电话</text>
							<text class="value">{{ order.contactPhone }}</text>
						</view>
						
						<view class="info-item address">
							<text class="label">上门地址</text>
							<text class="value">{{ order.address }}</text>
						</view>
						
						<view class="info-item" v-if="order.notes">
							<text class="label">备注信息</text>
							<text class="value">{{ order.notes }}</text>
						</view>
					</view>
					
					<view class="order-footer">
						<button 
							class="cancel-btn" 
							v-if="order.status === 1"
							@click="cancelOrder(order.id)"
						>取消预约</button>
					</view>
				</view>
			</template>
			
			<view v-else class="empty-state">
				<image src="/static/images/empty-orders.png" mode="aspectFit"></image>
				<text>暂无预约订单</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			orders: [],
			refreshing: false,
			loading: false
		}
	},
	
	onLoad() {
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.loadOrders()
	},
	
	methods: {
		// 返回上一页
		navigateBack() {
			uni.navigateBack()
		},
		
		// 加载订单数据
		async loadOrders() {
			try {
				const userId = uni.getStorageSync('userInfo').id
				const res = await uni.request({
					url: `http://localhost:8080/appointmentOrder/user/${userId}`,
					method: 'GET'
				})
				
				if (res.data.code === 1) {
					this.orders = res.data.data
				} else {
					throw new Error(res.data.msg)
				}
			} catch (err) {
				console.error('获取订单失败：', err)
				uni.showToast({
					title: '获取订单失败',
					icon: 'none'
				})
			}
		},
		
		// 下拉刷新
		async refresh() {
			this.refreshing = true
			await this.loadOrders()
			this.refreshing = false
		},
		
		// 取消订单
		async cancelOrder(orderId) {
			uni.showModal({
				title: '提示',
				content: '确定要取消这个预约吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							const result = await uni.request({
								url: `http://localhost:8080/appointmentOrder/cancel/${orderId}`,
								method: 'POST'
							})
							
							if (result.data.code === 1) {
								uni.showToast({
									title: '取消成功',
									icon: 'success'
								})
								this.loadOrders()
							} else {
								throw new Error(result.data.msg)
							}
						} catch (err) {
							uni.showToast({
								title: '取消失败',
								icon: 'none'
							})
						}
					}
				}
			})
		},
		
		// 格式化日期
		formatDate(dateStr) {
			if (!dateStr) return ''
			const date = new Date(dateStr)
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				1: '待确认',
				2: '已接单',
				3: '已完成',
				4: '已取消'
			}
			return statusMap[status] || '未知状态'
		},
		
		// 获取状态样式类
		getStatusClass(status) {
			const classMap = {
				1: 'pending',
				2: 'processing',
				3: 'completed',
				4: 'cancelled'
			}
			return classMap[status] || ''
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background-color: #f5f7fa;
	display: flex;
	flex-direction: column;
}

.nav-bar {
	height: 44px;
	display: flex;
	align-items: center;
	padding: 0 16px;
	background: #fff;
	position: relative;
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
	
	.left {
		padding: 8px;
		margin-left: -8px;
		
		&:active {
			opacity: 0.7;
		}
	}
	
	.title {
		position: absolute;
		left: 50%;
		transform: translateX(-50%);
		font-size: 17px;
		font-weight: 600;
		color: #333;
	}
}

.order-list {
	flex: 1;
	padding: 20rpx;
}

.order-item {
	background: #fff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	padding: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
	
	.order-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
		padding-bottom: 20rpx;
		border-bottom: 1px solid rgba(0, 0, 0, 0.05);
		
		.order-time {
			font-size: 26rpx;
			color: #666;
		}
		
		.order-status {
			font-size: 26rpx;
			font-weight: 600;
			padding: 4rpx 16rpx;
			border-radius: 8rpx;
			
			&.pending {
				color: #ff9800;
				background: rgba(255, 152, 0, 0.1);
			}
			
			&.processing {
				color: #2979ff;
				background: rgba(41, 121, 255, 0.1);
			}
			
			&.completed {
				color: #4caf50;
				background: rgba(76, 175, 80, 0.1);
			}
			
			&.cancelled {
				color: #999;
				background: rgba(0, 0, 0, 0.05);
			}
		}
	}
	
	.order-content {
		.info-item {
			display: flex;
			margin-bottom: 16rpx;
			
			&:last-child {
				margin-bottom: 0;
			}
			
			&.address {
				align-items: flex-start;
			}
			
			.label {
				width: 140rpx;
				font-size: 26rpx;
				color: #999;
				flex-shrink: 0;
			}
			
			.value {
				flex: 1;
				font-size: 26rpx;
				color: #333;
			}
		}
	}
	
	.order-footer {
		margin-top: 24rpx;
		padding-top: 24rpx;
		border-top: 1px solid rgba(0, 0, 0, 0.05);
		display: flex;
		justify-content: flex-end;
		
		.cancel-btn {
			font-size: 26rpx;
			color: #666;
			background: #f5f5f5;
			padding: 12rpx 32rpx;
			border-radius: 8rpx;
			margin: 0;
			
			&:active {
				opacity: 0.8;
			}
		}
	}
}

.empty-state {
	padding: 120rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	
	image {
		width: 240rpx;
		height: 240rpx;
		margin-bottom: 30rpx;
	}
	
	text {
		font-size: 28rpx;
		color: #999;
	}
}
</style> 