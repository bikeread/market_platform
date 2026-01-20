<template>
  <view class="steering-container">
    <view class="dashboard">
      <view class="speed-display">
        <text class="speed-value">{{ currentSpeed }}</text>
        <text class="speed-unit">km/h</text>
      </view>
    </view>

    <view class="wheel-wrapper">
      <view
        class="steering-wheel"
        :style="{ transform: 'rotate(' + rotateAngle + 'deg)', transition: wheelTransition }"
      >
        <view class="wheel-outer">
          <view class="wheel-spoke" v-for="i in 3" :key="i" :style="{ transform: 'rotate(' + (i * 120) + 'deg)' }"></view>
          <view class="wheel-center">
            <text class="brand-logo">🚗</text>
          </view>
        </view>
      </view>
    </view>

    <view class="prize-display" v-if="result">
      <text class="prize-icon">{{ result.icon }}</text>
      <text class="prize-name">{{ result.name }}</text>
    </view>

    <view class="spin-btn" @click="spinWheel" :class="{ 'disabled': isSpinning }">
      <text>{{ isSpinning ? '转动中...' : '转动方向盘' }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'SteeringWheel',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '购车礼包', icon: '🚗', probability: 0.05 },
        { name: '保养套餐', icon: '🔧', probability: 0.1 },
        { name: '加油卡', icon: '⛽', probability: 0.15 },
        { name: '车载用品', icon: '🎁', probability: 0.2 },
        { name: '优惠券', icon: '🎫', probability: 0.25 },
        { name: '谢谢参与', icon: '💫', probability: 0.25 }
      ]
    }
  },
  data() {
    return {
      rotateAngle: 0,
      isSpinning: false,
      wheelTransition: 'none',
      result: null,
      currentSpeed: 0,
      speedTimer: null
    }
  },
  methods: {
    drawPrize() {
      const random = Math.random()
      let sum = 0
      for (const prize of this.prizes) {
        sum += prize.probability
        if (random < sum) return prize
      }
      return this.prizes[this.prizes.length - 1]
    },

    spinWheel() {
      if (this.isSpinning) return

      this.isSpinning = true
      this.result = null
      this.currentSpeed = 0

      // 加速动画
      let speed = 0
      this.speedTimer = setInterval(() => {
        speed += 5
        if (speed > 180) speed = 180
        this.currentSpeed = speed
      }, 50)

      // 设置转动
      const baseRounds = 5
      const extraAngle = Math.random() * 360
      const finalAngle = this.rotateAngle + 360 * baseRounds + extraAngle

      this.wheelTransition = 'transform 4s cubic-bezier(0.17, 0.67, 0.12, 0.99)'

      setTimeout(() => {
        this.rotateAngle = finalAngle
      }, 50)

      // 减速动画
      setTimeout(() => {
        clearInterval(this.speedTimer)
        const decelerate = setInterval(() => {
          this.currentSpeed -= 10
          if (this.currentSpeed <= 0) {
            this.currentSpeed = 0
            clearInterval(decelerate)
          }
        }, 100)
      }, 2000)

      // 结束
      setTimeout(() => {
        this.isSpinning = false
        this.wheelTransition = 'none'
        this.result = this.drawPrize()

        this.$emit('finish', this.result)

        uni.showModal({
          title: '🚗 恭喜',
          content: `您获得了：${this.result.icon} ${this.result.name}`,
          showCancel: false
        })
      }, 4100)
    }
  }
}
</script>

<style scoped>
.steering-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.dashboard {
  width: 200rpx;
  height: 100rpx;
  background: linear-gradient(180deg, #1a1a2e, #16213e);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40rpx;
  border: 4rpx solid #333;
}

.speed-display {
  display: flex;
  align-items: baseline;
}

.speed-value {
  font-size: 56rpx;
  font-weight: bold;
  color: #00ff88;
  font-family: monospace;
}

.speed-unit {
  font-size: 20rpx;
  color: #00ff88;
  margin-left: 8rpx;
}

.wheel-wrapper {
  width: 500rpx;
  height: 500rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.steering-wheel {
  width: 400rpx;
  height: 400rpx;
  position: relative;
}

.wheel-outer {
  width: 100%;
  height: 100%;
  border: 40rpx solid #2c2c2c;
  border-radius: 50%;
  position: relative;
  background: linear-gradient(135deg, #3a3a3a, #1a1a1a);
  box-shadow:
    0 10rpx 30rpx rgba(0,0,0,0.5),
    inset 0 5rpx 15rpx rgba(255,255,255,0.1);
}

.wheel-spoke {
  position: absolute;
  width: 12rpx;
  height: 140rpx;
  background: linear-gradient(180deg, #444, #222);
  left: calc(50% - 6rpx);
  top: calc(50% - 70rpx);
  transform-origin: center center;
  border-radius: 6rpx;
}

.wheel-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #444, #222);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 4rpx solid #555;
}

.brand-logo {
  font-size: 50rpx;
}

.prize-display {
  margin-top: 40rpx;
  padding: 30rpx 60rpx;
  background: linear-gradient(135deg, rgba(255,215,0,0.2), rgba(255,165,0,0.2));
  border-radius: 20rpx;
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

.prize-icon {
  font-size: 80rpx;
  margin-bottom: 10rpx;
}

.prize-name {
  font-size: 32rpx;
  color: #FFD700;
  font-weight: bold;
}

.spin-btn {
  margin-top: 50rpx;
  padding: 30rpx 100rpx;
  background: linear-gradient(135deg, #3a7bd5, #3a6073);
  border-radius: 50rpx;
  box-shadow: 0 10rpx 30rpx rgba(58,123,213,0.4);
}

.spin-btn.disabled {
  opacity: 0.6;
}

.spin-btn text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}
</style>
