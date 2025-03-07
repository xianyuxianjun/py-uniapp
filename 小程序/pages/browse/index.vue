<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="left" @click="navigateBack">
				<uni-icons type="left" size="20" color="#333"></uni-icons>
			</view>
			<text class="title">废品浏览</text>
		</view>
		
		<!-- 分类统计 -->
		<view class="category-stats">
			<text class="total">共 {{wasteList.length}} 种可回收物</text>
		</view>
		
		<!-- 废品列表 -->
		<scroll-view class="waste-list" scroll-y>
			<view class="waste-items">
				<view class="waste-item" v-for="item in wasteList" :key="item.id">
					<image 
						class="waste-image" 
						:src="item.imageUrl || '/static/images/default-waste.png'" 
						mode="aspectFill"
					></image>
					<view class="waste-content">
						<view class="waste-info">
							<text class="name">{{item.categoryName}}</text>
							<view class="price">
								<text class="amount">{{item.price}}</text>
								<text class="unit">元/{{item.unit}}</text>
							</view>
						</view>
						<view class="waste-desc">
							<text>{{item.description}}</text>
						</view>
					</view>
				</view>
			</view>
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
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.loadWasteCategories()
	},
	
	methods: {
		// 修改返回方法，确保返回到首页
		navigateBack() {
			uni.switchTab({
				url: '/pages/index/index'
			})
		},
		
		// 加载废品分类数据
		async loadWasteCategories() {
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/wasteCategory/list',
					method: 'GET'
				})
				
				if (res.data.code === 1) {
					this.wasteList = res.data.data.map(item => ({
						...item,
						price: Number(item.price).toFixed(2)
					}))
				} else {
					throw new Error(res.data.msg)
				}
			} catch (err) {
				console.error('获取废品分类失败：', err)
				uni.showToast({
					title: '获取数据失败',
					icon: 'none'
				})
			}
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

.status-bar {
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
}

/* 顶部导航栏 - 与其他页面保持一致 */
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

/* 分类统计区域优化 */
.category-stats {
	padding: 24rpx 30rpx;
	background: #fff;
	border-bottom: 1rpx solid rgba(0, 0, 0, 0.05);
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
	
	.total {
		font-size: 26rpx;
		color: #666;
		display: flex;
		align-items: center;
		
		&::before {
			content: '';
			width: 10rpx;
			height: 10rpx;
			background: linear-gradient(135deg, #4B6EFF, #55ACEE);
			border-radius: 50%;
			margin-right: 12rpx;
			box-shadow: 0 0 0 4rpx rgba(75, 110, 255, 0.1);
		}
	}
}

/* 废品列表区域优化 */
.waste-list {
	flex: 1;
	padding: 24rpx 16rpx;
	width: 100%;
	box-sizing: border-box;
}

.waste-items {
	display: flex;
	flex-direction: column;
	gap: 24rpx;
	width: 100%;
	box-sizing: border-box;
}

.waste-item {
	background: #fff;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.05);
	display: flex;
	width: 100%;
	box-sizing: border-box;
	transition: all 0.3s ease;
	
	&:active {
		transform: translateY(-8rpx);
		box-shadow: 0 16rpx 30rpx rgba(0, 0, 0, 0.08);
	}
	
	.waste-image {
		width: 180rpx;
		height: 180rpx;
		flex-shrink: 0;
		background: #f5f7fa;
		object-fit: cover;
	}
	
	.waste-content {
		flex: 1;
		min-width: 0;
		padding: 20rpx 16rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
		width: calc(100% - 180rpx);
		box-sizing: border-box;
	}
	
	.waste-info {
		display: flex;
		align-items: flex-start;
		width: 100%;
		box-sizing: border-box;
		
		.name {
			flex: 1;
			font-size: 32rpx;
			font-weight: 600;
			color: #333;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
			padding-right: 10rpx;
			max-width: 60%;
		}
		
		.price {
			flex-shrink: 0;
			text-align: right;
			min-width: 120rpx;
			
			.amount {
				font-size: 36rpx;
				color: #ff5151;
				font-weight: 600;
				font-family: 'DIN Alternate', -apple-system, BlinkMacSystemFont, sans-serif;
				
				&::before {
					content: '¥';
					font-size: 22rpx;
					margin-right: 2rpx;
					vertical-align: middle;
				}
			}
			
			.unit {
				font-size: 22rpx;
				color: #999;
				display: block;
				margin-top: 4rpx;
			}
		}
	}
	
	.waste-desc {
		margin-top: 16rpx;
		background-color: #f9fafb;
		padding: 12rpx;
		border-radius: 12rpx;
		width: 100%;
		box-sizing: border-box;
		
		text {
			font-size: 26rpx;
			color: #666;
			line-height: 1.6;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 3;
			overflow: hidden;
			word-break: break-all;
		}
	}
}

/* 添加滚动到底部的安全区域 */
.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 30rpx);
}
</style> 