<template>
	<view class="container">
		<!-- 顶部装饰 -->
		<view class="decoration">
			<view class="circle circle-1"></view>
			<view class="circle circle-2"></view>
		</view>
		
		<!-- 顶部logo -->
		<view class="logo-box">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="title">旧物回收</text>
			<text class="subtitle">让闲置物品重获新生</text>
		</view>
		
		<!-- 登录表单 -->
		<view class="form-box" ref="formBox">
			<view class="input-box">
				<text class="iconfont icon-email"></text>
				<input type="text" v-model="form.email" placeholder="请输入邮箱" />
			</view>
			<view class="input-box">
				<text class="iconfont icon-password"></text>
				<input type="password" v-model="form.passwordHash" placeholder="请输入密码" />
				<text class="forget-pwd">忘记密码？</text>
			</view>
			
			<button class="login-btn" @click="handleLogin" :disabled="!form.email || !form.passwordHash">
				<text class="btn-text">登录</text>
			</button>
			
			<view class="divider">
				<text class="line"></text>
				<text class="text">或</text>
				<text class="line"></text>
			</view>
			
			<view class="options">
				<text class="register-link" @click="toRegister">
					还没有账号？<text class="highlight">立即注册</text>
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
				passwordHash: ''
			}
		}
	},
	methods: {
		async handleLogin() {
			if (!this.form.email || !this.form.passwordHash) {
				this.shakeForm()
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}
			
			try {
				const res = await uni.request({
					url: 'http://localhost:8080/auth/login',
					method: 'POST',
					data: this.form
				})
				
				if (res.data.code === 200) {
					// 存储用户信息
					uni.setStorageSync('userInfo', res.data.data)
					uni.showToast({
						title: '登录成功',
						icon: 'success',
						duration: 1500
					})
					// 使用渐变动画跳转到首页
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index/index',
							animationType: 'slide-in-right',
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
		
		// 跳转到注册页面
		toRegister() {
			uni.navigateTo({
				url: '/pages/register/register',
				animationType: 'slide-in-left',
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
	
	.logo-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 160rpx;
		
		.logo {
			width: 160rpx;
			height: 160rpx;
			border-radius: 40rpx;
			box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.05);
		}
		
		.title {
			font-size: 48rpx;
			font-weight: bold;
			color: #333;
			margin-top: 30rpx;
		}
		
		.subtitle {
			font-size: 28rpx;
			color: #999;
			margin-top: 16rpx;
		}
	}
	
	.form-box {
		margin-top: 80rpx;
		padding: 0 60rpx;
		
		.input-box {
			display: flex;
			align-items: center;
			height: 100rpx;
			background: #fff;
			border-radius: 50rpx;
			padding: 0 40rpx;
			margin-bottom: 30rpx;
			box-shadow: 0 10rpx 30rpx rgba(0,0,0,0.03);
			position: relative;
			
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
			
			.forget-pwd {
				font-size: 24rpx;
				color: #07c160;
				position: absolute;
				right: 40rpx;
			}
		}
		
		.login-btn {
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
		
		.divider {
			display: flex;
			align-items: center;
			justify-content: center;
			margin: 60rpx 0;
			
			.line {
				width: 100rpx;
				height: 1px;
				background: #eee;
			}
			
			.text {
				color: #999;
				font-size: 24rpx;
				margin: 0 20rpx;
			}
		}
		
		.options {
			text-align: center;
			
			.register-link {
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

/* 修改抖动动画 */
@keyframes shake {
	0%, 100% { transform: translateX(0); }
	10%, 30%, 50%, 70%, 90% { transform: translateX(-10rpx); }
	20%, 40%, 60%, 80% { transform: translateX(10rpx); }
}

.shake {
	animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}

/* 输入框获取焦点时的动画 */
.input-box {
	transition: all 0.3s ease;
	
	&:focus-within {
		transform: scale(1.02);
		box-shadow: 0 15rpx 40rpx rgba(0,0,0,0.05);
	}
}

/* 按钮点击动画 */
.login-btn {
	transition: all 0.2s ease;
	
	&:active:not([disabled]) {
		transform: scale(0.95);
		opacity: 0.8;
	}
}

// 添加淡入动画
.fade-enter-active, .fade-leave-active {
	transition: opacity 0.3s ease;
}
.fade-enter, .fade-leave-to {
	opacity: 0;
}
</style> 