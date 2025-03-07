<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<text class="title">我的单子</text>
		</view>
		
		<!-- 订单标签切换 -->
		<view class="order-tabs">
			<view class="tab-item" 
				:class="{ active: activeTab === 0 }"
				@click="switchTab(0)"
			>全部</view>
			<view class="tab-item"
				:class="{ active: activeTab === 2 }"
				@click="switchTab(2)"
			>已接单</view>
			<view class="tab-item"
				:class="{ active: activeTab === 3 }"
				@click="switchTab(3)"
			>已完成</view>
		</view>
		
		<!-- 订单列表 -->
		<scroll-view class="order-list" scroll-y refresher-enabled
			:refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			
			<block v-if="filteredOrders.length > 0">
				<view class="order-item" v-for="order in filteredOrders" :key="order.id">
					<!-- 状态标签 -->
					<text class="status-tag" 
						:class="{
							'completed': order.status === 3,
							'canceled': order.status === 4
						}"
					>{{ getStatusText(order.status) }}</text>
					
					<!-- 订单头部 -->
					<view class="order-header">
						<uni-icons class="order-icon" type="email-filled" size="24" color="#4B6EFF"></uni-icons>
						<text class="order-id">订单号：{{ order.id }}</text>
						<text class="order-time">{{ formatTime(order.createdAt) }}</text>
					</view>
					
					<!-- 订单内容 -->
					<view class="order-content">
						<view class="info-item">
							<uni-icons class="icon" type="calendar" size="16" color="#4B6EFF"></uni-icons>
							<text class="label">预约时间：</text>
							<text class="value">{{ formatDate(order.appointmentTime) }}</text>
						</view>
						
						<view class="info-item">
							<uni-icons class="icon" type="person" size="16" color="#4B6EFF"></uni-icons>
							<text class="label">联系人：</text>
							<text class="value">{{ order.contactName }} {{ order.contactPhone }}</text>
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
					
					<!-- 订单底部 -->
					<view class="order-footer">
						<view class="distance" v-if="order.latitude && order.longitude">
							<uni-icons class="icon" type="navigate" size="16" color="#4B6EFF"></uni-icons>
							<text class="value">距离：<text>{{ calculateDistance(order.latitude, order.longitude) }}</text></text>
						</view>
						
						<view class="action-buttons">
							<button class="complete-btn" 
								v-if="order.status === 2"
								@click="handleCompleteOrder(order.id)"
							>完成订单</button>
							
							<button class="nav-btn" 
								v-if="order.status !== 4"
								@click="navigateTo(order.latitude, order.longitude, order.address)"
							>导航</button>
						</view>
					</view>
				</view>
			</block>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="filteredOrders.length === 0">
				<image src="/static/images/empty-order.png" mode="aspectFit"></image>
				<text class="empty-text">暂无相关订单</text>
				<text class="empty-sub-text">有新订单时会在这里显示</text>
			</view>
			
			<!-- 底部安全区域 -->
			<view class="safe-area-bottom"></view>
		</scroll-view>
		
		<uni-popup ref="popup" type="center">
			<complete-order-form :orderId="currentOrderId" @success="onFormSuccess" @cancel="onFormCancel" />
		</uni-popup>
		
		<view class="navigation-container" v-if="showNavigation">
			<map
				class="navigation-map"
				:latitude="currentLocation.latitude"
				:longitude="currentLocation.longitude"
				:markers="mapMarkers"
				:polyline="routeLine"
				:scale="16"
				show-location
			></map>
			
			<view class="navigation-info">
				<view class="info-header">
					<text class="distance">{{navigationInfo.distance}}</text>
					<text class="duration">预计{{navigationInfo.duration}}</text>
				</view>
				<view class="destination">
					<text>目的地：{{navigationInfo.address}}</text>
				</view>
				<view class="nav-actions">
					<button class="nav-btn" @tap="closeNavigation">关闭导航</button>
					<button class="nav-btn primary" @tap="startNavigation">开始导航</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import CompleteOrderForm from '@/components/complete-order-form/complete-order-form.vue'
import { initAMap, getCurrentLocation, openNavigation, getRouteInfo } from '@/utils/map.js'

export default {
	components: {
		CompleteOrderForm
	},
	
	data() {
		return {
			statusBarHeight: 0,
			orders: [],
			refreshing: false,
			userInfo: null,
			currentOrderId: null,
			showNavigation: false,
			currentLocation: {
				latitude: 0,
				longitude: 0
			},
			mapMarkers: [],
			routeLine: [],
			navigationInfo: {
				distance: '',
				duration: '',
				address: ''
			},
			activeTab: 0,
			filteredOrders: []
		}
	},
	
	onLoad() {
		const userInfo = uni.getStorageSync('userInfo')
		console.log(userInfo);
		
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
		
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.userInfo = userInfo
		this.loadOrders()
		
		// 获取当前位置
		this.getCurrentUserLocation()
	},
	
	methods: {
		// 加载订单列表
		async loadOrders() {
			if (!this.userInfo) return
			
			try {
				const res = await uni.request({
					url: `http://localhost:8080/appointmentOrder/recycler/list/${this.userInfo.id}`,
					method: 'GET'
				})
				
				if (res.data.code === 1) {
					this.orders = res.data.data
					this.filteredOrders = res.data.data
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
		
		// 格式化日期
		formatDate(dateStr) {
			if (!dateStr) return ''
			const date = new Date(dateStr)
			return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 格式化时间
		formatTime(dateStr) {
			if (!dateStr) return ''
			const date = new Date(dateStr)
			return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
		},
		
		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				1: '待确认',
				2: '进行中',
				3: '已完成',
				4: '已取消'
			}
			return statusMap[status] || '未知状态'
		},
		
		// 切换标签
		switchTab(tab) {
			this.activeTab = tab
			this.filteredOrders = this.orders.filter(order => {
				if (this.activeTab === 0) return true
				return order.status === this.activeTab
			})
		},
		
		// 完成订单
		handleCompleteOrder(orderId) {
			this.currentOrderId = orderId
			this.$refs.popup.open()
		},
		
		// 表单提交成功
		onFormSuccess() {
			this.$refs.popup.close()
			this.loadOrders()
		},
		
		// 表单取消
		onFormCancel() {
			this.$refs.popup.close()
		},
		
		// 获取用户当前位置
		getCurrentUserLocation() {
			uni.getLocation({
				type: 'gcj02',
				success: (res) => {
					this.currentLocation = {
						latitude: res.latitude,
						longitude: res.longitude
					}
				},
				fail: (err) => {
					console.error('获取位置失败', err)
					uni.showToast({
						title: '获取位置失败，无法计算距离',
						icon: 'none'
					})
				}
			})
		},
		
		// 计算距离
		calculateDistance(targetLat, targetLng) {
			if (!targetLat || !targetLng || 
				!this.currentLocation.latitude || 
				!this.currentLocation.longitude) {
				return '未知'
			}
			
			// 将角度转换为弧度
			const radLat1 = (this.currentLocation.latitude * Math.PI) / 180
			const radLat2 = (targetLat * Math.PI) / 180
			const radLng1 = (this.currentLocation.longitude * Math.PI) / 180
			const radLng2 = (targetLng * Math.PI) / 180
			
			// 地球半径（千米）
			const EARTH_RADIUS = 6378.137
			
			// 计算距离
			const dLat = radLat2 - radLat1
			const dLng = radLng2 - radLng1
			const distance = 2 * Math.asin(
				Math.sqrt(
					Math.pow(Math.sin(dLat / 2), 2) +
					Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(dLng / 2), 2)
				)
			) * EARTH_RADIUS
			
			// 格式化距离显示
			if (distance < 1) {
				return Math.round(distance * 1000) + '米'
			} else {
				return distance.toFixed(1) + '公里'
			}
		},
		
		// 导航到订单地址
		async navigateTo(latitude, longitude, address) {
			if (!latitude || !longitude) {
				uni.showToast({
					title: '无法获取地址坐标',
					icon: 'none'
				})
				return
			}
			
			try {
				await openNavigation(
					latitude,
					longitude,
					'联系人',
					address
				)
			} catch (error) {
				uni.showToast({
					title: '导航失败',
					icon: 'none'
				})
			}
		},
		
		// 关闭导航
		closeNavigation() {
			this.showNavigation = false
		},
		
		// 开始导航
		startNavigation() {
			openNavigation(
				this.mapMarkers[1].latitude,
				this.mapMarkers[1].longitude,
				this.navigationInfo.address,
				this.navigationInfo.address
			).catch(() => {
				uni.showToast({
					title: '导航失败',
					icon: 'none'
				})
			})
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

/* 导航栏样式 */
.nav-bar {
	height: 44px;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0;
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	width: 100%;
	box-sizing: border-box;
	position: relative;
	
	.title {
		font-size: 18px;
		font-weight: 600;
		color: #fff;
		letter-spacing: 1px;
	}
}

/* 订单标签切换 */
.order-tabs {
	display: flex;
	background: #fff;
	padding: 4rpx 16rpx 0;
	border-bottom: 1rpx solid rgba(0, 0, 0, 0.05);
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.03);
	
	.tab-item {
		flex: 1;
		text-align: center;
		padding: 24rpx 0;
		font-size: 28rpx;
		color: #666;
		position: relative;
		transition: all 0.3s;
		
		&.active {
			color: #4B6EFF;
			font-weight: 500;
			
			&::after {
				content: '';
				position: absolute;
				bottom: 0;
				left: 25%;
				right: 25%;
				height: 6rpx;
				background: linear-gradient(to right, #4B6EFF, #55ACEE);
				border-radius: 6rpx 6rpx 0 0;
			}
		}
	}
}

/* 订单列表 */
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
		
		&.completed {
			background-color: #e3f8e4;
			color: #4caf50;
		}
		
		&.canceled {
			background-color: #f5f5f5;
			color: #999;
		}
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
		
		.order-time {
			font-size: 24rpx;
			color: #999;
			margin-left: auto;
			margin-right: 16rpx;
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
		
		.action-buttons {
			display: flex;
			gap: 16rpx;
		}
		
		.complete-btn {
			padding: 14rpx 36rpx;
			background: linear-gradient(135deg, #4CAF50, #8BC34A);
			color: #fff;
			border-radius: 40rpx;
			font-size: 28rpx;
			font-weight: 500;
			box-shadow: 0 4rpx 12rpx rgba(76, 175, 80, 0.2);
			transition: all 0.3s;
			
			&:active {
				transform: scale(0.96);
				opacity: 0.9;
			}
		}
		
		.nav-btn {
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

/* 空状态 */
.empty-state {
	padding: 120rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	
	image {
		width: 240rpx;
		height: 240rpx;
		margin-bottom: 30rpx;
		opacity: 0.8;
	}
	
	.empty-text {
		font-size: 32rpx;
		color: #333;
		margin-bottom: 16rpx;
	}
	
	.empty-sub-text {
		font-size: 28rpx;
		color: #999;
	}
}

/* 底部安全区域 */
.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 30rpx);
}

.navigation-container {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: #fff;
	z-index: 1000;
	
	.navigation-map {
		width: 100%;
		height: calc(100% - 160px);
	}
	
	.navigation-info {
		height: 160px;
		padding: 20px;
		background: #fff;
		box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
		
		.info-header {
			display: flex;
			align-items: center;
			margin-bottom: 12px;
			
			.distance {
				font-size: 24px;
				font-weight: 600;
				color: #333;
				margin-right: 12px;
			}
			
			.duration {
				font-size: 14px;
				color: #666;
			}
		}
		
		.destination {
			font-size: 14px;
			color: #666;
			margin-bottom: 20px;
		}
		
		.nav-actions {
			display: flex;
			gap: 12px;
			
			.nav-btn {
				flex: 1;
				height: 40px;
				line-height: 40px;
				border-radius: 20px;
				font-size: 16px;
				
				&.primary {
					background: linear-gradient(135deg, #2979ff, #1565C0);
					color: #fff;
					box-shadow: 0 2px 6px rgba(41, 121, 255, 0.3);
				}
			}
		}
	}
}
</style> 