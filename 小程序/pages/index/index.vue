<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y>
			<view class="banner">
				<image src="https://picsum.photos/750/300" mode="aspectFill"></image>
			</view>
			
			<view class="category-list">
				<view class="category-item" @click="navigateTo('/pages/appointment/index')">
					<view class="icon-wrapper">
						<uni-icons type="calendar" size="28" color="#2979ff"></uni-icons>
					</view>
					<text>预约回收</text>
				</view>
				<view class="category-item" @click="navigateTo('/pages/order/list')">
					<view class="icon-wrapper">
						<uni-icons type="list" size="28" color="#ff6b6b"></uni-icons>
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
				wasteList: [
					{
						categoryName: '废纸皮',
						price: '1.5',
						unit: '公斤',
						imageUrl: 'https://picsum.photos/100/100?random=1',
						description: '干净的纸箱、纸皮等'
					},
					{
						categoryName: '废铜',
						price: '35',
						unit: '公斤',
						imageUrl: 'https://picsum.photos/100/100?random=2',
						description: '电线、铜管、紫铜等'
					},
					{
						categoryName: '废铝',
						price: '12',
						unit: '公斤',
						imageUrl: 'https://picsum.photos/100/100?random=3',
						description: '易拉罐、铝合金等'
					}
				]
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
		},
		methods: {
			navigateTo(url) {
				uni.navigateTo({
					url: url
				})
			}
		}
	}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #f0f5ff 0%, #f5f5f5 30%);
	display: flex;
	flex-direction: column;
}

.status-bar {
	background: transparent;
}

.content {
	flex: 1;
	height: 0;
}

.banner {
	margin: 30rpx 30rpx 40rpx;
	height: 300rpx;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
	
	image {
		width: 100%;
		height: 100%;
	}
}

.category-list {
	display: flex;
	justify-content: space-between;
	padding: 40rpx 30rpx;
	background: rgba(255, 255, 255, 0.9);
	margin: 0 30rpx 40rpx;
	border-radius: 24rpx;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
	backdrop-filter: blur(10px);
	
	.category-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		flex: 1;
		
		.icon-wrapper {
			width: 96rpx;
			height: 96rpx;
			background: #f8f9fa;
			border-radius: 48rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-bottom: 16rpx;
			transition: all 0.3s;
			
			&:active {
				transform: scale(0.95);
			}
		}
		
		text {
			font-size: 26rpx;
			color: #333;
			font-weight: 500;
		}
	}
}

.section {
	background: rgba(255, 255, 255, 0.9);
	margin: 0 30rpx 40rpx;
	padding: 30rpx;
	border-radius: 24rpx;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
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
		}
		
		.more {
			font-size: 24rpx;
			color: #999;
		}
	}
}

.waste-list {
	.waste-items {
		.waste-item {
			background: #fff;
			border-radius: 12rpx;
			margin-bottom: 20rpx;
			overflow: hidden;
			border: 1px solid #f0f0f0;
			transition: all 0.2s ease;
			
			.waste-info {
				padding: 24rpx;
				display: flex;
				align-items: center;
				position: relative;
				
				&::after {
					content: '';
					position: absolute;
					left: 24rpx;
					right: 24rpx;
					bottom: 0;
					height: 1px;
					background: #f5f5f5;
				}
				
				.waste-image {
					width: 120rpx;
					height: 120rpx;
					border-radius: 12rpx;
					object-fit: cover;
					background: #f5f5f5;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
				}
				
				.info-content {
					flex: 1;
					margin: 0 24rpx;
					
					.waste-name {
						font-size: 30rpx;
						font-weight: 500;
						color: #333;
						margin-bottom: 12rpx;
						display: block;
					}
					
					.waste-desc {
						font-size: 26rpx;
						color: #666;
						display: block;
						line-height: 1.4;
					}
				}
				
				.waste-price {
					display: flex;
					flex-direction: column;
					align-items: flex-end;
					padding-left: 24rpx;
					position: relative;
					
					&::before {
						content: '';
						position: absolute;
						left: 0;
						top: 10%;
						bottom: 10%;
						width: 1px;
						background: #f0f0f0;
					}
					
					.price {
						font-size: 36rpx;
						color: #ff4d4f;
						font-weight: 500;
						font-family: 'DIN Alternate', sans-serif;
					}
					
					.unit {
						font-size: 24rpx;
						color: #999;
						margin-top: 4rpx;
					}
				}
			}
		}
		
		.waste-item-hover {
			background: #f8f9fa;
			transform: translateX(8rpx);
			border-color: #e8e8e8;
			box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
			
			.waste-image {
				transform: scale(1.02);
				transition: transform 0.2s ease;
			}
			
			.price {
				color: #ff2a2a !important;
			}
		}
	}
}

.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 100rpx); // 100rpx 是 tabbar 的高度
	background: transparent;
}
</style>
