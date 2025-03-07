<template>
	<view class="container">
		<!-- 顶部装饰图案 -->
		<view class="header">
			<!-- 动态背景 -->
			<view class="animated-background">
				<view class="gradient-layer"></view>
				<view class="bubble-group">
					<view class="bubble" v-for="i in 6" :key="i"></view>
				</view>
			</view>
			
			<!-- 头像图标 -->
			<view class="avatar-box">
				<image src="/static/logo.svg" mode="aspectFill" class="avatar-image"></image>
			</view>
		</view>
		
		<!-- 登录表单 -->
		<view class="login-form">
			<view class="welcome">
				<text class="title">欢迎回来</text>
				<text class="subtitle">登录您的账号开始探索</text>
			</view>
			
			<view class="form-group">
				<view class="input-item">
					<uni-icons type="email-filled" size="22" color="#4a73f3"></uni-icons>
					<input 
						type="text" 
						v-model="form.email" 
						placeholder="请输入邮箱"
						placeholder-class="placeholder"
					/>
				</view>
				<view class="input-item">
					<uni-icons type="locked-filled" size="22" color="#4a73f3"></uni-icons>
					<input 
						:type="showPassword ? 'text' : 'password'" 
						v-model="form.password" 
						placeholder="请输入密码"
						placeholder-class="placeholder"
					/>
					<uni-icons 
						:type="showPassword ? 'eye-filled' : 'eye-slash-filled'" 
						size="22" 
						color="#999"
						@click="togglePassword"
					></uni-icons>
				</view>
			</view>
			
			<button class="submit-btn" :class="{loading: isLoading}" @tap="handleLogin">
				<text v-if="!isLoading">登 录</text>
				<view v-else class="loading-icon"></view>
			</button>
			
			<view class="action-links">
				<text class="forgot-pwd">忘记密码？</text>
				<text class="register" @tap="goToRegister">注册账号</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			form: {
				email: '',
				password: ''
			},
			showPassword: false,
			isLoading: false
		}
	},
	methods: {
		togglePassword() {
			this.showPassword = !this.showPassword
		},
		async handleLogin() {
			if (this.isLoading) return
			
			if (!this.form.email || !this.form.password) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}
			
			this.isLoading = true
			
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/auth/login',
					method: 'POST',
					data: {
						email: this.form.email,
						passwordHash: this.form.password
					}
				})
				
				if (res.data.code === 1) {
					uni.setStorageSync('userInfo', res.data.data)
					uni.showToast({
						title: '登录成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.reLaunch({
							url: '/pages/index/index'
						})
					}, 1500)
				} else {
					uni.showToast({
						title: res.data.msg,
						icon: 'none'
					})
				}
			} catch (e) {
				uni.showToast({
					title: '登录失败',
					icon: 'none'
				})
			} finally {
				this.isLoading = false
			}
		},
		goToRegister() {
			uni.navigateTo({
				url: '/pages/register/register'
			})
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	display: flex;
	flex-direction: column;
	background: #fff;
}

.header {
	position: relative;
	height: 400rpx;
	overflow: hidden;
	
	.animated-background {
		position: absolute;
		width: 100%;
		height: 100%;
		
		.gradient-layer {
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background: linear-gradient(135deg, #4B6EFF, #55ACEE);
			animation: gradientMove 15s ease infinite;
		}
		
		.bubble-group {
			position: absolute;
			width: 100%;
			height: 100%;
			
			.bubble {
				position: absolute;
				border-radius: 50%;
				background: rgba(255, 255, 255, 0.1);
				animation: floatBubble 15s infinite ease-in;
				
				&:nth-child(1) {
					width: 120rpx;
					height: 120rpx;
					left: 10%;
					bottom: -60rpx;
					animation-duration: 25s;
					animation-delay: 0s;
				}
				
				&:nth-child(2) {
					width: 80rpx;
					height: 80rpx;
					left: 30%;
					bottom: -40rpx;
					animation-duration: 20s;
					animation-delay: 2s;
				}
				
				&:nth-child(3) {
					width: 160rpx;
					height: 160rpx;
					left: 50%;
					bottom: -80rpx;
					animation-duration: 28s;
					animation-delay: 1s;
				}
				
				&:nth-child(4) {
					width: 100rpx;
					height: 100rpx;
					left: 70%;
					bottom: -50rpx;
					animation-duration: 22s;
					animation-delay: 3s;
				}
				
				&:nth-child(5) {
					width: 140rpx;
					height: 140rpx;
					left: 85%;
					bottom: -70rpx;
					animation-duration: 26s;
					animation-delay: 2s;
				}
				
				&:nth-child(6) {
					width: 180rpx;
					height: 180rpx;
					left: 5%;
					bottom: -90rpx;
					animation-duration: 30s;
					animation-delay: 0.5s;
				}
			}
		}
	}
	
	.avatar-box {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -70%);
		width: 160rpx;
		height: 160rpx;
		background: #fff;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
		animation: pulseAvatar 4s ease infinite;
		z-index: 2;
		
		.avatar-image {
			width: 120rpx;
			height: 120rpx;
		}
	}
}

.login-form {
	position: relative;
	margin-top: -60rpx;
	background: #fff;
	border-radius: 40rpx 40rpx 0 0;
	flex: 1;
	padding: 90rpx 50rpx 50rpx;
	box-shadow: 0 -20rpx 60rpx rgba(0, 0, 0, 0.1);
	z-index: 1;
}

.welcome {
	text-align: center;
	margin-bottom: 60rpx;
	
	.title {
		font-size: 48rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 16rpx;
		display: block;
	}
	
	.subtitle {
		font-size: 28rpx;
		color: #999;
	}
}

.form-group {
	margin-bottom: 50rpx;
	
	.input-item {
		display: flex;
		align-items: center;
		height: 100rpx;
		padding: 0 30rpx;
		background: #f8f9fa;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
		transition: all 0.3s;
		border: 2rpx solid transparent;
		
		&:focus-within {
			background: #fff;
			border-color: #4B6EFF;
			box-shadow: 0 0 0 4rpx rgba(75, 110, 255, 0.1);
		}
		
		.iconfont {
			font-size: 40rpx;
			color: #4B6EFF;
			margin-right: 20rpx;
		}
		
		input {
			flex: 1;
			font-size: 28rpx;
			color: #333;
			background: transparent;
			border: none;
			padding: 0;
		}
		
		.placeholder {
			color: #bbb;
		}
	}
}

.submit-btn {
	width: 100%;
	height: 100rpx;
	line-height: 100rpx;
	text-align: center;
	background: linear-gradient(135deg, #4B6EFF, #55ACEE);
	color: #fff;
	font-size: 32rpx;
	font-weight: 500;
	border-radius: 50rpx;
	margin-bottom: 40rpx;
	transition: all 0.3s;
	
	&.loading {
		opacity: 0.8;
	}
	
	&:active {
		transform: scale(0.98);
		box-shadow: 0 6rpx 20rpx rgba(75, 110, 255, 0.3);
	}
}

.loading-icon {
	display: inline-block;
	width: 36rpx;
	height: 36rpx;
	border: 4rpx solid #fff;
	border-bottom-color: transparent;
	border-radius: 50%;
	animation: rotate 1s linear infinite;
}

.action-links {
	display: flex;
	justify-content: space-between;
	font-size: 28rpx;
	
	.forgot-pwd {
		color: #999;
	}
	
	.register {
		color: #4B6EFF;
		font-weight: 500;
	}
}

@keyframes rotate {
	from { transform: rotate(0deg); }
	to { transform: rotate(360deg); }
}

@keyframes gradientMove {
	0% {
		transform: rotate(0deg);
		background-position: 0% 0%;
	}
	50% {
		background-position: 100% 100%;
	}
	100% {
		transform: rotate(360deg);
		background-position: 0% 0%;
	}
}

@keyframes floatBubble {
	0% {
		transform: translateY(0) rotate(0deg);
		opacity: 0;
	}
	20% {
		opacity: 0.8;
	}
	80% {
		opacity: 0.8;
	}
	100% {
		transform: translateY(-100vh) rotate(360deg);
		opacity: 0;
	}
}

@keyframes pulseAvatar {
	0% {
		transform: translate(-50%, -70%) scale(1);
		box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
	}
	50% {
		transform: translate(-50%, -70%) scale(1.05);
		box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
	}
	100% {
		transform: translate(-50%, -70%) scale(1);
		box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
	}
}
</style> 