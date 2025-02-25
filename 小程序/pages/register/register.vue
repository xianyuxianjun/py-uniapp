<template>
	<view class="container">
		<view class="header">
			<!-- 动态背景 -->
			<view class="animated-background">
				<view class="gradient-layer"></view>
				<view class="bubble-group">
					<view class="bubble" v-for="i in 6" :key="i"></view>
				</view>
			</view>
			
			<!-- 返回按钮 -->
			<view class="nav-back" @tap="goToLogin">
				<uni-icons type="back" size="24" color="#fff"></uni-icons>
			</view>
			
			<!-- 注册图标 -->
			<view class="register-icon">
				<uni-icons type="person-add-filled" size="65" color="#fff"></uni-icons>
			</view>
		</view>
		
		<view class="register-form">
			<view class="welcome">
				<text class="title">创建账号</text>
				<text class="subtitle">开启您的回收之旅</text>
			</view>
			
			<view class="form-group">
				<view class="input-item">
					<uni-icons type="person-filled" size="22" color="#4a73f3"></uni-icons>
					<input 
						type="text" 
						v-model="form.username" 
						placeholder="请输入用户名"
						placeholder-class="placeholder"
					/>
				</view>
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
				<view class="input-item">
					<uni-icons type="locked-filled" size="22" color="#4a73f3"></uni-icons>
					<input 
						:type="showPassword ? 'text' : 'password'" 
						v-model="form.confirmPassword" 
						placeholder="请确认密码"
						placeholder-class="placeholder"
					/>
				</view>
			</view>
			
			<view class="agreement">
				<view class="checkbox-wrap" @tap="toggleAgreement">
					<view class="checkbox" :class="{ active: agreed }">
						<uni-icons v-if="agreed" type="checkmark" size="12" color="#fff"></uni-icons>
					</view>
					<text class="text">我已阅读并同意</text>
				</view>
				<view class="links">
					<text class="link">《用户协议》</text>
					<text>和</text>
					<text class="link">《隐私政策》</text>
				</view>
			</view>
			
			<button 
				class="submit-btn" 
				:class="{loading: isLoading, disabled: !agreed}" 
				@tap="handleRegister"
			>
				<text v-if="!isLoading">注 册</text>
				<view v-else class="loading-icon"></view>
			</button>
			
			<view class="login-link" @tap="goToLogin">
				已有账号？<text>立即登录</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			form: {
				username: '',
				email: '',
				password: '',
				confirmPassword: ''
			},
			showPassword: false,
			isLoading: false,
			agreed: false
		}
	},
	methods: {
		togglePassword() {
			this.showPassword = !this.showPassword
		},
		toggleAgreement() {
			this.agreed = !this.agreed
		},
		async handleRegister() {
			if (this.isLoading || !this.agreed) return
			
			if (!this.form.username || !this.form.email || !this.form.password || !this.form.confirmPassword) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}
			
			if (this.form.password !== this.form.confirmPassword) {
				uni.showToast({
					title: '两次密码不一致',
					icon: 'none'
				})
				return
			}
			
			this.isLoading = true
			
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/auth/register',
					method: 'POST',
					data: {
						username: this.form.username,
						email: this.form.email,
						passwordHash: this.form.password
					}
				})
				
				if (res.data.code === 1) {
					uni.showToast({
						title: '注册成功',
						icon: 'success'
					})
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				} else {
					uni.showToast({
						title: res.data.msg,
						icon: 'none'
					})
				}
			} catch (e) {
				uni.showToast({
					title: '注册失败',
					icon: 'none'
				})
			} finally {
				this.isLoading = false
			}
		},
		goToLogin() {
			// 先尝试返回上一页
			try {
				const pages = getCurrentPages()
				if (pages.length > 1) {
					uni.navigateBack({
						delta: 1
					})
				} else {
					// 如果没有上一页，则重定向到登录页
					uni.redirectTo({
						url: '/pages/login/login'
					})
				}
			} catch (e) {
				// 发生错误时，确保用户能回到登录页
				uni.redirectTo({
					url: '/pages/login/login'
				})
			}
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background-color: #ffffff;
}

.header {
	position: relative;
	height: 35vh;
	overflow: hidden;
}

.animated-background {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	overflow: hidden;
	
	.gradient-layer {
		position: absolute;
		top: -50%;
		left: -50%;
		right: -50%;
		bottom: -50%;
		background: linear-gradient(
			45deg,
			#4776E6,
			#8E54E9,
			#4776E6
		);
		background-size: 200% 200%;
		animation: gradientMove 15s ease infinite;
	}
	
	.bubble-group {
		position: absolute;
		width: 100%;
		height: 100%;
		
		.bubble {
			position: absolute;
			background: rgba(255, 255, 255, 0.1);
			border-radius: 50%;
			backdrop-filter: blur(5px);
			animation: floatBubble 20s linear infinite;
			
			&:nth-child(1) {
				width: 120rpx;
				height: 120rpx;
				left: 10%;
				top: 20%;
				animation-delay: 0s;
			}
			
			&:nth-child(2) {
				width: 160rpx;
				height: 160rpx;
				left: 65%;
				top: 40%;
				animation-delay: -5s;
			}
			
			&:nth-child(3) {
				width: 90rpx;
				height: 90rpx;
				left: 30%;
				top: 60%;
				animation-delay: -10s;
			}
			
			&:nth-child(4) {
				width: 140rpx;
				height: 140rpx;
				left: 85%;
				top: 15%;
				animation-delay: -15s;
			}
			
			&:nth-child(5) {
				width: 110rpx;
				height: 110rpx;
				left: 50%;
				top: 75%;
				animation-delay: -8s;
			}
			
			&:nth-child(6) {
				width: 130rpx;
				height: 130rpx;
				left: 15%;
				top: 85%;
				animation-delay: -12s;
			}
		}
	}
}

.nav-back {
	position: fixed;
	top: var(--status-bar-height);
	left: 30rpx;
	z-index: 100;
	width: 64rpx;
	height: 64rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(255, 255, 255, 0.15);
	border-radius: 50%;
	backdrop-filter: blur(10px);
	border: 1px solid rgba(255, 255, 255, 0.2);
}

.register-icon {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -70%);
	width: 140rpx;
	height: 140rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background: rgba(255, 255, 255, 0.15);
	border-radius: 50%;
	backdrop-filter: blur(10px);
	box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
	border: 1px solid rgba(255, 255, 255, 0.2);
	animation: pulseAvatar 2s ease-in-out infinite;
}

.register-form {
	position: relative;
	margin-top: -50rpx;
	padding: 60rpx 40rpx;
	background: #fff;
	border-radius: 40rpx 40rpx 0 0;
	box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.05);
	
	.welcome {
		margin-bottom: 60rpx;
		
		.title {
			display: block;
			font-size: 48rpx;
			font-weight: 600;
			color: #333;
			margin-bottom: 16rpx;
		}
		
		.subtitle {
			font-size: 28rpx;
			color: #999;
		}
	}
}

.form-group {
	margin-bottom: 40rpx;
	
	.input-item {
		display: flex;
		align-items: center;
		height: 100rpx;
		padding: 0 30rpx;
		background: #f8f9fa;
		border-radius: 16rpx;
		margin-bottom: 30rpx;
		transition: all 0.3s;
		
		&:focus-within {
			background: #fff;
			box-shadow: 0 0 0 2px rgba(74,115,243,0.2);
		}
		
		input {
			flex: 1;
			font-size: 28rpx;
			color: #333;
			margin-left: 20rpx;
		}
		
		.placeholder {
			color: #bbb;
		}
	}
}

.agreement {
	margin-bottom: 40rpx;
	
	.checkbox-wrap {
		display: flex;
		align-items: center;
		margin-bottom: 10rpx;
		
		.checkbox {
			width: 36rpx;
			height: 36rpx;
			border: 2rpx solid #ddd;
			border-radius: 8rpx;
			margin-right: 16rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			transition: all 0.3s;
			
			&.active {
				background: #4a73f3;
				border-color: #4a73f3;
			}
		}
		
		.text {
			font-size: 26rpx;
			color: #666;
		}
	}
	
	.links {
		padding-left: 52rpx;
		font-size: 26rpx;
		color: #666;
		
		.link {
			color: #4a73f3;
		}
	}
}

.submit-btn {
	width: 100%;
	height: 100rpx;
	line-height: 100rpx;
	text-align: center;
	background: linear-gradient(135deg, #6e8efb, #4a73f3);
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
	}
	
	&.disabled {
		background: #ccc;
		pointer-events: none;
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

.login-link {
	text-align: center;
	font-size: 28rpx;
	color: #999;
	
	text {
		color: #4a73f3;
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