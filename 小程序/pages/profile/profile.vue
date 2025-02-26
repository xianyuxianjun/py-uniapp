<template>
	<view class="profile-container">
		<!-- 顶部导航栏 -->
		<view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
			<view class="nav-back" @click="goBack">
				<uni-icons type="left" size="20" color="#333"></uni-icons>
			</view>
			<text class="nav-title">个人资料</text>
			<view class="nav-placeholder"></view>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="profile-content" scroll-y>
			<!-- 头像区域 -->
			<view class="avatar-section">
				<view class="avatar-wrapper" @click="chooseAvatar">
					<image class="avatar" :src="userInfo.avatar || 'https://picsum.photos/200/200'" mode="aspectFill"></image>
					<view class="avatar-edit">
						<uni-icons type="camera-filled" size="20" color="#fff"></uni-icons>
					</view>
				</view>
				<text class="avatar-tip">点击更换头像</text>
			</view>
			
			<!-- 表单区域 -->
			<view class="form-section">
				<view class="section-title">
					<text>基本信息</text>
				</view>
				
				<view class="form-item">
					<text class="form-label">用户名</text>
					<view class="input-wrapper">
						<input class="form-input" type="text" v-model="userInfo.username" placeholder="请输入用户名" />
						<uni-icons v-if="userInfo.username" type="clear" size="16" color="#999" @click="userInfo.username = ''" />
					</view>
				</view>
				
				<view class="form-item">
					<text class="form-label">手机号</text>
					<view class="input-wrapper">
						<input class="form-input" type="number" v-model="userInfo.phone" placeholder="请输入手机号" maxlength="11" />
						<uni-icons v-if="userInfo.phone" type="clear" size="16" color="#999" @click="userInfo.phone = ''" />
					</view>
				</view>
				
				<view class="form-item">
					<text class="form-label">邮箱</text>
					<view class="input-wrapper">
						<input class="form-input" type="text" v-model="userInfo.email" placeholder="请输入邮箱" />
						<uni-icons v-if="userInfo.email" type="clear" size="16" color="#999" @click="userInfo.email = ''" />
					</view>
				</view>
				
				<view class="form-item">
					<text class="form-label">性别</text>
					<view class="gender-options">
						<view class="gender-option" :class="{ active: userInfo.gender === 1 }" @click="userInfo.gender = 1">
							<uni-icons v-if="userInfo.gender === 1" type="checkbox-filled" size="18" color="#4776E6" />
							<text>男</text>
						</view>
						<view class="gender-option" :class="{ active: userInfo.gender === 2 }" @click="userInfo.gender = 2">
							<uni-icons v-if="userInfo.gender === 2" type="checkbox-filled" size="18" color="#4776E6" />
							<text>女</text>
						</view>
						<view class="gender-option" :class="{ active: userInfo.gender === 0 }" @click="userInfo.gender = 0">
							<uni-icons v-if="userInfo.gender === 0" type="checkbox-filled" size="18" color="#4776E6" />
							<text>保密</text>
						</view>
					</view>
				</view>
			</view>
			
			<view class="form-section">
				<view class="section-title">
					<text>地址信息</text>
				</view>
				
				<view class="form-item">
					<text class="form-label">地址</text>
					<view class="address-input" @click="chooseAddress">
						<text class="address-text" :class="{ 'placeholder': !userInfo.address }">{{ userInfo.address || '请选择地址' }}</text>
						<uni-icons type="location" size="18" color="#4776E6"></uni-icons>
					</view>
				</view>
				
				<view class="form-item">
					<text class="form-label">详细地址</text>
					<view class="input-wrapper">
						<input class="form-input" type="text" v-model="userInfo.addressDetail" placeholder="请输入详细地址" />
						<uni-icons v-if="userInfo.addressDetail" type="clear" size="16" color="#999" @click="userInfo.addressDetail = ''" />
					</view>
				</view>
			</view>
			
			<view class="form-section">
				<view class="section-title">
					<text>个人简介</text>
				</view>
				
				<view class="form-item no-border">
					<textarea class="form-textarea" v-model="userInfo.bio" placeholder="请输入个人简介，让大家更好地了解你" maxlength="200" />
					<text class="textarea-counter">{{ userInfo.bio ? userInfo.bio.length : 0 }}/200</text>
				</view>
			</view>
			
			<!-- 保存按钮 -->
			<view class="save-section">
				<button class="save-btn" @click="saveProfile" :loading="loading">
					<text>保存修改</text>
					<uni-icons type="checkmarkempty" size="18" color="#fff"></uni-icons>
				</button>
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
				statusBarHeight: 0, // 状态栏高度
				userInfo: {
					id: '',
					username: '',
					phone: '',
					email: '',
					avatar: '',
					gender: 0, // 0-保密，1-男，2-女
					address: '',
					addressDetail: '',
					bio: ''
				},
				loading: false, // 保存按钮加载状态
				baseUrl: 'http://localhost:8080' // 后端接口基础URL，根据实际情况修改
			}
		},
		onLoad() {
			// 获取状态栏高度
			this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
			
			// 获取用户信息
			this.getUserInfo()
		},
		methods: {
			// 返回上一页
			goBack() {
				uni.navigateBack()
			},
			
			// 获取用户信息
			getUserInfo() {
				const userInfo = uni.getStorageSync('userInfo')
				if (userInfo) {
					// 合并用户信息，保留表单中已有的数据
					this.userInfo = {
						...this.userInfo,
						...userInfo
					}
				} else {
					uni.showToast({
						title: '请先登录',
						icon: 'none',
						success: () => {
							setTimeout(() => {
								uni.navigateTo({
									url: '/pages/login/login'
								})
							}, 1500)
						}
					})
				}
			},
			
			// 选择头像
			chooseAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						// 这里可以实现头像上传到服务器的逻辑
						// 为了演示，我们直接使用本地路径
						this.userInfo.avatar = res.tempFilePaths[0]
						
						uni.showToast({
							title: '头像已更新',
							icon: 'success'
						})
					}
				})
			},
			
			// 选择地址
			chooseAddress() {
				uni.chooseLocation({
					success: (res) => {
						this.userInfo.address = res.address
						// 可以根据需要保存更多地址信息
					},
					fail: (err) => {
						console.error('选择地址失败：', err)
						if (err.errMsg.indexOf('authorize') !== -1) {
							uni.showModal({
								title: '提示',
								content: '需要授权位置权限才能选择地址',
								confirmText: '去设置',
								success: (res) => {
									if (res.confirm) {
										uni.openSetting()
									}
								}
							})
						}
					}
				})
			},
			
			// 保存个人资料
			saveProfile() {
				// 表单验证
				if (!this.userInfo.username) {
					return uni.showToast({
						title: '请输入用户名',
						icon: 'none'
					})
				}
				
				if (this.userInfo.phone && !/^1\d{10}$/.test(this.userInfo.phone)) {
					return uni.showToast({
						title: '手机号格式不正确',
						icon: 'none'
					})
				}
				
				if (this.userInfo.email && !/^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/.test(this.userInfo.email)) {
					return uni.showToast({
						title: '邮箱格式不正确',
						icon: 'none'
					})
				}
				
				this.loading = true
				
				// 这里应该调用后端接口更新用户信息
				// 为了演示，我们直接更新本地存储
				setTimeout(() => {
					// 保存到本地存储
					uni.setStorageSync('userInfo', this.userInfo)
					
					this.loading = false
					uni.showToast({
						title: '保存成功',
						icon: 'success',
						success: () => {
							setTimeout(() => {
								uni.navigateBack()
							}, 1500)
						}
					})
				}, 1000)
				
				// 实际开发中应该调用后端接口
				/*
				uni.request({
					url: this.baseUrl + '/user/update',
					method: 'POST',
					data: this.userInfo,
					success: (res) => {
						if (res.data.code === 1) {
							// 更新本地存储
							uni.setStorageSync('userInfo', this.userInfo)
							
							uni.showToast({
								title: '保存成功',
								icon: 'success',
								success: () => {
									setTimeout(() => {
										uni.navigateBack()
									}, 1500)
								}
							})
						} else {
							uni.showToast({
								title: res.data.message || '保存失败',
								icon: 'none'
							})
						}
					},
					fail: (err) => {
						console.error('更新用户信息失败：', err)
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						})
					},
					complete: () => {
						this.loading = false
					}
				})
				*/
			}
		}
	}
</script>

<style lang="scss">
.profile-container {
	display: flex;
	flex-direction: column;
	min-height: 100vh;
	background-color: #f5f7fa;
}

// 导航栏样式
.nav-bar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	height: 88rpx;
	background-color: #fff;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	position: relative;
	z-index: 10;
	
	.nav-back, .nav-placeholder {
		width: 88rpx;
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.nav-title {
		font-size: 32rpx;
		font-weight: 500;
		color: #333;
	}
}

// 内容区域样式
.profile-content {
	flex: 1;
	height: 0;
}

// 头像区域样式
.avatar-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 40rpx 0;
	background-color: #fff;
	margin-bottom: 20rpx;
	border-radius: 0 0 24rpx 24rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
	
	.avatar-wrapper {
		position: relative;
		width: 160rpx;
		height: 160rpx;
		border-radius: 80rpx;
		overflow: hidden;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
		border: 4rpx solid rgba(255, 255, 255, 0.8);
		
		.avatar {
			width: 100%;
			height: 100%;
		}
		
		.avatar-edit {
			position: absolute;
			bottom: 0;
			left: 0;
			right: 0;
			height: 60rpx;
			background-color: rgba(0, 0, 0, 0.5);
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}
	
	.avatar-tip {
		font-size: 26rpx;
		color: #999;
		background-color: #f5f7fa;
		padding: 8rpx 20rpx;
		border-radius: 30rpx;
	}
}

// 表单区域样式
.form-section {
	background-color: #fff;
	padding: 0 30rpx;
	margin-bottom: 20rpx;
	border-radius: 16rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.03);
	
	.section-title {
		padding: 24rpx 0;
		font-size: 30rpx;
		font-weight: 600;
		color: #333;
		border-bottom: 1rpx solid #f0f0f0;
	}
	
	.form-item {
		position: relative;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
		
		&.no-border {
			border-bottom: none;
		}
		
		.form-label {
			font-size: 28rpx;
			color: #333;
			margin-bottom: 16rpx;
			display: block;
		}
		
		.input-wrapper {
			display: flex;
			align-items: center;
			
			.form-input {
				flex: 1;
				height: 80rpx;
				font-size: 30rpx;
				color: #333;
			}
		}
		
		.form-textarea {
			width: 100%;
			height: 200rpx;
			font-size: 30rpx;
			color: #333;
			line-height: 1.5;
			padding: 16rpx;
			background-color: #f9f9f9;
			border-radius: 12rpx;
		}
		
		.textarea-counter {
			position: absolute;
			right: 0;
			bottom: 24rpx;
			font-size: 24rpx;
			color: #999;
			background-color: rgba(255, 255, 255, 0.8);
			padding: 4rpx 12rpx;
			border-radius: 20rpx;
		}
		
		.gender-options {
			display: flex;
			
			.gender-option {
				flex: 1;
				height: 80rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				background-color: #f5f7fa;
				margin-right: 20rpx;
				border-radius: 12rpx;
				font-size: 28rpx;
				color: #666;
				transition: all 0.2s ease;
				
				&:last-child {
					margin-right: 0;
				}
				
				&.active {
					background-color: rgba(71, 118, 230, 0.1);
					color: #4776E6;
					font-weight: 500;
					box-shadow: 0 2rpx 8rpx rgba(71, 118, 230, 0.2);
				}
				
				uni-icons {
					margin-right: 8rpx;
				}
			}
		}
		
		.address-input {
			display: flex;
			align-items: center;
			justify-content: space-between;
			height: 80rpx;
			background-color: #f5f7fa;
			padding: 0 20rpx;
			border-radius: 12rpx;
			
			.address-text {
				flex: 1;
				font-size: 30rpx;
				color: #333;
				
				&.placeholder {
					color: #999;
				}
			}
		}
	}
}

// 保存按钮样式
.save-section {
	padding: 40rpx 30rpx;
	
	.save-btn {
		background: linear-gradient(135deg, #4776E6, #8E54E9);
		color: #fff;
		height: 90rpx;
		line-height: 90rpx;
		border-radius: 45rpx;
		font-size: 32rpx;
		font-weight: 500;
		box-shadow: 0 8rpx 16rpx rgba(74, 115, 243, 0.2);
		display: flex;
		align-items: center;
		justify-content: center;
		
		text {
			margin-right: 10rpx;
		}
	}
}

// 底部安全区域
.safe-area-bottom {
	height: calc(env(safe-area-inset-bottom) + 20rpx);
}
</style> 