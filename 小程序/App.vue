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
		background-color: #f5f5f5;
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
</style>
