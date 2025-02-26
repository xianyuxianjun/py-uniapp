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
				<view class="order-item" v-for="order in orders" :key="order.id">
					<view class="order-header">
						<text class="order-id">订单号：{{order.id}}</text>
						<text class="order-status" :class="'status-' + order.status">
							{{getStatusText(order.status)}}
						</text>
					</view>
					
					<view class="order-content">
						<!-- 预约时间 -->
						<view class="info-row">
							<uni-icons type="calendar" size="16" color="#666"></uni-icons>
							<text class="label">预约时间：</text>
							<text class="value">{{formatDate(order.appointmentTime)}}</text>
						</view>
						
						<!-- 联系人信息 -->
						<view class="info-row">
							<uni-icons type="person" size="16" color="#666"></uni-icons>
							<text class="label">联系人：</text>
							<text class="value">{{order.contactName}}</text>
							<text class="phone">{{formatPhone(order.contactPhone)}}</text>
						</view>
						
						<!-- 地址信息 -->
						<view class="info-row address">
							<uni-icons type="location" size="16" color="#666"></uni-icons>
							<text class="label">地址：</text>
							<text class="value">{{order.address}}</text>
						</view>
						
						<!-- 备注信息 -->
						<view class="info-row" v-if="order.notes">
							<uni-icons type="info" size="16" color="#666"></uni-icons>
							<text class="label">备注：</text>
							<text class="value">{{order.notes}}</text>
						</view>
					</view>
					
					<view class="order-footer">
						<view class="location-info" v-if="order.latitude && order.longitude">
							<text class="distance">{{calculateDistance(order.latitude, order.longitude)}}</text>
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
		
		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				1: '待接单',
				2: '已接单',
				3: '已完成',
				4: '已取消'
			}
			return statusMap[status] || '未知状态'
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
	background: #f5f5f5;
}

.nav-bar {
	position: sticky;
	top: 0;
	z-index: 100;
	background: #fff;
	height: 44px;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 15px;
	box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
	
	.title {
		font-size: 16px;
		font-weight: 600;
		color: #333;
	}
}

.order-list {
	padding: 15px;
}

.order-item {
	background: #fff;
	border-radius: 12px;
	padding: 15px;
	margin-bottom: 15px;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
	
	.order-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12px;
		
		.order-id {
			font-size: 14px;
			color: #666;
		}
		
		.order-status {
			font-size: 14px;
			font-weight: 500;
			
			&.status-1 {
				color: #2979ff;
			}
			
			&.status-2 {
				color: #ff9800;
			}
			
			&.status-3 {
				color: #4caf50;
			}
			
			&.status-4 {
				color: #999;
			}
		}
	}
	
	.order-content {
		.info-row {
			display: flex;
			align-items: center;
			margin-bottom: 10px;
			
			.label {
				font-size: 14px;
				color: #666;
				margin: 0 5px;
			}
			
			.value {
				font-size: 14px;
				color: #333;
			}
			
			.phone {
				margin-left: 10px;
				font-size: 14px;
				color: #666;
			}
			
			&.address {
				align-items: flex-start;
				
				.value {
					flex: 1;
					line-height: 1.4;
				}
			}
		}
	}
	
	.order-footer {
		margin-top: 15px;
		padding-top: 15px;
		border-top: 1px solid #f0f0f0;
		display: flex;
		justify-content: space-between;
		align-items: center;
		
		.location-info {
			.distance {
				font-size: 14px;
				color: #666;
				
				&::before {
					content: '距离：';
				}
			}
		}
		
		.accept-btn {
			margin: 0;
			padding: 0 20px;
			height: 32px;
			line-height: 32px;
			font-size: 14px;
			color: #fff;
			background: #2979ff;
			border-radius: 16px;
			
			&:active {
				opacity: 0.8;
			}
		}
	}
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