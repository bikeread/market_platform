<template>
  <view class="gas-station-container">
    <view class="fuel-display">
      <view class="fuel-gauge">
        <view class="gauge-fill" :style="{ height: fuelPercent + '%' }"></view>
        <view class="gauge-marks">
          <text v-for="i in 5" :key="i">{{ (5 - i) * 25 }}%</text>
        </view>
      </view>
      <view class="fuel-info">
        <text class="fuel-liters">{{ fuelAmount.toFixed(1) }}L</text>
        <text class="fuel-label">已加油量</text>
      </view>
    </view>

    <view class="pump-area">
      <view class="fuel-pump">
        <view class="pump-display">
          <text class="pump-price">¥{{ (fuelAmount * 7.5).toFixed(2) }}</text>
        </view>
        <view class="pump-body">
          <view class="pump-nozzle" :class="{ 'pumping': isPumping }">⛽</view>
        </view>
      </view>
    </view>

    <view class="pump-btn-area">
      <view
        class="pump-btn"
        @touchstart="startPump"
        @touchend="stopPump"
        @click="tapPump"
        :class="{ 'active': isPumping, 'finished': isFinished }"
      >
        <text v-if="!isFinished">{{ isPumping ? '加油中...' : '按住加油' }}</text>
        <text v-else>查看奖励</text>
      </view>
    </view>

    <view class="target-info" v-if="!isFinished">
      <text>目标：加满 {{ targetAmount }}L 获得奖励</text>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: (fuelAmount / targetAmount * 100) + '%' }"></view>
      </view>
    </view>

    <view class="result-box" v-if="showResult">
      <view class="result-content">
        <text class="result-title">🎊 加油完成</text>
        <text class="result-amount">共加油 {{ fuelAmount.toFixed(1) }}L</text>
        <view class="result-prize">
          <text class="prize-icon">{{ prize && prize.icon ? prize.icon : '🎁' }}</text>
          <text class="prize-name">{{ prize && prize.name ? prize.name : '' }}</text>
        </view>
        <view class="result-btn" @click="resetGame">
          <text>再来一次</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'GasStation',
  props: {
    targetAmount: {
      type: Number,
      default: 50
    },
    prizes: {
      type: Array,
      default: () => [
        { name: '500元加油卡', icon: '⛽', minFuel: 50 },
        { name: '200元加油卡', icon: '🎫', minFuel: 40 },
        { name: '100元加油卡', icon: '🎁', minFuel: 30 },
        { name: '50元代金券', icon: '💳', minFuel: 20 },
        { name: '谢谢参与', icon: '💫', minFuel: 0 }
      ]
    }
  },
  data() {
    return {
      fuelAmount: 0,
      isPumping: false,
      isFinished: false,
      showResult: false,
      prize: null,
      pumpTimer: null
    }
  },
  computed: {
    fuelPercent() {
      return Math.min(100, (this.fuelAmount / this.targetAmount) * 100)
    }
  },
  methods: {
    startPump() {
      if (this.isFinished) return
      this.isPumping = true

      this.pumpTimer = setInterval(() => {
        this.fuelAmount += 0.5
        if (this.fuelAmount >= this.targetAmount) {
          this.finishPump()
        }
      }, 100)
    },

    stopPump() {
      if (this.isFinished) return
      this.isPumping = false
      clearInterval(this.pumpTimer)

      // 判断是否可以结束
      if (this.fuelAmount >= 10) {
        this.finishPump()
      }
    },

    tapPump() {
      if (this.isFinished) {
        this.showResult = true
        return
      }

      // 单次点击也可以加油
      if (!this.isPumping) {
        this.fuelAmount += 2
        if (this.fuelAmount >= this.targetAmount) {
          this.finishPump()
        }
      }
    },

    finishPump() {
      clearInterval(this.pumpTimer)
      this.isPumping = false
      this.isFinished = true

      // 根据加油量确定奖品
      for (const prize of this.prizes) {
        if (this.fuelAmount >= prize.minFuel) {
          this.prize = prize
          break
        }
      }

      this.$emit('finish', this.prize)

      setTimeout(() => {
        this.showResult = true
      }, 500)
    },

    resetGame() {
      this.fuelAmount = 0
      this.isFinished = false
      this.showResult = false
      this.prize = null
    }
  }
}
</script>

<style scoped>
.gas-station-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.fuel-display {
  display: flex;
  align-items: center;
  gap: 40rpx;
  margin-bottom: 40rpx;
}

.fuel-gauge {
  width: 80rpx;
  height: 300rpx;
  background: #222;
  border-radius: 10rpx;
  position: relative;
  overflow: hidden;
  border: 4rpx solid #444;
}

.gauge-fill {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(180deg, #4CAF50, #8BC34A);
  transition: height 0.1s;
}

.gauge-marks {
  position: absolute;
  top: 0;
  right: -60rpx;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10rpx 0;
}

.gauge-marks text {
  font-size: 20rpx;
  color: rgba(255,255,255,0.6);
}

.fuel-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.fuel-liters {
  font-size: 72rpx;
  font-weight: bold;
  color: #4CAF50;
  font-family: monospace;
}

.fuel-label {
  font-size: 24rpx;
  color: rgba(255,255,255,0.6);
}

.pump-area {
  margin-bottom: 40rpx;
}

.fuel-pump {
  width: 200rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.pump-display {
  width: 180rpx;
  padding: 20rpx;
  background: #111;
  border-radius: 10rpx;
  margin-bottom: 20rpx;
}

.pump-price {
  font-size: 36rpx;
  color: #00ff00;
  font-family: monospace;
}

.pump-body {
  width: 120rpx;
  height: 160rpx;
  background: linear-gradient(180deg, #f7971e, #ffd200);
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pump-nozzle {
  font-size: 80rpx;
  transition: transform 0.2s;
}

.pump-nozzle.pumping {
  animation: pump 0.3s ease-in-out infinite;
}

@keyframes pump {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(-10deg); }
}

.pump-btn-area {
  margin-bottom: 30rpx;
}

.pump-btn {
  padding: 30rpx 100rpx;
  background: linear-gradient(135deg, #f7971e, #ffd200);
  border-radius: 50rpx;
  box-shadow: 0 10rpx 30rpx rgba(247,151,30,0.4);
}

.pump-btn.active {
  transform: scale(0.98);
  opacity: 0.9;
}

.pump-btn.finished {
  background: linear-gradient(135deg, #4CAF50, #8BC34A);
}

.pump-btn text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.target-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 500rpx;
}

.target-info text {
  font-size: 26rpx;
  color: rgba(255,255,255,0.7);
  margin-bottom: 16rpx;
}

.progress-bar {
  width: 100%;
  height: 16rpx;
  background: rgba(255,255,255,0.2);
  border-radius: 8rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #f7971e, #ffd200);
  transition: width 0.2s;
}

.result-box {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.result-content {
  background: #fff;
  padding: 60rpx;
  border-radius: 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.result-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.result-amount {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 30rpx;
}

.result-prize {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx;
  background: linear-gradient(135deg, #fff5e6, #ffe0b2);
  border-radius: 20rpx;
  margin-bottom: 30rpx;
}

.prize-icon {
  font-size: 80rpx;
  margin-bottom: 10rpx;
}

.prize-name {
  font-size: 32rpx;
  color: #f7971e;
  font-weight: bold;
}

.result-btn {
  padding: 20rpx 80rpx;
  background: linear-gradient(135deg, #f7971e, #ffd200);
  border-radius: 40rpx;
}

.result-btn text {
  font-size: 30rpx;
  color: #fff;
  font-weight: bold;
}
</style>
