<template>
	<view class="container">
		<view class="user-header">
			<view class="user-info">
				<image class="avatar" :src="userInfo.avatar || 'https://picsum.photos/120/120'" mode="aspectFill"></image>
				<view class="info">
					<text class="nickname">{{userInfo.username || '未登录'}}</text>
					<text class="email">{{userInfo.email || '点击登录账号'}}</text>
				</view>
			</view>
			<view class="setting-btn" @tap="handleMenu(menuList[0])">
				<uni-icons type="gear" size="24" color="#fff"></uni-icons>
			</view>
		</view>
		
		<view class="menu-list">
			<view class="menu-item" v-for="(item, index) in menuList" :key="index" @tap="handleMenu(item)">
				<view class="icon">
					<uni-icons :type="item.icon" size="24" color="#4a73f3"></uni-icons>
				</view>
				<text class="title">{{item.title}}</text>
				<uni-icons type="right" size="16" color="#999"></uni-icons>
			</view>
		</view>
		
		<button class="logout-btn" @tap="handleLogout" v-if="userInfo.id">退出登录</button>
	</view>
</template>

<script>
export default {
	data() {
		return {
			userInfo: {},
			menuList: [
				{
					icon: 'person',
					title: '个人资料',
					path: '/pages/profile/profile'
				},
				{
					icon: 'calendar',
					title: '回收记录',
					path: '/pages/my-orders/index'
				},
				{
					icon: 'cart',
					title: '我的订单',
					path: '/pages/order-list/index'
				}
			]
		}
	},
	onShow() {
		this.getUserInfo()
	},
	methods: {
		getUserInfo() {
			const userInfo = uni.getStorageSync('userInfo')
			console.log(userInfo)
			if (userInfo) {
				this.userInfo = userInfo
			}
		},
		handleMenu(item) {
			if (!this.userInfo.id) {
				uni.navigateTo({
					url: '/pages/login/login'
				})
				return
			}
			
			// 如果是我的订单，需要传递用户ID
			if (item.title === '我的订单') {
				uni.navigateTo({
					url: `${item.path}?userId=${this.userInfo.id}`
				})
				return
			}
			
			uni.navigateTo({
				url: item.path
			})
		},
		handleLogout() {
			uni.showModal({
				title: '提示',
				content: '确定要退出登录吗？',
				success: (res) => {
					if (res.confirm) {
						// 清除用户信息和token
						uni.removeStorageSync('userInfo')
						uni.removeStorageSync('token')
						this.userInfo = {}
						
						uni.showToast({
							title: '已退出登录',
							icon: 'success',
							duration: 1500,
							success: () => {
								// 延迟跳转，让用户看到提示
								setTimeout(() => {
									// 跳转到登录页面
									uni.reLaunch({
										url: '/pages/login/login'
									})
								}, 1500)
							}
						})
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
	background: #f8f9fc;
}

.user-header {
	position: relative;
	padding: 80rpx 40rpx 100rpx;
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	border-radius: 0 0 40rpx 40rpx;
	overflow: hidden;
	
	// 添加背景气泡装饰
	&::before, &::after {
		content: '';
		position: absolute;
		background: rgba(255, 255, 255, 0.1);
		border-radius: 50%;
	}
	
	&::before {
		width: 300rpx;
		height: 300rpx;
		bottom: -150rpx;
		left: -100rpx;
	}
	
	&::after {
		width: 200rpx;
		height: 200rpx;
		top: -100rpx;
		right: -50rpx;
	}
	
	.user-info {
		position: relative;
		z-index: 1;
		display: flex;
		align-items: center;
	}
	
	.avatar {
		width: 140rpx;
		height: 140rpx;
		border-radius: 50%;
		border: 6rpx solid rgba(255, 255, 255, 0.3);
		background-color: #fff;
		box-shadow: 0 10rpx 20rpx rgba(0, 0, 0, 0.1);
	}
	
	.info {
		margin-left: 24rpx;
		flex: 1;
		
		.nickname {
			font-size: 36rpx;
			color: #fff;
			font-weight: 600;
			margin-bottom: 8rpx;
			text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
			display: block;
		}
		
		.email {
			font-size: 24rpx;
			color: rgba(255, 255, 255, 0.85);
			display: block;
		}
	}
	
	.setting-btn {
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.15);
		border-radius: 50%;
		backdrop-filter: blur(10px);
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
		transition: all 0.3s;
		
		&:active {
			transform: scale(0.95);
			background: rgba(255, 255, 255, 0.25);
		}
	}
}

.menu-list {
	margin: -60rpx 30rpx 40rpx;
	background: #fff;
	padding: 20rpx;
	border-radius: 24rpx;
	box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.05);
	position: relative;
	z-index: 2;
	
	.menu-item {
		display: flex;
		align-items: center;
		height: 110rpx;
		border-bottom: 1rpx solid #f5f5f5;
		padding: 0 20rpx;
		transition: all 0.3s;
		border-radius: 16rpx;
		
		&:last-child {
			border-bottom: none;
		}
		
		&:active {
			background-color: #f0f4ff;
			transform: translateX(10rpx);
		}
		
		.icon {
			width: 60rpx;
			height: 60rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			background: #f5f7fa;
			border-radius: 16rpx;
			margin-right: 24rpx;
		}
		
		.title {
			flex: 1;
			font-size: 30rpx;
			color: #333;
			font-weight: 500;
		}
	}
}

.logout-btn {
	margin: 60rpx 40rpx;
	height: 90rpx;
	line-height: 90rpx;
	background: #fff;
	color: #ff5151;
	font-size: 30rpx;
	border-radius: 45rpx;
	font-weight: 600;
	box-shadow: 0 6rpx 16rpx rgba(255, 81, 81, 0.1);
	position: relative;
	overflow: hidden;
	
	&::after {
		border: none;
	}
	
	&:active {
		transform: scale(0.98);
		background: #fff5f5;
	}
}

// 添加波纹动画效果
@keyframes ripple {
	0% {
		transform: scale(0);
		opacity: 0.5;
	}
	100% {
		transform: scale(2);
		opacity: 0;
	}
}
</style> 