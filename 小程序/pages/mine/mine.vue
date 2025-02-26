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
	background: #f5f5f5;
}

.user-header {
	position: relative;
	padding: 60rpx 40rpx;
	background: linear-gradient(135deg, #4776E6, #8E54E9);
	
	.user-info {
		display: flex;
		align-items: center;
		
		.avatar {
			width: 120rpx;
			height: 120rpx;
			border-radius: 50%;
			border: 4rpx solid rgba(255, 255, 255, 0.2);
		}
		
		.info {
			margin-left: 30rpx;
			
			.nickname {
				display: block;
				font-size: 36rpx;
				color: #fff;
				margin-bottom: 10rpx;
			}
			
			.email {
				font-size: 24rpx;
				color: rgba(255, 255, 255, 0.8);
			}
		}
	}
	
	.setting-btn {
		position: absolute;
		top: 60rpx;
		right: 40rpx;
		width: 80rpx;
		height: 80rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.1);
		border-radius: 50%;
		backdrop-filter: blur(10px);
	}
}

.menu-list {
	margin-top: 20rpx;
	background: #fff;
	padding: 0 30rpx;
	
	.menu-item {
		display: flex;
		align-items: center;
		height: 100rpx;
		border-bottom: 1rpx solid #f5f5f5;
		
		&:last-child {
			border-bottom: none;
		}
		
		.icon {
			width: 48rpx;
			height: 48rpx;
			display: flex;
			align-items: center;
			justify-content: center;
		}
		
		.title {
			flex: 1;
			font-size: 28rpx;
			color: #333;
			margin-left: 20rpx;
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
}
</style> 