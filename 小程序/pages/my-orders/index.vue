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
		// 修改返回按钮的处理方法
		navigateBack() {
			// 使用switchTab跳转到tabBar页面(首页)
			uni.switchTab({
				url: '/pages/index/index'
			})
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
	background-color: #f8f9fc;
	display: flex;
	flex-direction: column;
	box-sizing: border-box;
	width: 100%;
}

.status-bar {
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
}

.nav-bar {
	height: 44px;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0;
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	position: relative;
	width: 100%;
	box-sizing: border-box;
	
	.left {
		position: absolute;
		left: 8px;
		top: 50%;
		transform: translateY(-50%);
		width: 36px;
		height: 36px;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 50%;
		backdrop-filter: blur(5px);
		z-index: 2;
		
		&:active {
			background: rgba(255, 255, 255, 0.3);
		}
	}
	
	.title {
		font-size: 18px;
		font-weight: 600;
		color: #fff;
		position: relative;
		z-index: 1;
		padding: 0 44px;
		text-align: center;
	}
}

/* 优化订单列表，确保右侧内容可见 */
.order-list {
	flex: 1;
	padding: 20rpx;
	box-sizing: border-box;
	width: 100%;
}

.order-item {
	background: #fff;
	border-radius: 16rpx;
	margin-bottom: 24rpx;
	padding: 24rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
	width: 100%;
	box-sizing: border-box;
	overflow: hidden;
	
	.order-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-bottom: 20rpx;
		border-bottom: 1px solid #f0f0f0;
		width: 100%;
		box-sizing: border-box;
		
		.order-time {
			font-size: 28rpx;
			color: #666;
		}
		
		.order-status {
			font-size: 26rpx;
			padding: 4rpx 16rpx;
			border-radius: 30rpx;
			
			&.pending {
				color: #ff9800;
				background: rgba(255, 152, 0, 0.1);
			}
			
			&.completed {
				color: #4caf50;
				background: rgba(76, 175, 80, 0.1);
			}
			
			&.cancelled {
				color: #f44336;
				background: rgba(244, 67, 54, 0.1);
			}
		}
	}
	
	.order-content {
		padding: 20rpx 0;
		width: 100%;
		box-sizing: border-box;
		
		.info-item {
			display: flex;
			margin-bottom: 16rpx;
			width: 100%;
			box-sizing: border-box;
			
			&:last-child {
				margin-bottom: 0;
			}
			
			.label {
				width: 150rpx;
				flex-shrink: 0;
				font-size: 28rpx;
				color: #999;
			}
			
			.value {
				flex: 1;
				font-size: 28rpx;
				color: #333;
				word-break: break-all;
				padding-right: 10rpx;
			}
			
			&.address {
				.value {
					line-height: 1.4;
				}
			}
		}
	}
	
	.order-footer {
		display: flex;
		justify-content: flex-end;
		border-top: 1px solid #f0f0f0;
		padding-top: 20rpx;
		width: 100%;
		box-sizing: border-box;
		
		.cancel-btn {
			background: #fff;
			color: #ff5151;
			border: 1px solid #ff5151;
			font-size: 26rpx;
			padding: 8rpx 30rpx;
			border-radius: 30rpx;
			line-height: 1.5;
			height: auto;
			margin: 0;
		}
	}
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
	
	image {
		width: 200rpx;
		height: 200rpx;
		margin-bottom: 30rpx;
	}
	
	text {
		font-size: 28rpx;
		color: #999;
	}
}

// 添加下拉刷新样式
.order-list {
	.uni-scroll-view-refresh {
		.uni-scroll-view-refresh__spinner {
			color: #2979ff;
		}
		
		.uni-scroll-view-refresh__text {
			color: #666;
			font-size: 28rpx;
		}
	}
}
</style> 