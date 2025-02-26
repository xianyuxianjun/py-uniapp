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
		// 返回上一页
		navigateBack() {
			uni.navigateBack()
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

.category-stats {
	padding: 16rpx 30rpx;
	background: #fff;
	border-bottom: 1px solid rgba(0, 0, 0, 0.05);
	
	.total {
		font-size: 24rpx;
		color: #666;
		display: flex;
		align-items: center;
		
		&::before {
			content: '';
			width: 6rpx;
			height: 6rpx;
			background: #2979ff;
			border-radius: 50%;
			margin-right: 8rpx;
		}
	}
}

.waste-list {
	flex: 1;
	padding: 16rpx;
}

.waste-items {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.waste-item {
	background: #fff;
	border-radius: 12rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
	display: flex;
	width: 100%;
	box-sizing: border-box;
	
	.waste-image {
		width: 160rpx;
		height: 160rpx;
		flex-shrink: 0;
		background: #f5f7fa;
	}
	
	.waste-content {
		flex: 1;
		min-width: 0;
		padding: 16rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}
	
	.waste-info {
		display: flex;
		align-items: flex-start;
		gap: 16rpx;
		
		.name {
			flex: 1;
			font-size: 28rpx;
			font-weight: 600;
			color: #333;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
		
		.price {
			flex-shrink: 0;
			text-align: right;
			
			.amount {
				font-size: 32rpx;
				color: #ff6b6b;
				font-weight: 600;
				
				&::before {
					content: '¥';
					font-size: 22rpx;
					margin-right: 2rpx;
				}
			}
			
			.unit {
				font-size: 20rpx;
				color: #999;
				display: block;
			}
		}
	}
	
	.waste-desc {
		margin-top: 8rpx;
		
		text {
			font-size: 24rpx;
			color: #666;
			line-height: 1.4;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 2;
			overflow: hidden;
		}
	}
	
	&:active {
		transform: scale(0.99);
		opacity: 0.9;
	}
}
</style> 