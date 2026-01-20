<template>
  <view class="gacha-container">
    <view class="machine">
      <view class="machine-top">
        <view class="glass-dome">
          <view class="capsule" v-for="(capsule, i) in capsuleItems" :key="i" :style="capsule.positionStyle">
            <view class="capsule-body" :style="capsule.colorStyle"></view>
          </view>
        </view>
      </view>

      <view class="machine-body">
        <view class="coin-slot">
          <text>🪙</text>
        </view>
        <view class="prize-window" v-if="result">
          <view class="won-capsule" :style="{ background: result.color }">
            <text class="prize-icon">{{ result.icon }}</text>
          </view>
        </view>
        <view class="exit-hole" v-else>
          <view class="hole-inner"></view>
        </view>
      </view>

      <view class="turn-handle" :class="{ 'turning': isTurning }" @click="turnHandle">
        <view class="handle-base"></view>
        <view class="handle-arm"></view>
        <view class="handle-knob"></view>
      </view>
    </view>

    <view class="action-area">
      <view class="gacha-btn" @click="turnHandle" :class="{ 'disabled': isTurning }">
        <text>{{ isTurning ? '扭蛋中...' : '投币扭蛋' }}</text>
      </view>
    </view>

    <view class="result-popup" v-if="showResult">
      <view class="popup-content">
        <text class="popup-title">🎉 恭喜获得</text>
        <view class="popup-prize">
          <text class="popup-icon">{{ result && result.icon ? result.icon : '🎁' }}</text>
          <text class="popup-name">{{ result && result.name ? result.name : '' }}</text>
        </view>
        <view class="popup-btn" @click="closeResult">
          <text>好的</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'GachaMachine',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: 'SSR大奖', icon: '🏆', probability: 0.02, color: '#FFD700' },
        { name: 'SR奖品', icon: '🎁', probability: 0.08, color: '#C0C0C0' },
        { name: 'R奖品', icon: '🎈', probability: 0.2, color: '#CD7F32' },
        { name: 'N奖品', icon: '⭐', probability: 0.3, color: '#87CEEB' },
        { name: '谢谢参与', icon: '💫', probability: 0.4, color: '#DDA0DD' }
      ]
    }
  },
  data() {
    return {
      isTurning: false,
      result: null,
      showResult: false,
      capsuleColors: [
        'linear-gradient(180deg, #FF6B6B, #EE5A5A)',
        'linear-gradient(180deg, #4ECDC4, #45B7AA)',
        'linear-gradient(180deg, #FFE66D, #FFD93D)',
        'linear-gradient(180deg, #95E1D3, #7AC7C4)',
        'linear-gradient(180deg, #F38181, #E84A5F)',
        'linear-gradient(180deg, #AA96DA, #9B89B3)'
      ]
    }
  },
  computed: {
    // 扭蛋胶囊样式数组
    capsuleItems() {
      const items = []
      for (let i = 1; i <= 12; i++) {
        const angle = (i - 1) * 30
        const radius = 80
        const x = Math.cos(angle * Math.PI / 180) * radius
        const y = Math.sin(angle * Math.PI / 180) * radius * 0.6 + 20
        items.push({
          positionStyle: {
            left: 'calc(50% + ' + x + 'rpx - 30rpx)',
            top: 'calc(50% + ' + y + 'rpx - 30rpx)'
          },
          colorStyle: {
            background: this.capsuleColors[(i - 1) % this.capsuleColors.length]
          }
        })
      }
      return items
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

    turnHandle() {
      if (this.isTurning) return

      this.isTurning = true
      this.result = null
      this.showResult = false

      // 模拟扭蛋过程
      setTimeout(() => {
        this.result = this.drawPrize()
        this.isTurning = false

        setTimeout(() => {
          this.showResult = true
          this.$emit('finish', this.result)
        }, 500)
      }, 1500)
    },

    closeResult() {
      this.showResult = false
    }
  }
}
</script>

<style scoped>
.gacha-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.machine {
  position: relative;
  width: 400rpx;
}

.machine-top {
  width: 100%;
  height: 300rpx;
  position: relative;
}

.glass-dome {
  width: 350rpx;
  height: 280rpx;
  margin: 0 auto;
  background: radial-gradient(ellipse at center, rgba(255,255,255,0.9) 0%, rgba(200,200,200,0.7) 100%);
  border-radius: 50% 50% 10% 10%;
  border: 8rpx solid #666;
  position: relative;
  overflow: hidden;
}

.capsule {
  position: absolute;
  width: 60rpx;
  height: 80rpx;
}

.capsule-body {
  width: 100%;
  height: 100%;
  border-radius: 30rpx 30rpx 30rpx 30rpx;
  box-shadow: inset -5rpx -5rpx 15rpx rgba(0,0,0,0.2);
}

.machine-body {
  width: 380rpx;
  height: 200rpx;
  margin: 0 auto;
  background: linear-gradient(180deg, #E74C3C, #C0392B);
  border-radius: 20rpx;
  position: relative;
  border: 6rpx solid #666;
}

.coin-slot {
  position: absolute;
  top: 30rpx;
  right: 30rpx;
  font-size: 40rpx;
}

.prize-window,
.exit-hole {
  position: absolute;
  bottom: 20rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 120rpx;
  height: 100rpx;
  background: #333;
  border-radius: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hole-inner {
  width: 80rpx;
  height: 60rpx;
  background: #111;
  border-radius: 50%;
}

.won-capsule {
  width: 80rpx;
  height: 100rpx;
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: dropIn 0.5s ease-out;
}

@keyframes dropIn {
  0% { transform: translateY(-100rpx); opacity: 0; }
  100% { transform: translateY(0); opacity: 1; }
}

.prize-icon {
  font-size: 50rpx;
}

.turn-handle {
  position: absolute;
  right: -60rpx;
  top: 320rpx;
  cursor: pointer;
}

.turn-handle.turning {
  animation: turn 0.5s ease-in-out 3;
}

@keyframes turn {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(180deg); }
}

.handle-base {
  width: 40rpx;
  height: 40rpx;
  background: #666;
  border-radius: 50%;
}

.handle-arm {
  width: 60rpx;
  height: 16rpx;
  background: linear-gradient(90deg, #888, #aaa);
  position: absolute;
  top: 12rpx;
  left: 30rpx;
  border-radius: 8rpx;
}

.handle-knob {
  width: 30rpx;
  height: 30rpx;
  background: radial-gradient(circle at 30% 30%, #ff6b6b, #c0392b);
  border-radius: 50%;
  position: absolute;
  top: 5rpx;
  left: 80rpx;
}

.action-area {
  margin-top: 50rpx;
}

.gacha-btn {
  padding: 24rpx 80rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 50rpx;
  box-shadow: 0 8rpx 20rpx rgba(102,126,234,0.4);
}

.gacha-btn.disabled {
  opacity: 0.6;
}

.gacha-btn text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.result-popup {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.popup-content {
  background: #fff;
  padding: 60rpx;
  border-radius: 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popIn 0.3s ease-out;
}

@keyframes popIn {
  0% { transform: scale(0.5); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.popup-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.popup-prize {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.popup-icon {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.popup-name {
  font-size: 36rpx;
  color: #667eea;
  font-weight: bold;
}

.popup-btn {
  padding: 20rpx 80rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 40rpx;
}

.popup-btn text {
  font-size: 30rpx;
  color: #fff;
}
</style>
