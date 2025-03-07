<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 - 移除左右按钮，只保留标题 -->
		<view class="nav-bar">
			<text class="title">废品回收</text>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y>
			<view class="banner">
				<image src="/static/1.jpg" mode="aspectFill"></image>
			</view>
			
			<view class="category-list">
				<view class="category-item" @click="navigateTo('/pages/appointment/index')">
					<view class="icon-wrapper">
						<uni-icons type="calendar" size="28" color="#2979ff"></uni-icons>
					</view>
					<text>预约回收</text>
				</view>
				<view class="category-item" @click="navigateTo('/pages/my-orders/index')">
					<view class="icon-wrapper">
						<uni-icons type="list" size="28" color="#2979ff"></uni-icons>
					</view>
					<text>我的订单</text>
				</view>
				<view class="category-item" @click="navigateTo('/pages/message/index')">
					<view class="icon-wrapper">
						<uni-icons type="notification" size="28" color="#ffd43b"></uni-icons>
					</view>
					<text>消息通知</text>
				</view>
				<view class="category-item" @click="navigateTo('/pages/browse/index')">
					<view class="icon-wrapper">
						<uni-icons type="shop" size="28" color="#51cf66"></uni-icons>
					</view>
					<text>废品浏览</text>
				</view>
			</view>
			
			<!-- 废品回收列表 -->
			<view class="section waste-list">
				<view class="section-header">
					<text class="title">回收价目表</text>
				</view>
				<view class="waste-items">
					<view class="waste-item" 
						v-for="(item, index) in wasteList" 
						:key="index"
						hover-class="waste-item-hover"
						:hover-stay-time="100"
					>
						<view class="waste-info">
							<image class="waste-image" :src="item.imageUrl" mode="aspectFill"></image>
							<view class="info-content">
								<text class="waste-name">{{item.categoryName}}</text>
								<text class="waste-desc">{{item.description}}</text>
							</view>
							<view class="waste-price">
								<text class="price">{{item.price}}</text>
								<text class="unit">元/{{item.unit}}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 底部安全区域 -->
			<view class="safe-area-bottom"></view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				statusBarHeight: 0,
				wasteList: []
			}
		},
		onLoad() {
			// 获取状态栏高度
			this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
			
			// 检查登录状态
			const userInfo = uni.getStorageSync('userInfo')
			if (!userInfo) {
				uni.redirectTo({
					url: '/pages/login/login'
				})
			}
			
			// 调用获取废品分类列表的方法
			this.getWasteCategories()
		},
		methods: {
			navigateTo(url) {
				uni.navigateTo({
					url: url
				})
			},
			// 获取废品分类列表
			getWasteCategories() {
				// 显示加载提示
				uni.showLoading({
					title: '加载中...'
				})
				
				// 调用后端接口
				uni.request({
					url: 'http://localhost:8080/wasteCategory/list', // 请根据实际后端地址修改
					method: 'GET',
					success: (res) => {
						if (res.data.code === 1) { 
							// 处理价格显示，保留两位小数
							const list = res.data.data.map(item => ({
								...item,
								price: Number(item.price).toFixed(2)
							}))
							this.wasteList = list
						} else {
							uni.showToast({
								title: '获取数据失败',
								icon: 'none'
							})
						}
					},
					fail: (err) => {
						console.error('获取废品分类列表失败：', err)
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						})
					},
					complete: () => {
						uni.hideLoading()
					}
				})
			}
		}
	}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background-color: #f8f9fc;
}

.status-bar {
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
}

.content {
	padding-bottom: 30rpx;
}

.banner {
	position: relative;
	width: 100%;
	height: 380rpx;
	margin-bottom: 40rpx;
	overflow: hidden;
	
	&::after {
		content: '';
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		height: 100rpx;
		background: linear-gradient(to top, rgba(0,0,0,0.1), transparent);
		z-index: 1;
	}
	
	image {
		width: 100%;
		height: 100%;
		border-radius: 0 0 40rpx 40rpx;
		transform: scale(1.01);
		transition: transform 0.5s ease;
		
		&:active {
			transform: scale(1.05);
		}
	}
}

.category-list {
	display: flex;
	background-color: #ffffff;
	padding: 30rpx;
	margin: 0 30rpx 40rpx;
	border-radius: 24rpx;
	box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.06);
	justify-content: space-between;
	position: relative;
	
	// 添加小装饰元素
	&::before {
		content: '';
		position: absolute;
		top: -10rpx;
		left: 50%;
		transform: translateX(-50%);
		width: 100rpx;
		height: 10rpx;
		background: #55ACEE;
		border-radius: 10rpx 10rpx 0 0;
	}
	
	.category-item {
		flex: 1;
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 20rpx 10rpx;
		border-radius: 16rpx;
		transition: all 0.3s;
		
		&:active {
			background-color: #f0f4ff;
			transform: scale(0.92);
		}
		
		.icon-wrapper {
			width: 110rpx;
			height: 110rpx;
			background: linear-gradient(135deg, #e6f0ff, #f0f4ff);
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-bottom: 18rpx;
			box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
			position: relative;
			overflow: hidden;
			
			// 添加内部装饰
			&::after {
				content: '';
				position: absolute;
				width: 140%;
				height: 140%;
				background: radial-gradient(circle, rgba(255,255,255,0.8) 0%, rgba(255,255,255,0) 70%);
				top: -20%;
				left: -20%;
				opacity: 0.6;
			}
		}
		
		text {
			font-size: 26rpx;
			color: #444;
			font-weight: 500;
		}
	}
}

.section {
	margin: 0 30rpx 40rpx;
	background: #fff;
	padding: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.04);
	backdrop-filter: blur(10px);
	
	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
		
		.title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
			position: relative;
			padding-left: 24rpx;
			
			&::before {
				content: '';
				position: absolute;
				left: 0;
				top: 50%;
				transform: translateY(-50%);
				height: 34rpx;
				width: 8rpx;
				background: linear-gradient(to bottom, #4B6EFF, #55ACEE);
				border-radius: 4rpx;
			}
		}
		
		.more {
			font-size: 24rpx;
			color: #888;
			display: flex;
			align-items: center;
			background: #f5f7fa;
			padding: 6rpx 16rpx;
			border-radius: 20rpx;
			transition: all 0.3s;
			
			&:active {
				background: #e6f0ff;
				transform: translateX(5rpx);
			}
			
			&::after {
				content: ">";
				margin-left: 6rpx;
				font-size: 22rpx;
				transition: transform 0.3s;
			}
		}
	}
	
	.waste-list {
		.waste-items {
			.waste-item {
				background: #fff;
				border-radius: 20rpx;
				margin-bottom: 30rpx;
				overflow: hidden;
				border: 1px solid rgba(0, 0, 0, 0.03);
				transition: all 0.3s ease;
				
				.waste-info {
					padding: 24rpx;
					display: flex;
					align-items: center;
					position: relative;
					
					.waste-image {
						width: 150rpx;
						height: 150rpx;
						border-radius: 16rpx;
						object-fit: cover;
						background: #f5f7fa;
						box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.06);
						margin-right: 24rpx;
						transition: transform 0.3s ease;
					}
					
					.waste-content {
						flex: 1;
						min-width: 0;
						padding: 10rpx 0;
						
						.waste-name {
							font-size: 30rpx;
							font-weight: 600;
							color: #333;
							margin-bottom: 16rpx;
							display: block;
						}
						
						.waste-desc {
							font-size: 26rpx;
							color: #666;
							display: block;
							line-height: 1.4;
							display: -webkit-box;
							-webkit-line-clamp: 2;
							-webkit-box-orient: vertical;
							overflow: hidden;
						}
					}
					
					.waste-price {
						display: flex;
						flex-direction: column;
						align-items: flex-end;
						padding-left: 30rpx;
						position: relative;
						
						&::before {
							content: '';
							position: absolute;
							left: 0;
							top: 15%;
							bottom: 15%;
							width: 2px;
							background: linear-gradient(to bottom, rgba(75, 110, 255, 0.1), rgba(85, 174, 238, 0.1));
							border-radius: 1px;
						}
						
						.price {
							font-size: 38rpx;
							color: #ff6b6b;
							font-weight: 600;
							font-family: 'DIN Alternate', sans-serif;
							background: linear-gradient(to right, #ff6b6b, #ff8e8e);
							-webkit-background-clip: text;
							-webkit-text-fill-color: transparent;
						}
						
						.unit {
							font-size: 24rpx;
							color: #999;
							margin-top: 6rpx;
						}
					}
				}
			}
			
			.waste-item-hover {
				transform: translateY(-8rpx);
				box-shadow: 0 16rpx 32rpx rgba(0, 0, 0, 0.08);
				
				.waste-image {
					transform: scale(1.05);
				}
				
				.waste-price .price {
					background: linear-gradient(to right, #ff4d4f, #ff7875);
					-webkit-background-clip: text;
					-webkit-text-fill-color: transparent;
				}
			}
		}
	}
}

// 为scroll-view添加滚动条样式
::-webkit-scrollbar {
	width: 0;
	height: 0;
	color: transparent;
}

// 添加页面进入动画
@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translateY(20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.category-list, .section {
	animation: fadeInUp 0.6s ease forwards;
}

.section {
	animation-delay: 0.2s;
}

.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 100rpx); // 100rpx 是 tabbar 的高度
	background: transparent;
}

.nav-bar {
	height: 44px;
	display: flex;
	align-items: center;
	justify-content: center; /* 改为居中对齐 */
	padding: 0 16px;
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	position: relative;
	
	.title {
		/* 不再需要绝对定位 */
		position: static; 
		transform: none;
		font-size: 18px;
		font-weight: 600;
		color: #fff;
	}
}

/* 回收价目表区域样式优化 */
.section.waste-list {
	margin: 0 30rpx 40rpx;
	background: #fff;
	padding: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
	
	.section-header {
		margin-bottom: 30rpx;
		
		.title {
			position: relative;
			font-size: 34rpx;
			font-weight: 600;
			color: #333;
			display: flex;
			align-items: center;
			
			&::before {
				content: '';
				width: 8rpx;
				height: 32rpx;
				background: linear-gradient(to bottom, #4B6EFF, #55ACEE);
				border-radius: 4rpx;
				margin-right: 16rpx;
			}
		}
	}
	
	.waste-items {
		.waste-item {
			background: #fff;
			border-radius: 16rpx;
			margin-bottom: 24rpx;
			overflow: hidden;
			box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
			border: 1px solid rgba(0, 0, 0, 0.03);
			transition: all 0.3s;
			
			&:last-child {
				margin-bottom: 0;
			}
			
			.waste-info {
				padding: 20rpx;
				display: flex;
				align-items: center;
				
				.waste-image {
					width: 120rpx;
					height: 120rpx;
					border-radius: 12rpx;
					object-fit: cover;
					background: #f5f7fa;
					flex-shrink: 0;
					margin-right: 20rpx;
					border: 1px solid rgba(0, 0, 0, 0.03);
				}
				
				.info-content {
					flex: 1;
					display: flex;
					flex-direction: column;
					justify-content: center;
					min-width: 0;
					
					.waste-name {
						font-size: 30rpx;
						font-weight: 600;
						color: #333;
						margin-bottom: 10rpx;
						white-space: nowrap;
						overflow: hidden;
						text-overflow: ellipsis;
					}
					
					.waste-desc {
						font-size: 24rpx;
						color: #666;
						line-height: 1.4;
						display: -webkit-box;
						-webkit-box-orient: vertical;
						-webkit-line-clamp: 2;
						overflow: hidden;
					}
				}
				
				.waste-price {
					flex-shrink: 0;
					margin-left: 16rpx;
					text-align: right;
					padding-left: 16rpx;
					position: relative;
					
					&::before {
						content: '';
						position: absolute;
						left: 0;
						top: 25%;
						bottom: 25%;
						width: 1px;
						background-color: #f0f0f0;
					}
					
					.price {
						font-size: 36rpx;
						color: #ff5151;
						font-weight: 600;
						font-family: 'DIN Alternate', -apple-system, BlinkMacSystemFont, sans-serif;
					}
					
					.unit {
						font-size: 22rpx;
						color: #999;
						margin-top: 4rpx;
					}
				}
			}
			
			&:active {
				transform: scale(0.98);
				background-color: #fafafa;
			}
		}
	}
}
</style>
