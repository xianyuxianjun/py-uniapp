<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
	/* 引入阿里巴巴图标库 */
	@font-face {
		font-family: 'iconfont';
		src: url('//at.alicdn.com/t/font_3xxx_xxxxxxxxxx.eot');
		src: url('//at.alicdn.com/t/font_3xxx_xxxxxxxxxx.eot?#iefix') format('embedded-opentype'),
			url('//at.alicdn.com/t/font_3xxx_xxxxxxxxxx.woff2') format('woff2'),
			url('//at.alicdn.com/t/font_3xxx_xxxxxxxxxx.woff') format('woff'),
			url('//at.alicdn.com/t/font_3xxx_xxxxxxxxxx.ttf') format('truetype'),
			url('//at.alicdn.com/t/font_3xxx_xxxxxxxxxx.svg#iconfont') format('svg');
	}

	.iconfont {
		font-family: "iconfont" !important;
		font-style: normal;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
	}

	/* 全局样式 */
	page {
		/* 更新全局背景色为更柔和的色调 */
		background-color: #f8f9fc;
		/* 添加默认字体 */
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
		/* 默认文字颜色 */
		color: #333;
	}
	
	/* 全局卡片样式 */
	.card {
		background-color: #ffffff;
		border-radius: 16rpx;
		box-shadow: 0 6rpx 16rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
		margin-bottom: 20rpx;
		transition: all 0.3s ease;
	}
	
	/* 全局按钮样式 */
	button {
		font-weight: 500;
		border-radius: 44rpx;
		line-height: 88rpx;
		height: 88rpx;
		font-size: 32rpx;
		transition: all 0.2s ease;
	}
	
	button.primary {
		background: linear-gradient(135deg, #3B88FF, #1966FF);
		color: #fff;
		box-shadow: 0 6rpx 16rpx rgba(59, 136, 255, 0.25);
	}
	
	button.outline {
		border: 1px solid #3B88FF;
		color: #3B88FF;
		background: transparent;
	}
	
	/* 输入框统一样式 */
	input, textarea {
		background-color: #F5F7FA;
		border-radius: 8rpx;
		padding: 20rpx 24rpx;
		font-size: 28rpx;
		color: #333;
		transition: all 0.2s ease;
	}
	
	input:focus, textarea:focus {
		background-color: #ECF2FF;
		box-shadow: 0 0 0 2rpx rgba(59, 136, 255, 0.2);
	}
	
	/* 标题文本样式 */
	.title-text {
		font-size: 36rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 24rpx;
	}
	
	/* 副标题文本样式 */
	.subtitle-text {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 16rpx;
	}
	
	/* 导航栏统一样式 */
	.custom-nav-bar {
		height: 88rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		position: relative;
		padding: 0 30rpx;
		background: #fff;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.custom-nav-bar .nav-title {
		font-size: 34rpx;
		font-weight: 500;
		color: #333;
	}
	
	.custom-nav-bar .nav-back {
		position: absolute;
		left: 30rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		width: 60rpx;
		height: 60rpx;
	}

	/* 更新图标字体的Unicode编码 */
	.icon-email:before {
		content: "\e66f";
	}
	.icon-password:before {
		content: "\e67b";
	}
	.icon-eye:before {
		content: "\e668";
	}
	.icon-eye-close:before {
		content: "\e669";
	}
	.icon-user:before {
		content: "\e67a";
	}
	.icon-back:before {
		content: "\e679";
	}
	
	/* 渐变背景效果 */
	.gradient-bg {
		background: linear-gradient(135deg, #3B88FF, #5E81FF);
	}
	
	/* 空状态样式 */
	.empty-state {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 80rpx 0;
	}
	
	.empty-state image {
		width: 240rpx;
		height: 240rpx;
		margin-bottom: 30rpx;
	}
	
	.empty-state text {
		font-size: 28rpx;
		color: #999;
		margin-bottom: 10rpx;
	}
	
	/* 统一表单样式 */
	.form-item {
		margin-bottom: 30rpx;
	}
	
	.form-item .label {
		font-size: 28rpx;
		color: #666;
		margin-bottom: 16rpx;
		display: block;
	}
	
	/* 加载状态样式 */
	.loading-spinner {
		width: 60rpx;
		height: 60rpx;
		border: 4rpx solid #f3f3f3;
		border-top: 4rpx solid #3B88FF;
		border-radius: 50%;
		animation: spin 1s linear infinite;
	}
	
	@keyframes spin {
		0% { transform: rotate(0deg); }
		100% { transform: rotate(360deg); }
	}
	
	/* 安全区域 */
	.safe-area-bottom {
		height: calc(env(safe-area-inset-bottom));
		width: 100%;
	}
</style>
