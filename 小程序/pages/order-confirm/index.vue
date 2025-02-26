<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="left" @click="navigateBack">
				<uni-icons type="left" size="20" color="#333"></uni-icons>
			</view>
			<text class="title">确认订单</text>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y>
			<!-- 收货地址 -->
			<view class="address-section" @tap="chooseAddress">
				<view class="address-content" v-if="address">
					<view class="contact-info">
						<text class="name">{{ address.contactName }}</text>
						<text class="phone">{{ address.contactPhone }}</text>
					</view>
					<text class="address-text">{{ address.address }}</text>
				</view>
				<view class="no-address" v-else>
					<uni-icons type="location" size="24" color="#2979ff"></uni-icons>
					<text>添加收货地址</text>
				</view>
				<uni-icons type="right" size="16" color="#999"></uni-icons>
			</view>
			
			<!-- 商品信息 -->
			<view class="product-section">
				<view class="product-item">
					<image class="product-image" :src="product.imageUrl || '/static/images/default-product.png'" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ product.productName }}</text>
						<text class="product-price">¥{{ formatPrice(product.price) }}</text>
					</view>
					<view class="quantity-control">
						<view class="quantity-btn minus" @tap="decreaseQuantity">
							<uni-icons type="minus" size="16" color="#666"></uni-icons>
						</view>
						<text class="quantity">{{ quantity }}</text>
						<view class="quantity-btn plus" @tap="increaseQuantity">
							<uni-icons type="plus" size="16" color="#666"></uni-icons>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 订单备注 -->
			<view class="notes-section">
				<text class="label">订单备注</text>
				<input type="text" v-model="notes" placeholder="选填，请填写订单备注" placeholder-class="placeholder" />
			</view>
			
			<!-- 订单金额 -->
			<view class="amount-section">
				<view class="amount-item">
					<text class="label">商品金额</text>
					<text class="value">¥{{ formatPrice(product.price * quantity) }}</text>
				</view>
				<view class="amount-item">
					<text class="label">运费</text>
					<text class="value">¥0.00</text>
				</view>
				<view class="amount-item total">
					<text class="label">合计</text>
					<text class="value">¥{{ formatPrice(product.price * quantity) }}</text>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部结算栏 -->
		<view class="bottom-bar">
			<view class="price-section">
				<text class="label">实付款：</text>
				<text class="price">¥{{ formatPrice(product.price * quantity) }}</text>
			</view>
			<button class="submit-btn" @tap="submitOrder" :disabled="submitting">
				{{ submitting ? '提交中...' : '提交订单' }}
			</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			productId: null,
			product: {},
			quantity: 1,
			address: null,
			notes: '',
			submitting: false,
			baseUrl: 'http://localhost:8080'
		}
	},
	
	onLoad(options) {
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.productId = options.productId
		this.quantity = parseInt(options.quantity) || 1
		
		// 加载商品信息
		this.loadProductInfo()
		
		// 获取默认地址
		this.loadDefaultAddress()
	},
	
	methods: {
		// 返回上一页
		navigateBack() {
			uni.navigateBack()
		},
		
		// 加载商品信息
		async loadProductInfo() {
			try {
				// 修正接口调用方式
				const res = await uni.request({
					url: `${this.baseUrl}/product/getById`,
					method: 'GET',
					data: {
						id: this.productId
					}
				})
				
				if (res.data.code === 1) {
					this.product = res.data.data
				} else {
					throw new Error(res.data.msg || '获取商品信息失败')
				}
			} catch (err) {
				console.error('获取商品信息失败：', err)
				uni.showToast({
					title: '获取商品信息失败',
					icon: 'none'
				})
			}
		},
		
		// 加载默认地址
		loadDefaultAddress() {
			// 这里可以从本地存储或接口获取默认地址
			// 示例数据
			this.address = {
				contactName: '张三',
				contactPhone: '13800138000',
				address: '广东省广州市天河区天河路385号'
			}
		},
		
		// 选择地址
		chooseAddress() {
			// 这里可以跳转到地址选择页面
			// 示例：直接弹出输入框
			uni.showModal({
				title: '输入收货地址',
				content: '请输入收货信息',
				showCancel: true,
				confirmText: '确定',
				success: (res) => {
					if (res.confirm) {
						// 弹出三个输入框，分别输入联系人、电话和地址
						this.showAddressInputDialog()
					}
				}
			})
		},
		
		// 显示地址输入对话框
		showAddressInputDialog() {
			uni.showModal({
				title: '联系人',
				editable: true,
				placeholderText: '请输入联系人姓名',
				success: (res) => {
					if (res.confirm && res.content) {
						const contactName = res.content
						
						uni.showModal({
							title: '联系电话',
							editable: true,
							placeholderText: '请输入联系电话',
							success: (res) => {
								if (res.confirm && res.content) {
									const contactPhone = res.content
									
									uni.showModal({
										title: '收货地址',
										editable: true,
										placeholderText: '请输入详细地址',
										success: (res) => {
											if (res.confirm && res.content) {
												this.address = {
													contactName,
													contactPhone,
													address: res.content
												}
											}
										}
									})
								}
							}
						})
					}
				}
			})
		},
		
		// 减少数量
		decreaseQuantity() {
			if (this.quantity > 1) {
				this.quantity--
			}
		},
		
		// 增加数量
		increaseQuantity() {
			if (this.quantity < this.product.stockQuantity) {
				this.quantity++
			} else {
				uni.showToast({
					title: '已达到最大库存',
					icon: 'none'
				})
			}
		},
		
		// 格式化价格
		formatPrice(price) {
			if (!price) return '0.00'
			return parseFloat(price).toFixed(2)
		},
		
		// 提交订单
		async submitOrder() {
			// 检查是否登录
			const userInfo = uni.getStorageSync('userInfo')
			if (!userInfo || !userInfo.id) {
				uni.navigateTo({
					url: '/pages/login/login'
				})
				return
			}
			
			// 检查是否有收货地址
			if (!this.address) {
				uni.showToast({
					title: '请添加收货地址',
					icon: 'none'
				})
				return
			}
			
			// 检查库存
			if (this.product.stockQuantity < this.quantity) {
				uni.showToast({
					title: '商品库存不足',
					icon: 'none'
				})
				return
			}
			
			this.submitting = true
			
			try {
				// 使用JSON格式提交数据
				const res = await uni.request({
					url: `${this.baseUrl}/product/order/create`,
					method: 'POST',
					header: {
						'content-type': 'application/json'
					},
					data: {
						userId: userInfo.id,
						productId: this.productId,
						quantity: this.quantity,
						address: this.address.address,
						contactName: this.address.contactName,
						contactPhone: this.address.contactPhone,
						notes: this.notes || ''
					}
				})
				
				if (res.data.code === 1) {
					uni.showToast({
						title: '订单创建成功',
						icon: 'success'
					})
					
					// 跳转到订单详情页
					setTimeout(() => {
						uni.redirectTo({
							url: `/pages/order-detail/index?orderNo=${res.data.data.orderNo}`
						})
					}, 1500)
				} else {
					throw new Error(res.data.msg || '创建订单失败')
				}
			} catch (err) {
				console.error('创建订单失败：', err)
				uni.showToast({
					title: '创建订单失败',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
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

.address-section {
	background: #fff;
	margin: 15px;
	padding: 15px;
	border-radius: 10px;
	display: flex;
	align-items: center;
	
	.address-content {
		flex: 1;
		
		.contact-info {
			display: flex;
			align-items: center;
			margin-bottom: 8px;
			
			.name {
				font-size: 16px;
				font-weight: 500;
				color: #333;
				margin-right: 15px;
			}
			
			.phone {
				font-size: 14px;
				color: #666;
			}
		}
		
		.address-text {
			font-size: 14px;
			color: #666;
			line-height: 1.4;
		}
	}
	
	.no-address {
		flex: 1;
		display: flex;
		align-items: center;
		
		text {
			font-size: 14px;
			color: #2979ff;
			margin-left: 8px;
		}
	}
}

.product-section {
	background: #fff;
	margin: 15px;
	padding: 15px;
	border-radius: 10px;
	
	.product-item {
		display: flex;
		align-items: center;
		
		.product-image {
			width: 80px;
			height: 80px;
			border-radius: 5px;
			background: #f5f5f5;
		}
		
		.product-info {
			flex: 1;
			margin: 0 15px;
			
			.product-name {
				font-size: 14px;
				color: #333;
				margin-bottom: 8px;
				display: block;
			}
			
			.product-price {
				font-size: 16px;
				color: #ff6b6b;
				font-weight: 500;
			}
		}
		
		.quantity-control {
			display: flex;
			align-items: center;
			
			.quantity-btn {
				width: 28px;
				height: 28px;
				background: #f5f5f5;
				border-radius: 4px;
				display: flex;
				align-items: center;
				justify-content: center;
			}
			
			.quantity {
				width: 40px;
				text-align: center;
				font-size: 14px;
				color: #333;
			}
		}
	}
}

.notes-section {
	background: #fff;
	margin: 15px;
	padding: 15px;
	border-radius: 10px;
	display: flex;
	align-items: center;
	
	.label {
		font-size: 14px;
		color: #333;
		width: 80px;
	}
	
	input {
		flex: 1;
		height: 40px;
		font-size: 14px;
		color: #333;
	}
	
	.placeholder {
		color: #999;
	}
}

.amount-section {
	background: #fff;
	margin: 15px;
	padding: 15px;
	border-radius: 10px;
	
	.amount-item {
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

.bottom-bar {
	background: #fff;
	padding: 15px;
	display: flex;
	align-items: center;
	box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
	
	.price-section {
		flex: 1;
		
		.label {
			font-size: 14px;
			color: #666;
		}
		
		.price {
			font-size: 18px;
			color: #ff6b6b;
			font-weight: 500;
		}
	}
	
	.submit-btn {
		width: 120px;
		height: 40px;
		line-height: 40px;
		background: #2979ff;
		color: #fff;
		font-size: 14px;
		border-radius: 20px;
		margin: 0;
	}
}
</style> 