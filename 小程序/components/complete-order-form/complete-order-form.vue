<template>
	<view class="form-container">
		<view class="form-header">
			<text class="title">完成订单</text>
		</view>
		
		<view class="form-content">
			<!-- 分类选择 -->
			<view class="form-item">
				<text class="label">废品分类</text>
				<picker :value="form.categoryId" :range="categories" range-key="name" @change="onCategoryChange">
					<view class="picker">
						{{getCategoryName(form.categoryId)}}
					</view>
				</picker>
			</view>
			
			<!-- 重量输入 -->
			<view class="form-item">
				<text class="label">重量(kg)</text>
				<input type="digit" v-model="form.weight" placeholder="请输入重量" />
			</view>
			
			<!-- 单价输入 -->
			<view class="form-item">
				<text class="label">单价(元/kg)</text>
				<input type="digit" v-model="form.unitPrice" placeholder="请输入单价" />
			</view>
			
			<!-- 总金额展示 -->
			<view class="form-item">
				<text class="label">总金额</text>
				<text class="amount">¥ {{calculateAmount}}</text>
			</view>
		</view>
		
		<view class="form-footer">
			<button class="btn cancel" @tap="handleCancel">取消</button>
			<button class="btn confirm" @tap="handleConfirm" :loading="submitting">确认</button>
		</view>
	</view>
</template>

<script>
export default {
	props: {
		orderId: {
			type: [Number, String],
			required: true
		}
	},
	
	data() {
		return {
			form: {
				categoryId: 1,
				weight: '',
				unitPrice: ''
			},
			categories: [
				{ id: 1, name: '废纸' },
				{ id: 2, name: '废塑料' },
				{ id: 3, name: '废金属' },
				{ id: 4, name: '废玻璃' }
			],
			submitting: false
		}
	},
	
	computed: {
		calculateAmount() {
			const weight = parseFloat(this.form.weight) || 0
			const unitPrice = parseFloat(this.form.unitPrice) || 0
			return (weight * unitPrice).toFixed(2)
		}
	},
	
	methods: {
		getCategoryName(id) {
			const category = this.categories.find(item => item.id === id)
			return category ? category.name : '请选择分类'
		},
		
		onCategoryChange(e) {
			const index = e.detail.value
			this.form.categoryId = this.categories[index].id
		},
		
		handleCancel() {
			this.$emit('cancel')
		},
		
		async handleConfirm() {
			if (!this.form.weight || !this.form.unitPrice) {
				uni.showToast({
					title: '请填写完整信息',
					icon: 'none'
				})
				return
			}
			
			this.submitting = true
			try {
				const res = await uni.request({
					url: `http://localhost:8080/appointmentOrder/complete/${this.orderId}`,
					method: 'POST',
					header: {
						'Content-Type': 'application/json'
					},
					data: this.form
				})
				
				if (res.data.code === 1) {
					uni.showToast({
						title: '订单已完成',
						icon: 'success'
					})
					this.$emit('success')
				} else {
					uni.showToast({
						title: res.data.msg || '操作失败',
						icon: 'none'
					})
				}
			} catch (e) {
				console.error(e)
				uni.showToast({
					title: '网络错误',
					icon: 'none'
				})
			} finally {
				this.submitting = false
			}
		}
	}
}
</script>

<style lang="scss">
.form-container {
	background: #fff;
	border-radius: 12px;
	width: 600rpx;
	
	.form-header {
		padding: 20rpx;
		text-align: center;
		border-bottom: 1px solid #f0f0f0;
		
		.title {
			font-size: 32rpx;
			font-weight: 600;
			color: #333;
		}
	}
	
	.form-content {
		padding: 30rpx;
		
		.form-item {
			margin-bottom: 20rpx;
			
			.label {
				font-size: 28rpx;
				color: #666;
				margin-bottom: 10rpx;
				display: block;
			}
			
			input {
				height: 80rpx;
				border: 1px solid #ddd;
				border-radius: 8rpx;
				padding: 0 20rpx;
				font-size: 28rpx;
			}
			
			.picker {
				height: 80rpx;
				border: 1px solid #ddd;
				border-radius: 8rpx;
				padding: 0 20rpx;
				font-size: 28rpx;
				line-height: 80rpx;
			}
			
			.amount {
				font-size: 36rpx;
				color: #ff6b6b;
				font-weight: 600;
			}
		}
	}
	
	.form-footer {
		padding: 20rpx;
		display: flex;
		justify-content: space-around;
		border-top: 1px solid #f0f0f0;
		
		.btn {
			width: 200rpx;
			height: 80rpx;
			line-height: 80rpx;
			text-align: center;
			border-radius: 40rpx;
			font-size: 28rpx;
			
			&.cancel {
				background: #f5f5f5;
				color: #666;
			}
			
			&.confirm {
				background: #2979ff;
				color: #fff;
			}
		}
	}
}
</style> 