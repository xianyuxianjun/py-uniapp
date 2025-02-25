<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 自定义导航栏 - 放入搜索框 -->
		<view class="nav-bar">
			<view class="search-box">
				<uni-icons type="search" size="20" color="#666"></uni-icons>
				<input type="text" placeholder="搜索回收物品" placeholder-class="placeholder" />
			</view>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y>
			<view class="banner">
				<image src="https://picsum.photos/750/300" mode="aspectFill"></image>
			</view>
			
			<view class="category-list">
				<view class="category-item" v-for="(item, index) in categories" :key="index">
					<image :src="item.icon" mode="aspectFit"></image>
					<text>{{item.name}}</text>
				</view>
			</view>
			
			<view class="section">
				<view class="section-header">
					<text class="title">回收资讯</text>
					<text class="more">更多</text>
				</view>
				<view class="news-list">
					<view class="news-item" v-for="(item, index) in newsList" :key="index">
						<image :src="item.image" mode="aspectFill"></image>
						<view class="info">
							<text class="title">{{item.title}}</text>
							<text class="desc">{{item.desc}}</text>
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
				categories: [
					{ name: '废纸', icon: 'https://picsum.photos/80/80?random=1' },
					{ name: '塑料', icon: 'https://picsum.photos/80/80?random=2' },
					{ name: '金属', icon: 'https://picsum.photos/80/80?random=3' },
					{ name: '电器', icon: 'https://picsum.photos/80/80?random=4' }
				],
				newsList: [
					{
						title: '垃圾分类从我做起',
						desc: '让我们一起为环保出一份力',
						image: 'https://picsum.photos/200/140?random=5'
					},
					{
						title: '如何正确回收塑料制品',
						desc: '塑料回收小技巧',
						image: 'https://picsum.photos/200/140?random=6'
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
	justify-content: center;
	padding: 0 30rpx;
	
	.search-box {
		width: 100%;
		display: flex;
		align-items: center;
		height: 72rpx;
		padding: 0 30rpx;
		background: #f5f5f5;
		border-radius: 36rpx;
		
		input {
			flex: 1;
			height: 72rpx;
			font-size: 28rpx;
			margin-left: 20rpx;
		}
		
		.placeholder {
			color: #999;
		}
	}
}

.content {
	flex: 1;
	height: 0;
}

.banner {
	margin: 20rpx 30rpx;
	height: 300rpx;
	border-radius: 20rpx;
	overflow: hidden;
	
	image {
		width: 100%;
		height: 100%;
	}
}

.category-list {
	display: flex;
	justify-content: space-between;
	padding: 30rpx;
	background: #fff;
	margin-bottom: 20rpx;
	
	.category-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		
		image {
			width: 80rpx;
			height: 80rpx;
			margin-bottom: 16rpx;
		}
		
		text {
			font-size: 24rpx;
			color: #333;
		}
	}
}

.section {
	background: #fff;
	padding: 30rpx;
	
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
	
	.news-list {
		.news-item {
			display: flex;
			margin-bottom: 30rpx;
			
			&:last-child {
				margin-bottom: 0;
			}
			
			image {
				width: 200rpx;
				height: 140rpx;
				border-radius: 10rpx;
			}
			
			.info {
				flex: 1;
				margin-left: 20rpx;
				
				.title {
					font-size: 28rpx;
					color: #333;
					margin-bottom: 10rpx;
				}
				
				.desc {
					font-size: 24rpx;
					color: #999;
				}
			}
		}
	}
}

.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 100rpx); // 100rpx 是 tabbar 的高度
	background: transparent;
}
</style>
