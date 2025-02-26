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
// #ifdef H5
import { initAMap, getCurrentLocation, getAddressFromLocation } from '@/utils/map.js'
// #endif

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
				// #ifdef H5
				await initAMap()
				// #endif
				
				const location = await getCurrentLocation()
				this.form.address = location.address
				this.form.mapAddress = location.name
				this.form.longitude = location.longitude
				this.form.latitude = location.latitude
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
			// #ifdef H5
			try {
				await initAMap()
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
			// #endif
			
			// #ifdef MP-WEIXIN
			uni.chooseLocation({
				success: (res) => this.updateLocationInfo(res),
				fail: (err) => {
					console.error('选择地址失败：', err)
					uni.showToast({
						title: '选择地址失败',
						icon: 'none'
					})
				}
			})
			// #endif
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
				// 构建请求数据
				const appointmentData = {
					userId: uni.getStorageSync('userInfo').id,
					contactName: this.form.contactName,
					contactPhone: this.form.contactPhone,
					address: this.form.address,
					mapAddress: this.form.mapAddress || this.form.address,
					longitude: this.form.longitude ? Number(this.form.longitude) : null,
					latitude: this.form.latitude ? Number(this.form.latitude) : null,
					appointmentTime: `${this.form.appointmentTime}T09:00:00`,
					notes: this.form.notes || '',
					status: 1 // 待确认状态
				}
				
				console.log('提交的数据：', appointmentData)
				
				// #ifdef H5
				const res = await uni.request({
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
				// #endif
				
				// 统一处理响应
				if (res.statusCode === 200 && res.data.code === 1) {
					uni.showToast({
						title: '预约成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} else {
					throw new Error(res.data.msg || '预约失败')
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
/* 整体容器样式优化 */
.container {
	min-height: 100vh;
	background-color: #f5f7fa;
	display: flex;
	flex-direction: column;
}

/* 导航栏样式优化 */
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
		letter-spacing: 0.5px;
	}
}

/* 提示卡片样式优化 */
.tips-card {
	background: linear-gradient(135deg, rgba(41, 121, 255, 0.1), rgba(41, 121, 255, 0.05));
	border-radius: 16rpx;
	padding: 24rpx;
	margin-bottom: 30rpx;
	border: 1px solid rgba(41, 121, 255, 0.1);
	
	.tips-header {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
		
		.tips-title {
			font-size: 28rpx;
			color: #2979ff;
			font-weight: 600;
			margin-left: 8rpx;
		}
	}
	
	.tips-content {
		font-size: 26rpx;
		color: #666;
		line-height: 1.6;
	}
}

/* 表单区域样式优化 */
.form-group {
	background-color: #fff;
	border-radius: 16rpx;
	padding: 0 24rpx;
	margin-bottom: 30rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.form-item {
	padding: 24rpx 0;
	border-bottom: 1px solid rgba(0, 0, 0, 0.05);
	position: relative;
	
	&:last-child {
		border-bottom: none;
	}
	
	.label {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 16rpx;
		display: block;
		font-weight: 600;
	}
	
	input {
		font-size: 28rpx;
		color: #333;
		width: 100%;
		height: 80rpx;
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 0 24rpx;
		transition: all 0.3s;
		
		&:focus {
			background: #fff;
			box-shadow: 0 0 0 2px rgba(41, 121, 255, 0.1);
		}
	}
	
	textarea {
		width: 100%;
		height: 180rpx;
		font-size: 28rpx;
		color: #333;
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 20rpx 24rpx;
		transition: all 0.3s;
		
		&:focus {
			background: #fff;
			box-shadow: 0 0 0 2px rgba(41, 121, 255, 0.1);
		}
	}
	
	.word-count {
		position: absolute;
		right: 12rpx;
		bottom: 36rpx;
		font-size: 24rpx;
		color: #999;
		background: rgba(255, 255, 255, 0.9);
		padding: 4rpx 8rpx;
		border-radius: 6rpx;
	}
	
	&.location {
		.address-content {
			display: flex;
			justify-content: space-between;
			align-items: center;
			background: #f8f9fa;
			border-radius: 12rpx;
			padding: 20rpx 24rpx;
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
					margin-left: 8rpx;
					
					&.placeholder {
						color: #999;
					}
				}
			}
			
			.action-buttons {
				display: flex;
				align-items: center;
				
				.get-location {
					padding: 8rpx;
					border-radius: 8rpx;
					
					&:active {
						background: rgba(41, 121, 255, 0.1);
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
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 20rpx 24rpx;
		transition: all 0.3s;
		
		&:active {
			background: #f0f2f5;
		}
	}
}

/* 服务说明样式优化 */
.service-info {
	background: #fff;
	border-radius: 16rpx;
	padding: 30rpx;
	margin-bottom: 120rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
	
	.service-header {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		
		text {
			font-size: 28rpx;
			color: #333;
			font-weight: 600;
			margin-left: 8rpx;
		}
	}
	
	.service-items {
		.service-item {
			font-size: 26rpx;
			color: #666;
			line-height: 2;
			display: block;
			padding-left: 24rpx;
			position: relative;
			
			&::before {
				content: '';
				position: absolute;
				left: 8rpx;
				top: 18rpx;
				width: 6rpx;
				height: 6rpx;
				background: #2979ff;
				border-radius: 50%;
			}
		}
	}
}

/* 底部按钮样式优化 */
.bottom-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(255, 255, 255, 0.98);
	padding: 20rpx 30rpx;
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
	box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
	backdrop-filter: blur(10px);
}

.submit-btn {
	background: linear-gradient(135deg, #4a73f3, #2979ff);
	color: #fff;
	border-radius: 12rpx;
	font-size: 32rpx;
	font-weight: 600;
	height: 88rpx;
	line-height: 88rpx;
	letter-spacing: 1px;
	transition: all 0.3s;
	
	&:active {
		transform: scale(0.98);
		opacity: 0.9;
	}
}

.input-placeholder {
	color: #999;
}
</style> 