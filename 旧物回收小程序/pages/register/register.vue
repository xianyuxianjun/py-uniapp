<template>
	<view class="container">
		<!-- 顶部装饰 -->
		<view class="decoration">
			<view class="circle circle-1"></view>
			<view class="circle circle-2"></view>
		</view>
		
		<view class="form-box" ref="formBox">
			<view class="header">
				<text class="title">新用户注册</text>
				<text class="subtitle">填写信息，即刻开启环保之旅</text>
			</view>
			
			<view class="input-box">
				<text class="iconfont icon-email"></text>
				<input type="text" v-model="form.email" placeholder="请输入邮箱" />
			</view>
			<view class="input-box">
				<text class="iconfont icon-phone"></text>
				<input type="text" v-model="form.phone" placeholder="请输入手机号" />
			</view>
			<view class="input-box">
				<text class="iconfont icon-password"></text>
				<input type="password" v-model="form.passwordHash" placeholder="请输入密码" />
			</view>
			<view class="input-box">
				<text class="iconfont icon-password"></text>
				<input type="password" v-model="confirmPassword" placeholder="请确认密码" />
			</view>
			
			<button class="register-btn" @click="handleRegister" 
				:disabled="!form.email || !form.phone || !form.passwordHash || !confirmPassword">
				<text class="btn-text">注册</text>
			</button>
			
			<view class="options">
				<text class="login-link" @click="toLogin">
					已有账号？<text class="highlight">立即登录</text>
				</text>
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
				phone: '',
				passwordHash: ''
			},
			confirmPassword: ''
		}
	},
	methods: {
		async handleRegister() {
			// 表单验证
			if (!this.form.email || !this.form.phone || !this.form.passwordHash || !this.confirmPassword) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}
			
			if (this.form.passwordHash !== this.confirmPassword) {
				uni.showToast({
					title: '两次密码不一致',
					icon: 'none'
				})
				return
			}
			
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/auth/register',
					method: 'POST',
					data: this.form
				})
				
				if (res.data.code === 200) {
					uni.showToast({
						title: '注册成功',
						icon: 'success',
						duration: 1500
					})
					// 返回登录页
					setTimeout(() => {
						uni.navigateBack({
							delta: 1,
							animationType: 'slide-out-right',
							animationDuration: 300
						})
					}, 1500)
				} else {
					this.shakeForm()
					uni.showToast({
						title: res.data.msg,
						icon: 'none'
					})
				}
			} catch (e) {
				this.shakeForm()
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				})
			}
		},
		
		// 表单抖动动画
		shakeForm() {
			const formBox = this.$refs.formBox
			if (formBox) {
				formBox.classList.add('shake')
				setTimeout(() => {
					formBox.classList.remove('shake')
				}, 500)
			}
		},
		
		// 返回登录页面
		toLogin() {
			uni.navigateBack({
				delta: 1,
				animationType: 'slide-out-right',
				animationDuration: 300
			})
		}
	}
}
</script>

<style lang="scss">
.container {
	min-height: 100vh;
	background: linear-gradient(180deg, #ffffff 0%, #f8f8f8 100%);
	position: relative;
	overflow: hidden;
	
	.decoration {
		position: absolute;
		width: 100%;
		height: 400rpx;
		
		.circle {
			position: absolute;
			border-radius: 50%;
			
			&.circle-1 {
				width: 400rpx;
				height: 400rpx;
				background: linear-gradient(135deg, rgba(7, 193, 96, 0.2), rgba(7, 193, 96, 0.1));
				top: -200rpx;
				left: -100rpx;
			}
			
			&.circle-2 {
				width: 300rpx;
				height: 300rpx;
				background: linear-gradient(135deg, rgba(7, 193, 96, 0.15), rgba(7, 193, 96, 0.05));
				top: -50rpx;
				right: -150rpx;
			}
		}
	}
	
	.form-box {
		margin-top: 100rpx;
		padding: 0 60rpx;
		
		.header {
			margin-bottom: 60rpx;
			
			.title {
				font-size: 48rpx;
				font-weight: bold;
				color: #333;
				display: block;
			}
			
			.subtitle {
				font-size: 28rpx;
				color: #999;
				margin-top: 16rpx;
				display: block;
			}
		}
		
		.input-box {
			display: flex;
			align-items: center;
			height: 100rpx;
			background: #fff;
			border-radius: 50rpx;
			padding: 0 40rpx;
			margin-bottom: 30rpx;
			box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.03);
			
			.iconfont {
				font-size: 40rpx;
				color: #07c160;
				margin-right: 20rpx;
			}
			
			input {
				flex: 1;
				font-size: 28rpx;
				color: #333;
			}
		}
		
		.register-btn {
			height: 100rpx;
			background: linear-gradient(135deg, #07c160, #05a050);
			color: #fff;
			border-radius: 50rpx;
			margin-top: 60rpx;
			font-size: 32rpx;
			box-shadow: 0 10rpx 30rpx rgba(7, 193, 96, 0.3);
			display: flex;
			align-items: center;
			justify-content: center;
			
			&:active {
				transform: scale(0.98);
			}
			
			&[disabled] {
				background: #ccc;
				box-shadow: none;
			}
			
			.btn-text {
				font-weight: 500;
			}
		}
		
		.options {
			margin-top: 40rpx;
			text-align: center;
			
			.login-link {
				font-size: 28rpx;
				color: #666;
				
				.highlight {
					color: #07c160;
					font-weight: 500;
				}
			}
		}
	}
}

// 修改抖动动画
@keyframes shake {
	0%, 100% { transform: translateX(0); }
	10%, 30%, 50%, 70%, 90% { transform: translateX(-10rpx); }
	20%, 40%, 60%, 80% { transform: translateX(10rpx); }
}

.shake {
	animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}

// 输入框获取焦点时的动画
.input-box {
	transition: all 0.3s ease;
	
	&:focus-within {
		transform: scale(1.02);
		box-shadow: 0 15rpx 40rpx rgba(0,0,0,0.05);
	}
}

// 按钮点击动画
.register-btn {
	transition: all 0.2s ease;
	
	&:active:not([disabled]) {
		transform: scale(0.95);
		opacity: 0.8;
	}
}
</style> 