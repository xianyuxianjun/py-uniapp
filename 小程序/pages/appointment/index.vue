<template>
	<view class="container">
		<!-- 状态栏占位 -->
		<view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
		
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="left" @click="navigateBack">
				<uni-icons type="left" size="20" color="#333"></uni-icons>
			</view>
			<text class="title">预约回收</text>
		</view>
		
		<!-- 表单区域 -->
		<scroll-view class="form-container" scroll-y>
			<!-- 温馨提示 -->
			<view class="tips-card">
				<view class="tips-header">
					<uni-icons type="info" size="18" color="#2979ff"></uni-icons>
					<text class="tips-title">温馨提示</text>
				</view>
				<text class="tips-content">请填写真实信息，我们将安排回收人员按预约时间上门服务</text>
			</view>
			
			<!-- 预约信息表单 -->
			<view class="form-group">
				<view class="form-item">
					<text class="label">联系人</text>
					<input 
						type="text" 
						v-model="form.contactName" 
						placeholder="请输入联系人姓名"
						placeholder-class="input-placeholder"
					/>
				</view>
				
				<view class="form-item">
					<text class="label">手机号码</text>
					<input 
						type="number" 
						v-model="form.contactPhone" 
						placeholder="请输入联系电话" 
						maxlength="11"
						placeholder-class="input-placeholder"
					/>
				</view>
				
				<view class="form-item location">
					<text class="label">上门地址</text>
					<view class="address-content">
						<view class="address-wrapper" @click="chooseLocation">
							<uni-icons v-if="!form.address" type="location" size="16" color="#999"></uni-icons>
							<text :class="['address-text', !form.address ? 'placeholder' : '']">
								{{ form.address || '点击选择地址' }}
							</text>
						</view>
						<view class="action-buttons">
							<text class="get-location" @click.stop="getLocation">
								<uni-icons type="refresh" size="14" color="#2979ff"></uni-icons>
							</text>
							<text class="divider"></text>
							<uni-icons type="right" size="16" color="#999" @click="chooseLocation"></uni-icons>
						</view>
					</view>
				</view>
				
				<view class="form-item">
					<text class="label">预约时间</text>
					<picker 
						mode="date" 
						:start="startDate" 
						:end="endDate" 
						@change="onDateChange"
					>
						<view class="picker-content">
							<view class="picker-wrapper">
								<uni-icons v-if="!form.appointmentTime" type="calendar" size="16" color="#999"></uni-icons>
								<text :class="['picker-text', !form.appointmentTime ? 'placeholder' : '']">
									{{ form.appointmentTime || '请选择预约时间' }}
								</text>
							</view>
							<uni-icons type="right" size="16" color="#999"></uni-icons>
						</view>
					</picker>
				</view>
				
				<view class="form-item">
					<text class="label">备注信息</text>
					<textarea 
						v-model="form.notes" 
						placeholder="请输入备注信息（选填）" 
						placeholder-class="input-placeholder"
						maxlength="200"
					/>
					<text class="word-count">{{ form.notes.length }}/200</text>
				</view>
			</view>
			
			<!-- 服务说明 -->
			<view class="service-info">
				<view class="service-header">
					<uni-icons type="help" size="16" color="#666"></uni-icons>
					<text>服务说明</text>
				</view>
				<view class="service-items">
					<text class="service-item">· 预约成功后将有专人与您联系确认</text>
					<text class="service-item">· 请保持电话畅通</text>
					<text class="service-item">· 如需取消预约请提前告知</text>
				</view>
			</view>
		</scroll-view>
		
		<!-- 底部提交按钮 -->
		<view class="bottom-bar">
			<button class="submit-btn" @click="submitForm" :loading="submitting">
				{{ submitting ? '提交中...' : '提交预约' }}
			</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			statusBarHeight: 0,
			submitting: false,
			form: {
				contactName: '',
				contactPhone: '',
				address: '',
				mapAddress: '',
				longitude: '',
				latitude: '',
				appointmentTime: '',
				notes: ''
			},
			startDate: this.getStartDate(),
			endDate: this.getEndDate(),
			locationLoading: false
		}
	},
	onLoad() {
		this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
		this.getLocation()
	},
	methods: {
		// 返回上一页
		navigateBack() {
			uni.navigateBack()
		},
		
		// 获取最早可预约日期（今天）
		getStartDate() {
			const date = new Date()
			return date.toISOString().split('T')[0]
		},
		
		// 获取最晚可预约日期（30天后）
		getEndDate() {
			const date = new Date()
			date.setDate(date.getDate() + 30)
			return date.toISOString().split('T')[0]
		},
		
		// 获取当前位置
		async getLocation() {
			if (this.locationLoading) return
			this.locationLoading = true
			
			try {
				const res = await new Promise((resolve, reject) => {
					uni.getLocation({
						type: 'gcj02',
						isHighAccuracy: true,
						success: resolve,
						fail: reject
					})
				})
				
				// 只获取当前位置，不自动打开地图选择
				this.form.longitude = res.longitude
				this.form.latitude = res.latitude
				
			} catch (error) {
				console.error('获取位置失败：', error)
				uni.showToast({
					title: '获取位置失败',
					icon: 'none'
				})
			} finally {
				this.locationLoading = false
			}
		},
		
		// 选择地址
		async chooseLocation() {
			try {
				const location = await new Promise((resolve, reject) => {
					uni.chooseLocation({
						success: resolve,
						fail: reject
					})
				})
				this.updateLocationInfo(location)
			} catch (error) {
				console.error('选择地址失败：', error)
				uni.showToast({
					title: '选择地址失败',
					icon: 'none'
				})
			}
		},
		
		// 更新位置信息
		updateLocationInfo(location) {
			this.form.address = location.address
			this.form.mapAddress = location.name
			this.form.longitude = location.longitude
			this.form.latitude = location.latitude
		},
		
		// 日期选择改变
		onDateChange(e) {
			this.form.appointmentTime = e.detail.value
		},
		
		// 表单验证
		validateForm() {
			if (!this.form.contactName) {
				uni.showToast({
					title: '请输入联系人姓名',
					icon: 'none'
				})
				return false
			}
			if (!this.form.contactPhone) {
				uni.showToast({
					title: '请输入联系电话',
					icon: 'none'
				})
				return false
			}
			if (!/^1[3-9]\d{9}$/.test(this.form.contactPhone)) {
				uni.showToast({
					title: '请输入正确的手机号码',
					icon: 'none'
				})
				return false
			}
			if (!this.form.address) {
				uni.showToast({
					title: '请选择上门地址',
					icon: 'none'
				})
				return false
			}
			if (!this.form.appointmentTime) {
				uni.showToast({
					title: '请选择预约时间',
					icon: 'none'
				})
				return false
			}
			return true
		},
		
		// 提交表单
		async submitForm() {
			if (!this.validateForm() || this.submitting) return
			
			this.submitting = true
			
			try {
				let response;
				// 构建请求数据
				const appointmentData = {
					userId: uni.getStorageSync('userInfo').id,
					contactName: this.form.contactName,
					contactPhone: this.form.contactPhone,
					address: this.form.address,
					mapAddress: this.form.mapAddress || this.form.address,
					longitude: this.form.longitude ? Number(this.form.longitude) : null,
					latitude: this.form.latitude ? Number(this.form.latitude) : null,
					// 修改时间格式，去掉毫秒
					appointmentTime: `${this.form.appointmentTime}T09:00:00`,
					notes: this.form.notes || '',
					status: 1
				}
				
				console.log('提交的数据：', appointmentData)
				
				// #ifdef H5
				response = await uni.request({
					url: 'http://localhost:8080/appointmentOrder',
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: appointmentData
				})
				// #endif
				
				// #ifdef MP-WEIXIN
				const [error, res] = await uni.request({
					url: 'http://localhost:8080/appointmentOrder',
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: appointmentData
				})
				if (error) {
					throw new Error('网络请求失败')
				}
				response = res
				// #endif
				
				// 统一处理响应
				if (response.statusCode === 200 && response.data.code === 1) {
					uni.showToast({
						title: '预约成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.redirectTo({
							url: '/pages/my-orders/index'
						})
					}, 1500)
				} else {
					throw new Error(response.data.msg || '预约失败')
				}
			} catch (err) {
				console.error('提交预约失败：', err)
				uni.showToast({
					title: err.message || '网络请求失败',
					icon: 'none',
					duration: 2000
				})
			} finally {
				this.submitting = false
			}
		}
	}
}
</script>

<style lang="scss">
/* 容器样式优化，确保内容不会溢出 */
.container {
	min-height: 100vh;
	background-color: #f8f9fc;
	display: flex;
	flex-direction: column;
	box-sizing: border-box;
	width: 100%;
}

.status-bar {
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
}

/* 导航栏样式调整，确保标题居中且不会溢出 */
.nav-bar {
	height: 44px;
	display: flex;
	align-items: center;
	padding: 0 16px;
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	position: relative;
	width: 100%;
	box-sizing: border-box;
	
	.left {
		width: 36px;
		height: 36px;
		display: flex;
		align-items: center;
		justify-content: center;
		background: rgba(255, 255, 255, 0.2);
		border-radius: 50%;
		backdrop-filter: blur(5px);
		z-index: 1;
		
		&:active {
			background: rgba(255, 255, 255, 0.3);
		}
	}
	
	.title {
		position: absolute;
		left: 50%;
		transform: translateX(-50%);
		font-size: 18px;
		font-weight: 600;
		color: #fff;
		max-width: 60%;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
}

/* 表单容器样式优化，确保内容可见 */
.form-container {
	flex: 1;
	padding: 20rpx 30rpx;
	box-sizing: border-box;
	width: 100%;
}

.tips-card {
	background: rgba(75, 110, 255, 0.05);
	border: 1px solid rgba(75, 110, 255, 0.1);
	border-radius: 16rpx;
	padding: 24rpx;
	margin-bottom: 30rpx;
	
	.tips-header {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
		
		.tips-title {
			font-size: 28rpx;
			color: #4B6EFF;
			font-weight: 500;
			margin-left: 10rpx;
		}
	}
	
	.tips-content {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
	}
}

/* 输入区域样式，确保右侧边距足够 */
.form-group {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.03);
	
	.form-item {
		margin-bottom: 30rpx;
		width: 100%;
		box-sizing: border-box;
		
		&:last-child {
			margin-bottom: 0;
		}
		
		.label {
			font-size: 28rpx;
			color: #666;
			margin-bottom: 16rpx;
			display: block;
			font-weight: 500;
		}
		
		input, textarea {
			width: 100%;
			box-sizing: border-box;
			background: #f5f7fa;
			border-radius: 12rpx;
			height: 88rpx;
			padding: 20rpx 24rpx;
			font-size: 28rpx;
			color: #333;
			border: 2rpx solid transparent;
			transition: all 0.3s;
			
			&:focus {
				background: #fff;
				border-color: #4B6EFF;
				box-shadow: 0 0 0 4rpx rgba(75, 110, 255, 0.1);
			}
		}
		
		textarea {
			height: 200rpx;
			padding: 20rpx 24rpx;
			line-height: 1.5;
		}
		
		.word-count {
			font-size: 24rpx;
			color: #999;
			text-align: right;
			display: block;
			margin-top: 8rpx;
		}
	}
	
	.location {
		.address-content {
			display: flex;
			align-items: center;
			background: #f5f7fa;
			border-radius: 12rpx;
			padding: 24rpx;
			transition: all 0.3s;
			
			&:active {
				background: #f0f2f5;
			}
			
			.address-wrapper {
				flex: 1;
				display: flex;
				align-items: center;
				
				.address-text {
					font-size: 28rpx;
					color: #333;
					margin-left: 10rpx;
					
					&.placeholder {
						color: #999;
					}
				}
			}
			
			.action-buttons {
				display: flex;
				align-items: center;
				
				.get-location {
					width: 60rpx;
					height: 60rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					background: rgba(75, 110, 255, 0.08);
					border-radius: 50%;
					
					&:active {
						background: rgba(75, 110, 255, 0.15);
					}
				}
				
				.divider {
					width: 1px;
					height: 24rpx;
					background: #e8e8e8;
					margin: 0 12rpx;
				}
			}
		}
	}
	
	.picker-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		background: #f5f7fa;
		border-radius: 12rpx;
		padding: 24rpx;
		transition: all 0.3s;
		
		&:active {
			background: #f0f2f5;
		}
		
		.picker-wrapper {
			display: flex;
			align-items: center;
			
			.picker-text {
				margin-left: 10rpx;
				font-size: 28rpx;
				color: #333;
				
				&.placeholder {
					color: #999;
				}
			}
		}
	}
}

/* 服务说明样式优化 */
.service-info {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 120rpx;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.03);
	
	.service-header {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		
		text {
			font-size: 28rpx;
			color: #333;
			font-weight: 600;
			margin-left: 10rpx;
		}
	}
	
	.service-items {
		.service-item {
			position: relative;
			font-size: 26rpx;
			color: #666;
			line-height: 1.8;
			display: block;
			padding-left: 30rpx;
			margin-bottom: 10rpx;
			
			&:last-child {
				margin-bottom: 0;
			}
			
			&::before {
				content: '';
				position: absolute;
				left: 10rpx;
				top: 18rpx;
				width: 8rpx;
				height: 8rpx;
				background: #4B6EFF;
				border-radius: 50%;
				box-shadow: 0 0 0 4rpx rgba(75, 110, 255, 0.1);
			}
		}
	}
}

/* 底部按钮区域调整 */
.bottom-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(255, 255, 255, 0.98);
	padding: 24rpx 30rpx;
	padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
	backdrop-filter: blur(10px);
	z-index: 10;
	width: 100%;
	box-sizing: border-box;
}

.submit-btn {
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	color: #fff;
	border-radius: 45rpx;
	font-size: 32rpx;
	font-weight: 600;
	height: 90rpx;
	line-height: 90rpx;
	letter-spacing: 2rpx;
	box-shadow: 0 8rpx 20rpx rgba(75, 110, 255, 0.2);
	transition: all 0.3s;
	
	&:active {
		transform: scale(0.98);
		box-shadow: 0 4rpx 10rpx rgba(75, 110, 255, 0.2);
	}
}

.input-placeholder {
	color: #bbb;
}
</style> 