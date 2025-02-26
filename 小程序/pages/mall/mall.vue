<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
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
							<text class="product-price">¥{{ formatPrice(product.price) }}</text>
							<text class="product-stock">库存: {{ product.stockQuantity }}</text>
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
					<image class="detail-image" :src="currentProduct.imageUrl || '/static/images/default-product.png'" mode="aspectFill"></image>
					
					<view class="detail-info">
						<text class="detail-name">{{ currentProduct.productName }}</text>
						<view class="detail-price-row">
							<text class="detail-price">¥{{ formatPrice(currentProduct.price) }}</text>
							<text class="detail-stock">库存: {{ currentProduct.stockQuantity }}</text>
						</view>
						<view class="detail-desc">
							<text class="desc-title">商品描述</text>
							<text class="desc-content">{{ currentProduct.description || '暂无描述' }}</text>
						</view>
					</view>
				</scroll-view>
				
				<view class="detail-footer">
					<button class="buy-now-btn" @tap="buyNow">立即购买</button>
				</view>
				
				<!-- 底部安全区域 -->
				<view class="popup-safe-area-bottom"></view>
			</view>
		</uni-popup>
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
			searchKeyword: '', // 搜索关键词
			currentProduct: {}, // 当前查看的商品
			baseUrl: 'http://localhost:8080' // 后端接口基础URL
		}
	},
	
	computed: {
		// 过滤后的商品列表
		filteredProducts() {
			return this.products.filter(product => {
				// 状态筛选
				const statusMatch = this.statusFilter === 'all' || product.status === this.statusFilter
				
				// 搜索关键词筛选
				const searchMatch = !this.searchKeyword || 
					product.productName.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
					(product.description && product.description.toLowerCase().includes(this.searchKeyword.toLowerCase()))
				
				return statusMatch && searchMatch
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
		
		// 处理搜索
		handleSearch() {
			uni.showToast({
				title: '搜索功能开发中',
				icon: 'none'
			})
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
	background: #f5f5f5;
	display: flex;
	flex-direction: column;
}

.status-bar {
	background: #fff;
}

.content {
	flex: 1;
	height: 0;
}

.category-tabs {
	display: flex;
	background: #fff;
	padding: 15px 15px 5px;
	
	.tab-item {
		flex: 1;
		text-align: center;
		font-size: 14px;
		color: #666;
		padding: 8px 0;
		position: relative;
		
		&.active {
			color: #2979ff;
			font-weight: 500;
			
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 30%;
				right: 30%;
				height: 3px;
				background: #2979ff;
				border-radius: 3px;
			}
		}
	}
}

.product-list {
	padding: 15px;
	display: flex;
	flex-direction: column;
	gap: 15px;
}

.product-item {
	background: #fff;
	border-radius: 10px;
	overflow: hidden;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
	transition: all 0.3s;
	
	.product-image {
		width: 100%;
		height: 180px;
		background: #f5f5f5;
	}
	
	.product-info {
		padding: 12px;
		
		.product-name {
			font-size: 16px;
			font-weight: 600;
			color: #333;
			margin-bottom: 6px;
			display: block;
		}
		
		.product-desc {
			font-size: 13px;
			color: #666;
			margin-bottom: 10px;
			line-height: 1.4;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 2;
			overflow: hidden;
		}
		
		.product-bottom {
			display: flex;
			justify-content: space-between;
			align-items: center;
			
			.product-price {
				font-size: 18px;
				color: #ff6b6b;
				font-weight: 600;
			}
			
			.product-stock {
				font-size: 12px;
				color: #999;
			}
		}
	}
}

.product-item-hover {
	transform: translateY(-3px);
	box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.empty-state {
	padding: 60px 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	
	image {
		width: 120px;
		height: 120px;
		margin-bottom: 15px;
		opacity: 0.7;
	}
	
	text {
		font-size: 16px;
		color: #999;
	}
	
	.sub-text {
		font-size: 14px;
		color: #bbb;
		margin-top: 8px;
	}
}

.loading-state {
	padding: 60px 0;
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

.product-detail {
	background: #fff;
	border-radius: 20px 20px 0 0;
	max-height: 75vh; /* 减小最大高度，避免遮挡 */
	display: flex;
	flex-direction: column;
	
	.detail-header {
		padding: 15px;
		display: flex;
		justify-content: center;
		align-items: center;
		position: relative;
		border-bottom: 1px solid #f0f0f0;
		
		.detail-title {
			font-size: 16px;
			font-weight: 600;
			color: #333;
		}
		
		.close-btn {
			position: absolute;
			right: 15px;
			top: 50%;
			transform: translateY(-50%);
			width: 30px;
			height: 30px;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}
	
	.detail-content {
		flex: 1;
		height: 0;
		
		.detail-image {
			width: 100%;
			height: 250px; /* 减小图片高度 */
			background: #f5f5f5;
		}
		
		.detail-info {
			padding: 15px;
			
			.detail-name {
				font-size: 18px;
				font-weight: 600;
				color: #333;
				margin-bottom: 10px;
				display: block;
			}
			
			.detail-price-row {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 15px;
				
				.detail-price {
					font-size: 24px;
					color: #ff6b6b;
					font-weight: 600;
				}
				
				.detail-stock {
					font-size: 14px;
					color: #999;
				}
			}
			
			.detail-desc {
				background: #f9f9f9;
				padding: 15px;
				border-radius: 10px;
				
				.desc-title {
					font-size: 15px;
					font-weight: 600;
					color: #333;
					margin-bottom: 8px;
					display: block;
				}
				
				.desc-content {
					font-size: 14px;
					color: #666;
					line-height: 1.6;
				}
			}
		}
	}
	
	.detail-footer {
		padding: 15px;
		display: flex;
		gap: 15px;
		border-top: 1px solid #f0f0f0;
		
		button {
			flex: 1;
			height: 44px;
			line-height: 44px;
			font-size: 16px;
			border-radius: 22px;
			margin: 0;
		}
		
		.buy-now-btn {
			background: #2979ff;
			color: #fff;
		}
	}
	
	/* 弹窗底部安全区域 */
	.popup-safe-area-bottom {
		height: calc(env(safe-area-inset-bottom) + 50px); /* 增加底部安全区域高度 */
		background: #fff;
	}
}

.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 100rpx);
	background: transparent;
}

@keyframes spin {
	to { transform: rotate(360deg); }
}
</style> 