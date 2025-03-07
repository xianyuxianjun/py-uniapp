<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部标题栏 -->
		<view class="title-header">
			<text class="page-title">积分商城</text>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y refresher-enabled 
			:refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			
			<!-- 商品分类标签 -->
			<view class="category-tabs">
				<view class="tab-item" 
					:class="{ active: statusFilter === 'all' }" 
					@tap="setStatusFilter('all')">
					全部
				</view>
				<view class="tab-item" 
					:class="{ active: statusFilter === 1 }" 
					@tap="setStatusFilter(1)">
					在售
				</view>
				<view class="tab-item" 
					:class="{ active: statusFilter === 2 }" 
					@tap="setStatusFilter(2)">
					下架
				</view>
			</view>
			
			<!-- 商品列表 -->
			<view class="product-list" v-if="filteredProducts.length > 0">
				<view class="product-item" v-for="product in filteredProducts" :key="product.id"
					@tap="viewProductDetail(product)" hover-class="product-item-hover">
					<image class="product-image" :src="product.imageUrl || '/static/images/default-product.png'" mode="aspectFill"></image>
					<view class="product-info">
						<text class="product-name">{{ product.productName }}</text>
						<text class="product-desc">{{ product.description }}</text>
						<view class="product-bottom">
							<view class="price-container">
								<text class="product-price">{{ formatPrice(product.price) }}</text>
								<text class="price-unit">积分</text>
							</view>
							<text class="product-stock" :class="{'stock-low': product.stockQuantity < 10}">
								{{ product.stockQuantity > 0 ? '库存: ' + product.stockQuantity : '已售罄' }}
							</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="filteredProducts.length === 0 && !loading">
				<image src="/static/images/empty-cart.png" mode="aspectFit"></image>
				<text>暂无商品</text>
				<text class="sub-text">敬请期待更多商品上架</text>
			</view>
			
			<!-- 加载状态 -->
			<view class="loading-state" v-if="loading && products.length === 0">
				<view class="loading-spinner"></view>
				<text>加载中...</text>
			</view>
			
			<!-- 底部安全区域 -->
			<view class="safe-area-bottom"></view>
		</scroll-view>
		
		<!-- 商品详情弹窗 -->
		<uni-popup ref="productDetailPopup" type="bottom" safe-area>
			<view class="product-detail">
				<view class="detail-header">
					<text class="detail-title">商品详情</text>
					<view class="close-btn" @tap="closeProductDetail">
						<uni-icons type="close" size="20" color="#999"></uni-icons>
					</view>
				</view>
				
				<scroll-view class="detail-content" scroll-y>
					<swiper class="detail-swiper" circular indicator-dots autoplay 
						:indicator-color="'rgba(255, 255, 255, 0.4)'" 
						:indicator-active-color="'#ffffff'">
						<swiper-item>
							<image class="detail-image" :src="currentProduct.imageUrl || '/static/images/default-product.png'" mode="aspectFill"></image>
						</swiper-item>
						<!-- 可以添加更多商品图片 -->
					</swiper>
					
					<view class="detail-info">
						<text class="detail-name">{{ currentProduct.productName }}</text>
						<view class="detail-price-row">
							<view class="price-container">
								<text class="price-symbol">¥</text>
								<text class="detail-price">{{ formatPrice(currentProduct.price) }}</text>
							</view>
							<text class="detail-stock" :class="{'stock-low': currentProduct.stockQuantity < 10}">
								{{ currentProduct.stockQuantity > 0 ? '库存: ' + currentProduct.stockQuantity : '已售罄' }}
							</text>
						</view>
						
						<!-- 积分兑换信息 -->
						<view class="points-info" v-if="currentProduct.pointsPrice">
							<uni-icons type="gift-filled" size="16" color="#ff9500"></uni-icons>
							<text>可用 {{ currentProduct.pointsPrice }} 积分兑换</text>
						</view>
						
						<!-- 商品规格 -->
						<view class="specs-section">
							<view class="section-title">
								<text>商品规格</text>
							</view>
							<view class="spec-tags">
								<text class="spec-tag">{{ currentProduct.specification || '默认规格' }}</text>
							</view>
						</view>
						
						<view class="detail-desc">
							<view class="section-title">
								<text>商品描述</text>
							</view>
							<text class="desc-content">{{ currentProduct.description || '暂无描述' }}</text>
						</view>
					</view>
				</scroll-view>
				
				<view class="detail-footer">
					<view class="cart-btn" @tap="addToCart">
						<uni-icons type="cart" size="20" color="#ffffff"></uni-icons>
						<text>加入购物车</text>
					</view>
					<button class="buy-now-btn" @tap="buyNow">立即购买</button>
				</view>
				
				<!-- 底部安全区域 -->
				<view class="popup-safe-area-bottom"></view>
			</view>
		</uni-popup>
		
		<!-- 购买成功提示 -->
		<view class="success-tip" :class="{'show-tip': showSuccessTip}">
			<uni-icons type="checkmarkempty" size="20" color="#ffffff"></uni-icons>
			<text>加入购物车成功</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			products: [], // 所有商品
			loading: false,
			refreshing: false,
			statusFilter: 'all', // 状态筛选
			currentProduct: {}, // 当前查看的商品
			baseUrl: 'http://localhost:8080', // 后端接口基础URL
			showSuccessTip: false, // 显示成功提示
			// 轮播图数据
			banners: [
				{
					imageUrl: '/static/images/banner1.jpg',
					linkUrl: ''
				},
				{
					imageUrl: '/static/images/banner2.jpg',
					linkUrl: ''
				},
				{
					imageUrl: '/static/images/banner3.jpg',
					linkUrl: ''
				}
			]
		}
	},
	
	computed: {
		// 过滤后的商品列表
		filteredProducts() {
			return this.products.filter(product => {
				// 状态筛选
				const statusMatch = this.statusFilter === 'all' || product.status === this.statusFilter
				return statusMatch
			})
		}
	},
	
	onLoad() {
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.loadProducts()
	},
	
	methods: {
		// 加载商品数据
		async loadProducts() {
			this.loading = true
			
			try {
				const res = await uni.request({
					url: `${this.baseUrl}/product/list`,
					method: 'GET'
				})
				
				if (res.data.code === 1) {
					this.products = res.data.data || []
				} else {
					throw new Error(res.data.msg || '获取商品失败')
				}
			} catch (err) {
				console.error('获取商品列表失败：', err)
				uni.showToast({
					title: '获取商品失败',
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
			this.loadProducts()
		},
		
		// 设置状态筛选
		setStatusFilter(status) {
			this.statusFilter = status
		},
		
		// 查看商品详情
		viewProductDetail(product) {
			this.currentProduct = product
			this.$refs.productDetailPopup.open('bottom')
		},
		
		// 关闭商品详情
		closeProductDetail() {
			this.$refs.productDetailPopup.close()
		},
		
		// 格式化价格
		formatPrice(price) {
			if (!price) return '0.00'
			return parseFloat(price).toFixed(2)
		},
		
		// 加入购物车
		addToCart() {
			// 检查是否登录
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo || !userInfo.id) {
				uni.navigateTo({
					url: '/pages/login/login'
				});
				return;
			}
			
			// 检查库存
			if (this.currentProduct.stockQuantity <= 0) {
				uni.showToast({
					title: '商品库存不足',
					icon: 'none'
				});
				return;
			}
			
			// 显示成功提示
			this.showSuccessTip = true;
			setTimeout(() => {
				this.showSuccessTip = false;
			}, 2000);
			
			// 这里可以添加加入购物车的API调用
			
			// 关闭弹窗
			this.$refs.productDetailPopup.close();
		},
		
		// 立即购买
		buyNow() {
			// 检查是否登录
			const userInfo = uni.getStorageSync('userInfo');
			if (!userInfo || !userInfo.id) {
				uni.navigateTo({
					url: '/pages/login/login'
				});
				return;
			}
			
			// 检查库存
			if (this.currentProduct.stockQuantity <= 0) {
				uni.showToast({
					title: '商品库存不足',
					icon: 'none'
				});
				return;
			}
			
			// 跳转到订单确认页面
			uni.navigateTo({
				url: `/pages/order-confirm/index?productId=${this.currentProduct.id}&quantity=1`
			});
			
			// 关闭弹窗
			this.$refs.productDetailPopup.close();
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background: #f7f8fa;
	display: flex;
	flex-direction: column;
}

.status-bar {
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
}

/* 标题头部 */
.title-header {
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	padding: 20rpx 30rpx 30rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	
	.page-title {
		font-size: 36rpx;
		font-weight: 600;
		color: #fff;
		letter-spacing: 2rpx;
	}
}

/* 轮播图 */
.banner-swiper {
	height: 300rpx;
	
	.banner-image {
		width: 100%;
		height: 100%;
	}
}

.content {
	flex: 1;
}

/* 分类标签 */
.category-tabs {
	display: flex;
	background: #fff;
	padding: 6rpx 0;
	margin: 20rpx 30rpx;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
	
	.tab-item {
		flex: 1;
		text-align: center;
		font-size: 28rpx;
		color: #666;
		padding: 20rpx 0;
		position: relative;
		transition: all 0.3s ease;
		
		&.active {
			color: #4B6EFF;
			font-weight: 500;
			
			&::after {
				content: '';
				position: absolute;
				bottom: 10rpx;
				left: 30%;
				right: 30%;
				height: 6rpx;
				background: #4B6EFF;
				border-radius: 3rpx;
			}
		}
	}
}

/* 商品列表 */
.product-list {
	padding: 0 30rpx;
	display: flex;
	flex-direction: column;
	gap: 30rpx;
	margin-bottom: 30rpx;
}

.product-item {
	background: #fff;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.05);
	transition: all 0.3s ease;
	
	.product-image {
		width: 100%;
		height: 340rpx;
		background: #f5f7fa;
	}
	
	.product-info {
		padding: 30rpx;
		
		.product-name {
			font-size: 32rpx;
			font-weight: 600;
			color: #333;
			margin-bottom: 12rpx;
			display: block;
		}
		
		.product-desc {
			font-size: 26rpx;
			color: #777;
			margin-bottom: 20rpx;
			line-height: 1.5;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 2;
			overflow: hidden;
			height: 78rpx;
		}
		
		.product-bottom {
			display: flex;
			justify-content: space-between;
			align-items: center;
			
			.price-container {
				display: flex;
				align-items: baseline;
				
				.product-price {
					font-size: 36rpx;
					color: #ff6b6b;
					font-weight: 600;
				}
				
				.price-unit {
					font-size: 24rpx;
					color: #ff6b6b;
					margin-left: 4rpx;
				}
			}
			
			.product-stock {
				font-size: 24rpx;
				color: #999;
				padding: 6rpx 16rpx;
				border-radius: 20rpx;
				background: #f5f7fa;
				
				&.stock-low {
					color: #ff9800;
					background: #fff5e6;
				}
			}
		}
	}
}

.product-item-hover {
	transform: translateY(-6rpx);
	box-shadow: 0 16rpx 36rpx rgba(0, 0, 0, 0.1);
}

/* 空状态 */
.empty-state {
	padding: 120rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	
	image {
		width: 240rpx;
		height: 240rpx;
		margin-bottom: 30rpx;
		opacity: 0.8;
	}
	
	text {
		font-size: 32rpx;
		color: #666;
	}
	
	.sub-text {
		font-size: 28rpx;
		color: #999;
		margin-top: 16rpx;
	}
}

/* 加载状态 */
.loading-state {
	padding: 120rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	
	.loading-spinner {
		width: 60rpx;
		height: 60rpx;
		border: 6rpx solid #f0f0f0;
		border-top-color: #4B6EFF;
		border-radius: 50%;
		animation: spin 0.8s linear infinite;
		margin-bottom: 30rpx;
	}
	
	text {
		font-size: 28rpx;
		color: #999;
	}
}

/* 商品详情 */
.product-detail {
	background: #fff;
	border-radius: 40rpx 40rpx 0 0;
	max-height: 90vh;
	display: flex;
	flex-direction: column;
	
	.detail-header {
		padding: 30rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		position: relative;
		
		.detail-title {
			font-size: 32rpx;
			font-weight: 600;
			color: #333;
		}
		
		.close-btn {
			position: absolute;
			right: 30rpx;
			top: 50%;
			transform: translateY(-50%);
			width: 60rpx;
			height: 60rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			border-radius: 30rpx;
			background: #f5f7fa;
		}
	}
	
	.detail-content {
		flex: 1;
		height: 0;
		
		.detail-swiper {
			width: 100%;
			height: 600rpx;
		}
		
		.detail-image {
			width: 100%;
			height: 100%;
			background: #f5f7fa;
		}
		
		.detail-info {
			padding: 30rpx;
			
			.detail-name {
				font-size: 36rpx;
				font-weight: 600;
				color: #333;
				margin-bottom: 20rpx;
				display: block;
			}
			
			.detail-price-row {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 30rpx;
				
				.price-container {
					display: flex;
					align-items: baseline;
					
					.price-symbol {
						font-size: 28rpx;
						color: #ff6b6b;
						margin-right: 4rpx;
					}
					
					.detail-price {
						font-size: 48rpx;
						color: #ff6b6b;
						font-weight: 600;
					}
				}
				
				.detail-stock {
					font-size: 26rpx;
					color: #999;
					padding: 8rpx 20rpx;
					border-radius: 24rpx;
					background: #f5f7fa;
					
					&.stock-low {
						color: #ff9800;
						background: #fff5e6;
					}
				}
			}
			
			.points-info {
				display: flex;
				align-items: center;
				background: #fff9ec;
				padding: 16rpx 24rpx;
				border-radius: 16rpx;
				margin-bottom: 30rpx;
				
				.uni-icons {
					margin-right: 12rpx;
				}
				
				text {
					font-size: 26rpx;
					color: #ff9500;
				}
			}
			
			.section-title {
				margin-bottom: 20rpx;
				padding-left: 20rpx;
				border-left: 8rpx solid #4B6EFF;
				
				text {
					font-size: 30rpx;
					font-weight: 600;
					color: #333;
				}
			}
			
			.specs-section {
				margin-bottom: 30rpx;
			}
			
			.spec-tags {
				display: flex;
				flex-wrap: wrap;
				gap: 20rpx;
				
				.spec-tag {
					padding: 12rpx 24rpx;
					background: #f5f7fa;
					border-radius: 16rpx;
					font-size: 26rpx;
					color: #666;
				}
			}
			
			.detail-desc {
				background: #f9f9f9;
				padding: 30rpx;
				border-radius: 20rpx;
				margin-bottom: 30rpx;
				
				.desc-content {
					font-size: 28rpx;
					color: #666;
					line-height: 1.6;
				}
			}
		}
	}
	
	.detail-footer {
		padding: 20rpx 30rpx;
		display: flex;
		gap: 30rpx;
		border-top: 1px solid #f0f0f0;
		
		.cart-btn, .buy-now-btn {
			flex: 1;
			height: 88rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			border-radius: 44rpx;
			font-size: 32rpx;
			font-weight: 500;
		}
		
		.cart-btn {
			background: #ffa23c;
			color: #fff;
			
			.uni-icons {
				margin-right: 10rpx;
			}
		}
		
		.buy-now-btn {
			background: linear-gradient(135deg, #4B6EFF, #55ACEE);
			color: #fff;
			margin: 0;
		}
	}
	
	/* 弹窗底部安全区域 */
	.popup-safe-area-bottom {
		height: calc(env(safe-area-inset-bottom) + 20rpx);
		background: #fff;
	}
}

.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 100rpx);
	background: transparent;
}

/* 加入购物车成功提示 */
.success-tip {
	position: fixed;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	background: rgba(0, 0, 0, 0.7);
	color: #fff;
	padding: 20rpx 40rpx;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	gap: 10rpx;
	opacity: 0;
	visibility: hidden;
	transition: all 0.3s ease;
	z-index: 9999;
	
	text {
		font-size: 28rpx;
	}
	
	&.show-tip {
		opacity: 1;
		visibility: visible;
	}
}

@keyframes spin {
	to { transform: rotate(360deg); }
}
</style> 