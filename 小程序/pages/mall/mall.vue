<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 自定义导航栏 -->
		<view class="nav-bar">
			<text class="title">商城</text>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y>
			<view class="mall-header">
				<view class="search-box">
					<uni-icons type="search" size="20" color="#666"></uni-icons>
					<input type="text" placeholder="搜索商品" placeholder-class="placeholder" />
				</view>
			</view>
			
			<view class="content-area">
				<text>商城页面开发中...</text>
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
			statusBarHeight: 0
		}
	},
	onLoad() {
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
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
	padding: 0 15px;
	
	.title {
		font-size: 16px;
		font-weight: 500;
		color: #333;
	}
}

.content {
	flex: 1;
	height: 0;
}

.mall-header {
	padding: 20rpx 30rpx;
	background: #fff;
	
	.search-box {
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

.content-area {
	padding: 30rpx;
	text-align: center;
	color: #999;
	font-size: 28rpx;
}

.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 100rpx);
	background: transparent;
}
</style> 