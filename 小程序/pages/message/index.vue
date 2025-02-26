<template>
	<view class="message-container">
		<!-- 顶部导航栏 -->
		<view class="nav-bar" :style="{ paddingTop: statusBarHeight + 'px' }">
			<view class="nav-back" @click="goBack">
				<uni-icons type="left" size="20" color="#333"></uni-icons>
			</view>
			<text class="nav-title">消息通知</text>
			<view class="nav-placeholder"></view>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="message-content" scroll-y refresher-enabled 
			:refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			
			<!-- 顶部提示 -->
			<view class="top-tip">
				<uni-icons type="notification-filled" size="18" color="#2979ff"></uni-icons>
				<text class="tip-text">这里显示系统发布的公告信息</text>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="announcements.length === 0 && !loading">
				<image src="/static/images/empty.png" mode="aspectFit" class="empty-image"></image>
				<text class="empty-text">暂无消息通知</text>
				<text class="empty-sub-text">有新公告时会在这里显示</text>
			</view>
			
			<!-- 加载状态 -->
			<view class="loading-state" v-if="loading && announcements.length === 0">
				<view class="loading-spinner">
					<uni-icons type="spinner-cycle" size="30" color="#2979ff"></uni-icons>
				</view>
				<text class="loading-text">加载中...</text>
			</view>
			
			<!-- 公告列表 -->
			<view class="announcement-list" v-if="announcements.length > 0">
				<view class="announcement-item" v-for="(item, index) in announcements" :key="item.id"
					@click="viewDetail(item)" hover-class="announcement-item-hover">
					<view class="announcement-icon">
						<uni-icons type="sound-filled" size="24" color="#2979ff"></uni-icons>
					</view>
					<view class="announcement-content">
						<view class="announcement-header">
							<text class="announcement-title">{{ item.title }}</text>
							<text class="announcement-date">{{ formatDate(item.publishDate) }}</text>
						</view>
						<view class="content-body">
							<text class="content-preview">{{ item.content }}</text>
						</view>
						<view class="announcement-footer">
							<text class="read-more">点击查看详情</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 底部安全区域 -->
			<view class="safe-area-bottom"></view>
		</scroll-view>
		
		<!-- 公告详情弹窗 -->
		<uni-popup ref="detailPopup" type="bottom" background-color="#fff">
			<view class="detail-popup">
				<view class="detail-header">
					<text class="detail-title">{{ currentAnnouncement.title }}</text>
					<view class="detail-close" @click="closeDetail">
						<uni-icons type="close" size="20" color="#999"></uni-icons>
					</view>
				</view>
				<scroll-view class="detail-content" scroll-y>
					<view class="detail-info">
						<text class="detail-date">发布时间：{{ formatDate(currentAnnouncement.publishDate) }}</text>
					</view>
					<view class="detail-body">
						<text class="detail-text">{{ currentAnnouncement.content }}</text>
					</view>
				</scroll-view>
				<view class="detail-footer">
					<view class="detail-btn" @click="closeDetail">关闭</view>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				statusBarHeight: 0, // 状态栏高度
				announcements: [], // 公告列表
				loading: false, // 加载状态
				refreshing: false, // 刷新状态
				currentAnnouncement: {}, // 当前查看的公告
				baseUrl: 'http://localhost:8080' // 后端接口基础URL，根据实际情况修改
			}
		},
		onLoad() {
			// 获取状态栏高度
			this.statusBarHeight = uni.getSystemInfoSync().statusBarHeight
			
			// 加载公告数据
			this.getAnnouncements()
		},
		methods: {
			// 返回上一页
			goBack() {
				uni.navigateBack()
			},
			
			// 获取公告列表
			getAnnouncements() {
				this.loading = true
				
				uni.request({
					url: this.baseUrl + '/announcement/published',
					method: 'GET',
					success: (res) => {
						if (res.data.code === 1) {
							this.announcements = res.data.data || []
						} else {
							uni.showToast({
								title: '获取公告失败',
								icon: 'none'
							})
						}
					},
					fail: (err) => {
						console.error('获取公告列表失败：', err)
						uni.showToast({
							title: '网络请求失败',
							icon: 'none'
						})
					},
					complete: () => {
						this.loading = false
						this.refreshing = false
					}
				})
			},
			
			// 下拉刷新
			onRefresh() {
				this.refreshing = true
				this.getAnnouncements()
			},
			
			// 查看公告详情
			viewDetail(announcement) {
				this.currentAnnouncement = announcement
				this.$refs.detailPopup.open('bottom')
			},
			
			// 关闭详情弹窗
			closeDetail() {
				this.$refs.detailPopup.close()
			},
			
			// 格式化日期
			formatDate(dateStr) {
				if (!dateStr) return '未知时间'
				
				const date = new Date(dateStr)
				const year = date.getFullYear()
				const month = (date.getMonth() + 1).toString().padStart(2, '0')
				const day = date.getDate().toString().padStart(2, '0')
				const hour = date.getHours().toString().padStart(2, '0')
				const minute = date.getMinutes().toString().padStart(2, '0')
				
				return `${year}-${month}-${day} ${hour}:${minute}`
			}
		}
	}
</script>

<style lang="scss">
.message-container {
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
.message-content {
	flex: 1;
	height: 0;
	padding: 30rpx;
}

// 顶部提示样式
.top-tip {
	display: flex;
	align-items: center;
	background-color: rgba(41, 121, 255, 0.1);
	padding: 20rpx 30rpx;
	border-radius: 12rpx;
	margin-bottom: 30rpx;
	
	.tip-text {
		font-size: 26rpx;
		color: #2979ff;
		margin-left: 16rpx;
	}
}

// 空状态样式
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 0;
	
	.empty-image {
		width: 240rpx;
		height: 240rpx;
		margin-bottom: 30rpx;
	}
	
	.empty-text {
		font-size: 30rpx;
		color: #666;
		margin-bottom: 16rpx;
		font-weight: 500;
	}
	
	.empty-sub-text {
		font-size: 26rpx;
		color: #999;
	}
}

// 加载状态样式
.loading-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 0;
	
	.loading-spinner {
		animation: rotate 1.5s linear infinite;
	}
	
	.loading-text {
		font-size: 28rpx;
		color: #999;
		margin-top: 20rpx;
	}
}

@keyframes rotate {
	from { transform: rotate(0deg); }
	to { transform: rotate(360deg); }
}

// 公告列表样式
.announcement-list {
	.announcement-item {
		background-color: #fff;
		border-radius: 16rpx;
		padding: 24rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 2rpx 16rpx rgba(0, 0, 0, 0.05);
		transition: all 0.2s ease;
		display: flex;
		
		.announcement-icon {
			margin-right: 20rpx;
			padding-top: 6rpx;
		}
		
		.announcement-content {
			flex: 1;
			
			.announcement-header {
				display: flex;
				justify-content: space-between;
				align-items: flex-start;
				margin-bottom: 16rpx;
				
				.announcement-title {
					font-size: 32rpx;
					font-weight: 500;
					color: #333;
					flex: 1;
				}
				
				.announcement-date {
					font-size: 24rpx;
					color: #999;
					margin-left: 20rpx;
				}
			}
			
			.content-body {
				margin-bottom: 16rpx;
				
				.content-preview {
					font-size: 28rpx;
					color: #666;
					line-height: 1.5;
					// 多行文本截断
					display: -webkit-box;
					-webkit-box-orient: vertical;
					-webkit-line-clamp: 2;
					overflow: hidden;
					text-overflow: ellipsis;
				}
			}
			
			.announcement-footer {
				.read-more {
					font-size: 24rpx;
					color: #2979ff;
				}
			}
		}
	}
	
	.announcement-item-hover {
		transform: translateY(-4rpx);
		box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
	}
}

// 详情弹窗样式
.detail-popup {
	background-color: #fff;
	border-radius: 24rpx 24rpx 0 0;
	padding-bottom: calc(env(safe-area-inset-bottom));
	max-height: 80vh;
	display: flex;
	flex-direction: column;
	
	.detail-header {
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
		display: flex;
		justify-content: space-between;
		align-items: center;
		
		.detail-title {
			font-size: 36rpx;
			font-weight: 600;
			color: #333;
			flex: 1;
			padding-right: 20rpx;
		}
		
		.detail-close {
			width: 60rpx;
			height: 60rpx;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}
	
	.detail-content {
		flex: 1;
		height: 0;
		padding: 0 30rpx;
		
		.detail-info {
			padding: 20rpx 0;
			border-bottom: 1rpx solid #f0f0f0;
			
			.detail-date {
				font-size: 26rpx;
				color: #999;
			}
		}
		
		.detail-body {
			padding: 30rpx 0;
			
			.detail-text {
				font-size: 30rpx;
				color: #333;
				line-height: 1.6;
			}
		}
	}
	
	.detail-footer {
		padding: 20rpx 30rpx 30rpx;
		display: flex;
		justify-content: center;
		
		.detail-btn {
			background-color: #2979ff;
			color: #fff;
			font-size: 30rpx;
			padding: 20rpx 0;
			border-radius: 12rpx;
			text-align: center;
			width: 100%;
		}
	}
}

// 底部安全区域
.safe-area-bottom {
	height: env(safe-area-inset-bottom);
}
</style> 