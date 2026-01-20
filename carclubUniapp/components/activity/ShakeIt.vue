<template>
  <view class="shake-container">
    <view class="phone-icon" :class="{ 'shaking': isShaking }">
      <text>📱</text>
    </view>

    <view class="shake-info">
      <text class="shake-count">已摇 {{ shakeCount }} 次</text>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
      </view>
      <text class="shake-tip">{{ tipText }}</text>
    </view>

    <view class="result-box" v-if="result">
      <text class="result-icon">{{ result.icon }}</text>
      <text class="result-name">{{ result.name }}</text>
    </view>

    <!-- 模拟摇一摇按钮（小程序中使用加速计） -->
    <view class="shake-btn" @click="simulateShake" v-if="!result">
      <text>{{ isShaking ? '摇动中...' : '点击摇一摇' }}</text>
    </view>

    <view class="shake-btn reset" @click="reset" v-if="result">
      <text>再摇一次</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ShakeIt',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '一等奖', icon: '🏆', probability: 0.05 },
        { name: '二等奖', icon: '🎁', probability: 0.1 },
        { name: '三等奖', icon: '🎈', probability: 0.2 },
        { name: '优惠券', icon: '🎫', probability: 0.3 },
        { name: '谢谢参与', icon: '💫', probability: 0.35 }
      ]
    },
    requiredShakes: {
      type: Number,
      default: 10
    }
  },
  data() {
    return {
      shakeCount: 0,
      isShaking: false,
      result: null,
      lastShakeTime: 0
    }
  },
  computed: {
    progressPercent() {
      return Math.min(100, (this.shakeCount / this.requiredShakes) * 100)
    },
    tipText() {
      if (this.shakeCount < this.requiredShakes) {
        return `再摇 ${this.requiredShakes - this.shakeCount} 次即可抽奖`
      }
      return '可以抽奖了！'
    }
  },
  mounted() {
    this.startAccelerometer()
  },
  beforeDestroy() {
    this.stopAccelerometer()
  },
  methods: {
    startAccelerometer() {
      uni.onAccelerometerChange((res) => {
        const now = Date.now()
        // 检测摇动
        if (Math.abs(res.x) > 1.5 || Math.abs(res.y) > 1.5 || Math.abs(res.z) > 1.5) {
          if (now - this.lastShakeTime > 300) {
            this.handleShake()
            this.lastShakeTime = now
          }
        }
      })

      uni.startAccelerometer({
        interval: 'normal'
      })
    },

    stopAccelerometer() {
      uni.stopAccelerometer()
    },

    simulateShake() {
      if (this.isShaking || this.result) return
      this.handleShake()
    },

    handleShake() {
      if (this.result) return

      this.isShaking = true
      this.shakeCount++

      setTimeout(() => {
        this.isShaking = false
      }, 300)

      if (this.shakeCount >= this.requiredShakes) {
        this.drawPrize()
      }
    },

    drawPrize() {
      const random = Math.random()
      let sum = 0
      for (const prize of this.prizes) {
        sum += prize.probability
        if (random < sum) {
          this.result = prize
          break
        }
      }
      if (!this.result) {
        this.result = this.prizes[this.prizes.length - 1]
      }

      this.$emit('finish', this.result)

      setTimeout(() => {
        uni.showModal({
          title: '🎊 摇出好运',
          content: `您获得了：${this.result.icon} ${this.result.name}`,
          showCancel: false
        })
      }, 500)
    },

    reset() {
      this.shakeCount = 0
      this.result = null
    }
  }
}
</script>

<style scoped>
.shake-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 40rpx;
}

.phone-icon {
  font-size: 200rpx;
  margin-bottom: 40rpx;
  transition: transform 0.1s;
}

.phone-icon.shaking {
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: rotate(0deg); }
  20% { transform: rotate(-15deg); }
  40% { transform: rotate(15deg); }
  60% { transform: rotate(-10deg); }
  80% { transform: rotate(10deg); }
}

.shake-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 500rpx;
}

.shake-count {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.progress-bar {
  width: 100%;
  height: 20rpx;
  background: rgba(255,255,255,0.2);
  border-radius: 10rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #00c6ff, #0072ff);
  border-radius: 10rpx;
  transition: width 0.3s;
}

.shake-tip {
  font-size: 26rpx;
  color: rgba(255,255,255,0.7);
}

.result-box {
  margin-top: 50rpx;
  padding: 40rpx 80rpx;
  background: linear-gradient(135deg, rgba(255,215,0,0.2), rgba(255,165,0,0.2));
  border-radius: 30rpx;
  border: 4rpx solid #FFD700;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popIn 0.5s ease-out;
}

@keyframes popIn {
  0% { transform: scale(0); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.result-icon {
  font-size: 100rpx;
  margin-bottom: 20rpx;
}

.result-name {
  font-size: 36rpx;
  color: #FFD700;
  font-weight: bold;
}

.shake-btn {
  margin-top: 50rpx;
  padding: 30rpx 100rpx;
  background: linear-gradient(135deg, #00c6ff, #0072ff);
  border-radius: 50rpx;
  box-shadow: 0 10rpx 30rpx rgba(0,114,255,0.4);
}

.shake-btn.reset {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 10rpx 30rpx rgba(102,126,234,0.4);
}

.shake-btn text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.shake-btn:active {
  transform: scale(0.98);
  opacity: 0.9;
}
</style>
