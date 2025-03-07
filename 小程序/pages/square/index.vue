<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<text class="title">订单广场</text>
		</view>
		
		<!-- 订单列表 -->
		<scroll-view class="order-list" scroll-y refresher-enabled 
			:refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			
			<!-- 订单卡片 -->
			<view class="order-items" v-if="orders.length > 0">
				<view class="order-item" v-for="(order, index) in orders" :key="index">
					<text class="status-tag">待接单</text>
					
					<view class="order-header">
						<uni-icons class="order-icon" type="email-filled" size="24" color="#4B6EFF"></uni-icons>
						<text class="order-id">订单号：{{ order.id }}</text>
					</view>
					
					<view class="order-content">
						<view class="info-item">
							<uni-icons class="icon" type="calendar" size="16" color="#4B6EFF"></uni-icons>
							<text class="label">预约时间：</text>
							<text class="value">{{ formatDate(order.appointmentTime) }}</text>
						</view>
						
						<view class="info-item">
							<uni-icons class="icon" type="person" size="16" color="#4B6EFF"></uni-icons>
							<text class="label">联系人：</text>
							<text class="value">{{ order.contactName }} {{ formatPhone(order.contactPhone) }}</text>
						</view>
						
						<view class="info-item address">
							<uni-icons class="icon" type="location" size="16" color="#4B6EFF"></uni-icons>
							<text class="label">地址：</text>
							<text class="value">{{ order.address }}</text>
						</view>
						
						<view class="info-item" v-if="order.notes">
							<uni-icons class="icon" type="info" size="16" color="#4B6EFF"></uni-icons>
							<text class="label">备注：</text>
							<text class="value">{{ order.notes }}</text>
						</view>
					</view>
					
					<view class="order-footer">
						<view class="distance">
							<uni-icons class="icon" type="navigate" size="16" color="#4B6EFF"></uni-icons>
							<text class="value">距离：<text>{{ calculateDistance(order.latitude, order.longitude) }}</text></text>
						</view>
						
						<button class="accept-btn" 
							v-if="order.status === 1"
							@tap.stop="handleAcceptOrder(order.id)"
							:loading="acceptingId === order.id"
						>接单</button>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-else>
				<image src="/static/images/empty-order.png" mode="aspectFit"></image>
				<text>暂无可接订单</text>
				<text class="sub-text">刷新试试看</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			orders: [],
			refreshing: false,
			acceptingId: null, // 正在接单的订单ID
			currentLocation: null, // 当前位置
			userInfo: null
		}
	},
	
	onLoad() {
		const userInfo = uni.getStorageSync('userInfo')
		
		// 判断是否登录且是回收员
		if (!userInfo || userInfo.role !== 'recycler') {
			uni.showModal({
				title: '提示',
				content: '请先登录回收员账号',
				showCancel: false,
				success: () => {
					// 跳转到登录页
					uni.redirectTo({
						url: '/pages/login/login'
					})
				}
			})
			return
		}
		
		this.userInfo = userInfo
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.getCurrentLocation()
		this.loadOrders()
	},
	
	methods: {
		// 加载订单列表
		async loadOrders() {
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/appointmentOrder/list',
					method: 'GET',
					timeout: 10000
				})
				
				if (res.data.code === 1) {
					// 只显示待确认的订单
					this.orders = res.data.data.filter(order => order.status === 1)
				} else {
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				}
			} catch (e) {
				console.error(e)
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				})
			}
		},
		
		// 下拉刷新
		async onRefresh() {
			this.refreshing = true
			await this.loadOrders()
			this.refreshing = false
		},
		
		// 获取当前位置
		getCurrentLocation() {
			uni.getLocation({
				type: 'gcj02',
				success: res => {
					this.currentLocation = {
						latitude: res.latitude,
						longitude: res.longitude
					}
				},
				fail: () => {
					uni.showToast({
						title: '获取位置失败',
						icon: 'none'
					})
				}
			})
		},
		
		// 计算距离
		calculateDistance(lat2, lng2) {
			if (!this.currentLocation) return '未知距离'
			
			const lat1 = this.currentLocation.latitude
			const lng1 = this.currentLocation.longitude
			
			const radLat1 = lat1 * Math.PI / 180.0
			const radLat2 = lat2 * Math.PI / 180.0
			
			const a = radLat1 - radLat2
			const b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0
			
			let s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
				Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)))
			s = s * 6378.137
			s = Math.round(s * 10) / 10
			
			return s < 1 ? `${(s*1000).toFixed(0)}m` : `${s.toFixed(1)}km`
		},
		
		// 格式化日期
		formatDate(dateStr) {
			const date = new Date(dateStr)
			return `${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 格式化电话号码
		formatPhone(phone) {
			return phone ? phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : ''
		},
		
		// 接单
		async handleAcceptOrder(orderId) {
			if (this.acceptingId === orderId) return
			
			try {
				this.acceptingId = orderId
				const userInfo = uni.getStorageSync('userInfo')
				
				const res = await uni.request({
					url: `http://localhost:8080/appointmentOrder/accept/${orderId}?recyclerId=${userInfo.id}`,
					method: 'POST',
					timeout: 10000
				})
				
				if (res.data.code === 1) {
					uni.showToast({
						title: '接单成功',
						icon: 'success'
					})
					// 刷新列表
					this.loadOrders()
					
					// 跳转到我的单子页面
					setTimeout(() => {
						uni.switchTab({
							url: '/pages/orders/index'
						})
					}, 1500)
				} else {
					uni.showToast({
						title: res.data.msg || '接单失败',
						icon: 'none'
					})
				}
			} catch (e) {
				console.error(e)
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				})
			} finally {
				this.acceptingId = null
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

/* 顶部导航栏 */
.nav-bar {
	height: 44px;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0;
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	width: 100%;
	box-sizing: border-box;
	
	.title {
		font-size: 18px;
		font-weight: 600;
		color: #fff;
		letter-spacing: 1px;
	}
}

/* 订单列表区域 */
.order-list {
	flex: 1;
	padding: 24rpx 20rpx;
	box-sizing: border-box;
	width: 100%;
}

.order-item {
	background: #fff;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 24rpx;
	box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.04);
	position: relative;
	width: 100%;
	box-sizing: border-box;
	
	.status-tag {
		position: absolute;
		top: 30rpx;
		right: 30rpx;
		font-size: 24rpx;
		padding: 6rpx 20rpx;
		border-radius: 30rpx;
		background-color: #ffeed6;
		color: #ff9800;
		font-weight: 500;
	}
	
	.order-header {
		display: flex;
		align-items: center;
		padding-bottom: 20rpx;
		border-bottom: 1px dashed #eee;
		margin-bottom: 20rpx;
		padding-right: 100rpx; /* 为右侧状态腾出空间 */
		
		.order-icon {
			margin-right: 16rpx;
			color: #4B6EFF;
		}
		
		.order-id {
			font-size: 32rpx;
			font-weight: 600;
			color: #333;
		}
	}
	
	.order-content {
		.info-item {
			display: flex;
			margin-bottom: 16rpx;
			align-items: flex-start;
			
			.icon {
				margin-right: 16rpx;
				flex-shrink: 0;
				color: #4B6EFF;
				opacity: 0.85;
			}
			
			.label {
				width: 150rpx;
				flex-shrink: 0;
				font-size: 28rpx;
				color: #666;
			}
			
			.value {
				flex: 1;
				font-size: 28rpx;
				color: #333;
				word-break: break-all; /* 确保长文本可以换行 */
				padding-right: 20rpx; /* 确保右侧有足够空间 */
				line-height: 1.5;
			}
			
			&.address {
				.value {
					line-height: 1.6;
				}
			}
		}
	}
	
	.order-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-top: 24rpx;
		padding-top: 20rpx;
		border-top: 1px dashed #eee;
		
		.distance {
			display: flex;
			align-items: center;
			
			.icon {
				margin-right: 8rpx;
				color: #4B6EFF;
			}
			
			.value {
				font-size: 26rpx;
				color: #666;
				
				text {
					color: #4B6EFF;
					font-weight: 500;
				}
			}
		}
		
		.accept-btn {
			padding: 14rpx 36rpx;
			background: linear-gradient(135deg, #4B6EFF, #55ACEE);
			color: #fff;
			border-radius: 40rpx;
			font-size: 28rpx;
			font-weight: 500;
			box-shadow: 0 4rpx 12rpx rgba(75, 110, 255, 0.2);
			transition: all 0.3s;
			
			&:active {
				transform: scale(0.96);
				opacity: 0.9;
			}
		}
	}
}

/* 添加底部安全区域 */
.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 30rpx);
}

.empty-state {
	padding: 60px 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	
	image {
		width: 120px;
		height: 120px;
		margin-bottom: 15px;
	}
	
	text {
		font-size: 16px;
		color: #333;
		
		&.sub-text {
			font-size: 14px;
			color: #999;
			margin-top: 5px;
		}
	}
}
</style> 