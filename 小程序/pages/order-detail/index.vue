<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="left" @click="navigateBack">
				<uni-icons type="left" size="20" color="#333"></uni-icons>
			</view>
			<text class="title">订单详情</text>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y v-if="order">
			<!-- 订单状态 -->
			<view class="status-section">
				<view class="status-icon" :class="'status-' + order.status">
					<uni-icons :type="getStatusIcon(order.status)" size="28" color="#fff"></uni-icons>
				</view>
				<view class="status-info">
					<text class="status-text">{{ getStatusText(order.status) }}</text>
					<text class="status-desc">{{ getStatusDesc(order.status) }}</text>
				</view>
			</view>
			
			<!-- 收货地址 -->
			<view class="info-card">
				<view class="card-header">
					<uni-icons type="location" size="18" color="#2979ff"></uni-icons>
					<text>收货信息</text>
				</view>
				<view class="address-info">
					<view class="contact-row">
						<text class="name">{{ order.contactName }}</text>
						<text class="phone">{{ order.contactPhone }}</text>
					</view>
					<text class="address">{{ order.address }}</text>
				</view>
			</view>
			
			<!-- 商品信息 -->
			<view class="info-card">
				<view class="card-header">
					<uni-icons type="cart" size="18" color="#2979ff"></uni-icons>
					<text>商品信息</text>
				</view>
				<view class="product-item">
					<image class="product-image" :src="order.productImage || '/static/images/default-product.png'" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ order.productName }}</text>
						<view class="product-price-row">
							<text class="product-price">¥{{ formatPrice(order.price) }}</text>
							<text class="product-quantity">x{{ order.quantity }}</text>
						</view>
					</view>
				</view>
				<view class="amount-info">
					<view class="amount-row">
						<text class="label">商品金额</text>
						<text class="value">¥{{ formatPrice(order.price * order.quantity) }}</text>
					</view>
					<view class="amount-row">
						<text class="label">运费</text>
						<text class="value">¥0.00</text>
					</view>
					<view class="amount-row total">
						<text class="label">实付款</text>
						<text class="value">¥{{ formatPrice(order.totalAmount) }}</text>
					</view>
				</view>
			</view>
			
			<!-- 订单信息 -->
			<view class="info-card">
				<view class="card-header">
					<uni-icons type="list" size="18" color="#2979ff"></uni-icons>
					<text>订单信息</text>
				</view>
				<view class="order-info">
					<view class="info-row">
						<text class="label">订单编号</text>
						<view class="value-copy">
							<text class="value">{{ order.orderNo }}</text>
							<view class="copy-btn" @tap="copyOrderNo">复制</view>
						</view>
					</view>
					<view class="info-row">
						<text class="label">创建时间</text>
						<text class="value">{{ formatDate(order.createdAt) }}</text>
					</view>
					<view class="info-row" v-if="order.paymentTime">
						<text class="label">支付时间</text>
						<text class="value">{{ formatDate(order.paymentTime) }}</text>
					</view>
					<view class="info-row" v-if="order.deliveryTime">
						<text class="label">发货时间</text>
						<text class="value">{{ formatDate(order.deliveryTime) }}</text>
					</view>
					<view class="info-row" v-if="order.completionTime">
						<text class="label">完成时间</text>
						<text class="value">{{ formatDate(order.completionTime) }}</text>
					</view>
					<view class="info-row" v-if="order.notes">
						<text class="label">订单备注</text>
						<text class="value">{{ order.notes }}</text>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar" v-if="order">
			<view class="btn-group">
				<button class="action-btn outline" v-if="order.status === 1" @tap="cancelOrder">取消订单</button>
				<button class="action-btn primary" v-if="order.status === 1" @tap="payOrder">立即支付</button>
				<button class="action-btn outline" v-if="order.status === 2" @tap="checkDelivery">查看物流</button>
				<button class="action-btn primary" v-if="order.status === 3" @tap="confirmReceive">确认收货</button>
			</view>
		</view>
		
		<!-- 加载状态 -->
		<view class="loading-state" v-if="loading">
			<view class="loading-spinner"></view>
			<text>加载中...</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			orderNo: '',
			order: null,
			loading: true,
			baseUrl: 'http://localhost:8080'
		}
	},
	
	onLoad(options) {
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.orderNo = options.orderNo
		this.loadOrderDetail()
	},
	
	methods: {
		// 返回上一页
		navigateBack() {
			uni.navigateBack()
		},
		
		// 加载订单详情
		async loadOrderDetail() {
			this.loading = true
			
			try {
				const res = await uni.request({
					url: `${this.baseUrl}/product/order/detail`,
					method: 'GET',
					data: {
						orderNo: this.orderNo
					}
				})
				
				if (res.data.code === 1) {
					this.order = res.data.data
				} else {
					throw new Error(res.data.msg || '获取订单详情失败')
				}
			} catch (err) {
				console.error('获取订单详情失败：', err)
				uni.showToast({
					title: '获取订单详情失败',
					icon: 'none'
				})
			} finally {
				this.loading = false
			}
		},
		
		// 获取状态图标
		getStatusIcon(status) {
			switch (status) {
				case 1: return 'wallet'
				case 2: return 'checkmarkempty'
				case 3: return 'car'
				case 4: return 'flag'
				case 5: return 'closeempty'
				default: return 'help'
			}
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
		
		// 获取状态描述
		getStatusDesc(status) {
			switch (status) {
				case 1: return '请尽快完成支付'
				case 2: return '商家正在处理您的订单'
				case 3: return '商品已发出，请耐心等待'
				case 4: return '订单已完成，感谢您的购买'
				case 5: return '订单已取消'
				default: return ''
			}
		},
		
		// 格式化价格
		formatPrice(price) {
			if (!price) return '0.00'
			return parseFloat(price).toFixed(2)
		},
		
		// 格式化日期
		formatDate(dateStr) {
			if (!dateStr) return ''
			const date = new Date(dateStr)
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 复制订单号
		copyOrderNo() {
			uni.setClipboardData({
				data: this.order.orderNo,
				success: () => {
					uni.showToast({
						title: '复制成功',
						icon: 'success'
					})
				}
			})
		},
		
		// 取消订单
		cancelOrder() {
			uni.showModal({
				title: '提示',
				content: '确认取消订单吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							const result = await uni.request({
								url: `${this.baseUrl}/product/order/cancel?orderNo=${this.order.orderNo}`,
								method: 'POST'
							})
							
							if (result.data.code === 1) {
								uni.showToast({
									title: '订单已取消',
									icon: 'success'
								})
								// 重新加载订单详情
								setTimeout(() => {
									this.loadOrderDetail()
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
		payOrder() {
			uni.showModal({
				title: '模拟支付',
				content: '这是一个模拟支付功能，点击确定将直接标记为已支付',
				success: async (res) => {
					if (res.confirm) {
						try {
							const result = await uni.request({
								url: `${this.baseUrl}/product/order/pay?orderNo=${this.order.orderNo}`,
								method: 'POST'
							})
							
							if (result.data.code === 1) {
								uni.showToast({
									title: '支付成功',
									icon: 'success'
								})
								// 重新加载订单详情
								setTimeout(() => {
									this.loadOrderDetail()
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
		checkDelivery() {
			uni.showToast({
				title: '暂未开放此功能',
				icon: 'none'
			})
		},
		
		// 确认收货
		confirmReceive() {
			uni.showModal({
				title: '提示',
				content: '确认已收到商品吗？',
				success: (res) => {
					if (res.confirm) {
						uni.showToast({
							title: '确认收货成功',
							icon: 'success'
						})
						// 模拟更新状态
						setTimeout(() => {
							this.order.status = 4
						}, 1500)
					}
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
	
	.left {
		width: 40px;
		height: 44px;
		display: flex;
		align-items: center;
	}
	
	.title {
		flex: 1;
		text-align: center;
		font-size: 16px;
		font-weight: 500;
		color: #333;
	}
}

.content {
	flex: 1;
	height: 0;
}

.status-section {
	background: #fff;
	padding: 20px 15px;
	display: flex;
	align-items: center;
	
	.status-icon {
		width: 50px;
		height: 50px;
		border-radius: 25px;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 15px;
		
		&.status-1 {
			background: #ff9800;
		}
		
		&.status-2 {
			background: #2979ff;
		}
		
		&.status-3 {
			background: #00bcd4;
		}
		
		&.status-4 {
			background: #4caf50;
		}
		
		&.status-5 {
			background: #9e9e9e;
		}
	}
	
	.status-info {
		.status-text {
			font-size: 18px;
			font-weight: 600;
			color: #333;
			display: block;
			margin-bottom: 5px;
		}
		
		.status-desc {
			font-size: 14px;
			color: #999;
		}
	}
}

.info-card {
	background: #fff;
	margin: 15px;
	border-radius: 10px;
	overflow: hidden;
	
	.card-header {
		padding: 15px;
		display: flex;
		align-items: center;
		border-bottom: 1px solid #f0f0f0;
		
		text {
			font-size: 15px;
			font-weight: 500;
			color: #333;
			margin-left: 8px;
		}
	}
}

.address-info {
	padding: 15px;
	
	.contact-row {
		display: flex;
		align-items: center;
		margin-bottom: 8px;
		
		.name {
			font-size: 15px;
			font-weight: 500;
			color: #333;
			margin-right: 15px;
		}
		
		.phone {
			font-size: 14px;
			color: #666;
		}
	}
	
	.address {
		font-size: 14px;
		color: #666;
		line-height: 1.4;
	}
}

.product-item {
	padding: 15px;
	display: flex;
	align-items: center;
	border-bottom: 1px solid #f0f0f0;
	
	.product-image {
		width: 80px;
		height: 80px;
		border-radius: 5px;
		background: #f5f5f5;
	}
	
	.product-info {
		flex: 1;
		margin-left: 15px;
		
		.product-name {
			font-size: 14px;
			color: #333;
			margin-bottom: 8px;
			display: block;
		}
		
		.product-price-row {
			display: flex;
			justify-content: space-between;
			align-items: center;
			
			.product-price {
				font-size: 16px;
				color: #ff6b6b;
				font-weight: 500;
			}
			
			.product-quantity {
				font-size: 14px;
				color: #999;
			}
		}
	}
}

.amount-info {
	padding: 15px;
	
	.amount-row {
		display: flex;
		justify-content: space-between;
		margin-bottom: 10px;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		&.total {
			padding-top: 10px;
			border-top: 1px solid #f0f0f0;
			
			.label, .value {
				font-weight: 500;
				color: #333;
			}
			
			.value {
				color: #ff6b6b;
				font-size: 16px;
			}
		}
		
		.label {
			font-size: 14px;
			color: #666;
		}
		
		.value {
			font-size: 14px;
			color: #333;
		}
	}
}

.order-info {
	padding: 15px;
	
	.info-row {
		display: flex;
		justify-content: space-between;
		margin-bottom: 12px;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		.label {
			font-size: 14px;
			color: #666;
		}
		
		.value {
			font-size: 14px;
			color: #333;
			text-align: right;
		}
		
		.value-copy {
			display: flex;
			align-items: center;
			
			.value {
				margin-right: 8px;
			}
			
			.copy-btn {
				font-size: 12px;
				color: #2979ff;
				padding: 2px 6px;
				border: 1px solid #2979ff;
				border-radius: 10px;
			}
		}
	}
}

.bottom-bar {
	background: #fff;
	padding: 15px;
	box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
	
	.btn-group {
		display: flex;
		justify-content: flex-end;
		gap: 15px;
		
		.action-btn {
			height: 40px;
			line-height: 40px;
			padding: 0 20px;
			font-size: 14px;
			border-radius: 20px;
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

.loading-state {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(255, 255, 255, 0.8);
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