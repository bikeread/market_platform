<template>
  <view class="lucky-key-container">
    <view class="treasure-box" :class="{ 'opened': isOpened }">
      <view class="box-lid" v-if="!isOpened"></view>
      <view class="box-body">
        <view class="prize-inside" v-if="isOpened">
          <text class="prize-icon">{{ result && result.icon ? result.icon : '🎁' }}</text>
          <text class="prize-name">{{ result && result.name ? result.name : '' }}</text>
        </view>
      </view>
      <view class="box-glow" v-if="isOpened"></view>
    </view>

    <view class="keys-section" v-if="!isOpened">
      <text class="keys-title">选择一把钥匙开启宝箱</text>
      <view class="keys-row">
        <view
          class="key-item"
          v-for="(key, index) in keys"
          :key="index"
          :class="{ 'selected': selectedKey === index, 'shake': selectedKey === index && isAnimating }"
          @click="selectKey(index)"
        >
          <text class="key-icon">🔑</text>
          <text class="key-label">{{ key.label }}</text>
        </view>
      </view>
    </view>

    <view class="action-btn" v-if="selectedKey !== null && !isOpened" @click="openBox">
      <text>开启宝箱</text>
    </view>

    <view class="action-btn reset" v-if="isOpened" @click="reset">
      <text>再试一次</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'LuckyKey',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '购车大礼包', icon: '🚗', probability: 0.05 },
        { name: '保养套餐', icon: '🔧', probability: 0.1 },
        { name: '加油卡500元', icon: '⛽', probability: 0.1 },
        { name: '精品配件', icon: '🎁', probability: 0.2 },
        { name: '代金券100元', icon: '🎫', probability: 0.25 },
        { name: '谢谢参与', icon: '💫', probability: 0.3 }
      ]
    }
  },
  data() {
    return {
      keys: [
        { label: '金钥匙', color: '#FFD700' },
        { label: '银钥匙', color: '#C0C0C0' },
        { label: '铜钥匙', color: '#CD7F32' }
      ],
      selectedKey: null,
      isOpened: false,
      isAnimating: false,
      result: null
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

    selectKey(index) {
      if (this.isAnimating || this.isOpened) return
      this.selectedKey = index
    },

    openBox() {
      if (this.isAnimating || this.selectedKey === null) return

      this.isAnimating = true

      setTimeout(() => {
        this.isAnimating = false
        this.isOpened = true
        this.result = this.drawPrize()

        this.$emit('finish', this.result)

        setTimeout(() => {
          uni.showModal({
            title: '🔑 宝箱已开启',
            content: `您获得了：${this.result.icon} ${this.result.name}`,
            showCancel: false
          })
        }, 500)
      }, 1000)
    },

    reset() {
      this.selectedKey = null
      this.isOpened = false
      this.result = null
    }
  }
}
</script>

<style scoped>
.lucky-key-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.treasure-box {
  position: relative;
  width: 300rpx;
  height: 250rpx;
  margin-bottom: 60rpx;
}

.box-lid {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 280rpx;
  height: 80rpx;
  background: linear-gradient(180deg, #8B4513, #654321);
  border-radius: 20rpx 20rpx 0 0;
  border: 6rpx solid #FFD700;
  border-bottom: none;
  z-index: 2;
  transition: all 0.5s;
}

.treasure-box.opened .box-lid {
  transform: translateX(-50%) rotateX(-120deg);
  transform-origin: bottom center;
}

.box-body {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 300rpx;
  height: 180rpx;
  background: linear-gradient(180deg, #8B4513, #654321);
  border-radius: 10rpx;
  border: 6rpx solid #FFD700;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.prize-inside {
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popUp 0.5s ease-out;
}

@keyframes popUp {
  0% { transform: translateY(50rpx); opacity: 0; }
  100% { transform: translateY(0); opacity: 1; }
}

.prize-icon {
  font-size: 80rpx;
}

.prize-name {
  font-size: 26rpx;
  color: #FFD700;
  font-weight: bold;
  margin-top: 10rpx;
}

.box-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400rpx;
  height: 400rpx;
  background: radial-gradient(circle, rgba(255,215,0,0.4) 0%, transparent 70%);
  animation: glow 1s ease-in-out infinite alternate;
  z-index: -1;
}

@keyframes glow {
  from { transform: translate(-50%, -50%) scale(0.8); opacity: 0.5; }
  to { transform: translate(-50%, -50%) scale(1.2); opacity: 1; }
}

.keys-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.keys-title {
  font-size: 30rpx;
  color: rgba(255,255,255,0.8);
  margin-bottom: 30rpx;
}

.keys-row {
  display: flex;
  gap: 30rpx;
}

.key-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 20rpx;
  border: 4rpx solid transparent;
  transition: all 0.3s;
}

.key-item.selected {
  background: rgba(255,215,0,0.2);
  border-color: #FFD700;
  transform: scale(1.1);
}

.key-item.shake {
  animation: shake 0.5s ease-in-out infinite;
}

@keyframes shake {
  0%, 100% { transform: scale(1.1) rotate(0deg); }
  25% { transform: scale(1.1) rotate(-10deg); }
  75% { transform: scale(1.1) rotate(10deg); }
}

.key-icon {
  font-size: 60rpx;
  margin-bottom: 10rpx;
}

.key-label {
  font-size: 24rpx;
  color: #fff;
}

.action-btn {
  margin-top: 50rpx;
  padding: 24rpx 80rpx;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  border-radius: 50rpx;
  box-shadow: 0 8rpx 20rpx rgba(255,165,0,0.4);
}

.action-btn.reset {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 8rpx 20rpx rgba(102,126,234,0.4);
}

.action-btn text {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
}
</style>
