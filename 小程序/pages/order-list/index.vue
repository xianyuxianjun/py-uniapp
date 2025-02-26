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
		
		<!-- 订单状态标签 -->
		<view class="status-tabs">
			<view class="tab-item" 
				:class="{ active: activeTab === 'all' }" 
				@tap="setActiveTab('all')">
				全部
			</view>
			<view class="tab-item" 
				:class="{ active: activeTab === 1 }" 
				@tap="setActiveTab(1)">
				待支付
			</view>
			<view class="tab-item" 
				:class="{ active: activeTab === 2 }" 
				@tap="setActiveTab(2)">
				已支付
			</view>
			<view class="tab-item" 
				:class="{ active: activeTab === 3 }" 
				@tap="setActiveTab(3)">
				已发货
			</view>
			<view class="tab-item" 
				:class="{ active: activeTab === 4 }" 
				@tap="setActiveTab(4)">
				已完成
			</view>
		</view>
		
		<!-- 订单列表 -->
		<scroll-view class="order-list" scroll-y refresher-enabled 
			:refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			
			<view class="order-items" v-if="filteredOrders.length > 0">
				<view class="order-item" v-for="order in filteredOrders" :key="order.id"
					@tap="viewOrderDetail(order)">
					<view class="order-header">
						<text class="order-no">订单号：{{ order.orderNo }}</text>
						<text class="order-status" :class="'status-' + order.status">{{ getStatusText(order.status) }}</text>
					</view>
					
					<view class="product-info">
						<image class="product-image" :src="order.productImage || '/static/images/default-product.png'" mode="aspectFill"></image>
						<view class="product-detail">
							<text class="product-name">{{ order.productName }}</text>
							<view class="price-quantity">
								<text class="price">¥{{ formatPrice(order.price) }}</text>
								<text class="quantity">x{{ order.quantity }}</text>
							</view>
						</view>
					</view>
					
					<view class="order-footer">
						<view class="total-section">
							<text class="total-label">合计：</text>
							<text class="total-price">¥{{ formatPrice(order.totalAmount) }}</text>
						</view>
						
						<view class="action-buttons">
							<button class="action-btn outline" v-if="order.status === 1" @tap.stop="cancelOrder(order)">取消订单</button>
							<button class="action-btn primary" v-if="order.status === 1" @tap.stop="payOrder(order)">立即支付</button>
							<button class="action-btn outline" v-if="order.status === 2" @tap.stop="checkDelivery(order)">查看物流</button>
							<button class="action-btn primary" v-if="order.status === 3" @tap.stop="confirmReceive(order)">确认收货</button>
							<button class="action-btn outline" v-if="order.status === 4" @tap.stop="deleteOrder(order)">删除订单</button>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="filteredOrders.length === 0 && !loading">
				<image src="/static/images/empty-order.png" mode="aspectFit"></image>
				<text>暂无订单</text>
				<text class="sub-text">去商城看看吧</text>
				<button class="go-shop-btn" @tap="goToShop">去购物</button>
			</view>
			
			<!-- 加载状态 -->
			<view class="loading-state" v-if="loading && orders.length === 0">
				<view class="loading-spinner"></view>
				<text>加载中...</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			activeTab: 'all',
			orders: [],
			loading: true,
			refreshing: false,
			baseUrl: 'http://localhost:8080'
		}
	},
	
	computed: {
		// 过滤后的订单列表
		filteredOrders() {
			if (this.activeTab === 'all') {
				return this.orders
			} else {
				return this.orders.filter(order => order.status === this.activeTab)
			}
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
		
		// 设置活动标签
		setActiveTab(tab) {
			this.activeTab = tab
		},
		
		// 加载订单列表
		async loadOrders() {
			this.loading = true
			
			try {
				const userInfo = uni.getStorageSync('userInfo')
				if (!userInfo || !userInfo.id) {
					uni.navigateTo({
						url: '/pages/login/login'
					})
					return
				}
				
				const res = await uni.request({
					url: `${this.baseUrl}/product/order/list`,
					method: 'GET',
					data: {
						userId: userInfo.id
					}
				})
				
				if (res.data.code === 1) {
					this.orders = res.data.data || []
				} else {
					throw new Error(res.data.msg || '获取订单列表失败')
				}
			} catch (err) {
				console.error('获取订单列表失败：', err)
				uni.showToast({
					title: '获取订单列表失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
				this.refreshing = false
			}
		},
		
		// 下拉刷新
		onRefresh() {
			this.refreshing = true
			this.loadOrders()
		},
		
		// 获取状态文本
		getStatusText(status) {
			switch (status) {
				case 1: return '待支付'
				case 2: return '已支付'
				case 3: return '已发货'
				case 4: return '已完成'
				case 5: return '已取消'
				default: return '未知状态'
			}
		},
		
		// 格式化价格
		formatPrice(price) {
			if (!price) return '0.00'
			return parseFloat(price).toFixed(2)
		},
		
		// 查看订单详情
		viewOrderDetail(order) {
			uni.navigateTo({
				url: `/pages/order-detail/index?orderNo=${order.orderNo}`
			})
		},
		
		// 取消订单
		async cancelOrder(order) {
			uni.showModal({
				title: '提示',
				content: '确定要取消该订单吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							const result = await uni.request({
								url: `${this.baseUrl}/product/order/cancel`,
								method: 'POST',
								data: {
									orderNo: order.orderNo
								}
							})
							
							if (result.data.code === 1) {
								uni.showToast({
									title: '订单已取消',
									icon: 'success'
								})
								// 重新加载订单列表
								setTimeout(() => {
									this.loadOrders()
								}, 1500)
							} else {
								throw new Error(result.data.msg || '取消订单失败')
							}
						} catch (err) {
							console.error('取消订单失败：', err)
							uni.showToast({
								title: '取消订单失败',
								icon: 'none'
							})
						}
					}
				}
			})
		},
		
		// 支付订单
		async payOrder(order) {
			uni.showModal({
				title: '模拟支付',
				content: '这是一个模拟支付功能，点击确定将直接标记为已支付',
				success: async (res) => {
					if (res.confirm) {
						try {
							const result = await uni.request({
								url: `${this.baseUrl}/product/order/pay`,
								method: 'POST',
								data: {
									orderNo: order.orderNo
								}
							})
							
							if (result.data.code === 1) {
								uni.showToast({
									title: '支付成功',
									icon: 'success'
								})
								// 重新加载订单列表
								setTimeout(() => {
									this.loadOrders()
								}, 1500)
							} else {
								throw new Error(result.data.msg || '支付订单失败')
							}
						} catch (err) {
							console.error('支付订单失败：', err)
							uni.showToast({
								title: '支付订单失败',
								icon: 'none'
							})
						}
					}
				}
			})
		},
		
		// 查看物流
		checkDelivery(order) {
			uni.showToast({
				title: '暂未开放物流查询功能',
				icon: 'none'
			})
		},
		
		// 确认收货
		confirmReceive(order) {
			uni.showModal({
				title: '确认收货',
				content: '确认已收到商品吗？',
				success: (res) => {
					if (res.confirm) {
						uni.showToast({
							title: '确认收货成功',
							icon: 'success'
						})
						// 这里应该调用确认收货接口
						// 然后重新加载订单列表
						setTimeout(() => {
							// 模拟更新状态
							const index = this.orders.findIndex(item => item.id === order.id)
							if (index !== -1) {
								this.orders[index].status = 4
								this.$forceUpdate()
							}
						}, 1500)
					}
				}
			})
		},
		
		// 删除订单
		deleteOrder(order) {
			uni.showModal({
				title: '删除订单',
				content: '确定要删除该订单吗？',
				success: (res) => {
					if (res.confirm) {
						// 这里应该调用删除订单接口
						// 然后重新加载订单列表
						const index = this.orders.findIndex(item => item.id === order.id)
						if (index !== -1) {
							this.orders.splice(index, 1)
						}
						
						uni.showToast({
							title: '删除成功',
							icon: 'success'
						})
					}
				}
			})
		},
		
		// 去购物
		goToShop() {
			uni.switchTab({
				url: '/pages/mall/mall'
			})
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background: #f5f5f5;
	display: flex;
	flex-direction: column;
}

.status-bar {
	background: #fff;
}

.nav-bar {
	height: 44px;
	background: #fff;
	display: flex;
	align-items: center;
	padding: 0 15px;
	position: relative;
	
	.left {
		width: 40px;
		height: 40px;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.title {
		position: absolute;
		left: 0;
		right: 0;
		text-align: center;
		font-size: 17px;
		font-weight: 500;
		color: #333;
	}
}

.status-tabs {
	display: flex;
	background: #fff;
	padding: 0 15px;
	border-bottom: 1px solid #f0f0f0;
	
	.tab-item {
		flex: 1;
		height: 44px;
		line-height: 44px;
		text-align: center;
		font-size: 14px;
		color: #666;
		position: relative;
		
		&.active {
			color: #2979ff;
			font-weight: 500;
			
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 50%;
				transform: translateX(-50%);
				width: 20px;
				height: 3px;
				background: #2979ff;
				border-radius: 2px;
			}
		}
	}
}

.order-list {
	flex: 1;
	padding: 15px;
}

.order-items {
	.order-item {
		background: #fff;
		border-radius: 10px;
		margin-bottom: 15px;
		overflow: hidden;
		
		.order-header {
			padding: 15px;
			display: flex;
			justify-content: space-between;
			align-items: center;
			border-bottom: 1px solid #f0f0f0;
			
			.order-no {
				font-size: 13px;
				color: #666;
			}
			
			.order-status {
				font-size: 13px;
				font-weight: 500;
				
				&.status-1 {
					color: #ff9800;
				}
				
				&.status-2 {
					color: #2979ff;
				}
				
				&.status-3 {
					color: #2979ff;
				}
				
				&.status-4 {
					color: #4caf50;
				}
				
				&.status-5 {
					color: #999;
				}
			}
		}
		
		.product-info {
			padding: 15px;
			display: flex;
			border-bottom: 1px solid #f0f0f0;
			
			.product-image {
				width: 80px;
				height: 80px;
				border-radius: 5px;
				background: #f5f5f5;
			}
			
			.product-detail {
				flex: 1;
				margin-left: 15px;
				
				.product-name {
					font-size: 14px;
					color: #333;
					margin-bottom: 8px;
					display: block;
				}
				
				.price-quantity {
					display: flex;
					justify-content: space-between;
					
					.price {
						font-size: 15px;
						color: #ff6b6b;
					}
					
					.quantity {
						font-size: 13px;
						color: #999;
					}
				}
			}
		}
		
		.order-footer {
			padding: 15px;
			
			.total-section {
				display: flex;
				justify-content: flex-end;
				align-items: center;
				margin-bottom: 15px;
				
				.total-label {
					font-size: 14px;
					color: #666;
				}
				
				.total-price {
					font-size: 16px;
					color: #ff6b6b;
					font-weight: 500;
				}
			}
			
			.action-buttons {
				display: flex;
				justify-content: flex-end;
				gap: 10px;
				
				.action-btn {
					height: 32px;
					line-height: 32px;
					padding: 0 15px;
					font-size: 13px;
					border-radius: 16px;
					margin: 0;
					
					&.outline {
						background: #fff;
						color: #666;
						border: 1px solid #ddd;
					}
					
					&.primary {
						background: #2979ff;
						color: #fff;
					}
				}
			}
		}
	}
}

.empty-state {
	padding: 50px 0;
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
		margin-bottom: 5px;
	}
	
	.sub-text {
		font-size: 14px;
		color: #999;
		margin-bottom: 20px;
	}
	
	.go-shop-btn {
		width: 120px;
		height: 40px;
		line-height: 40px;
		background: #2979ff;
		color: #fff;
		font-size: 14px;
		border-radius: 20px;
	}
}

.loading-state {
	padding: 50px 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	
	.loading-spinner {
		width: 30px;
		height: 30px;
		border: 3px solid #f0f0f0;
		border-top-color: #2979ff;
		border-radius: 50%;
		animation: spin 0.8s linear infinite;
		margin-bottom: 15px;
	}
	
	text {
		font-size: 14px;
		color: #999;
	}
}

@keyframes spin {
	to { transform: rotate(360deg); }
}
</style> 