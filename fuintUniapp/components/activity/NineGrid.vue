<template>
  <view class="nine-grid-container">
    <view class="grid-wrapper">
      <view
        class="grid-item"
        v-for="(item, index) in gridItems"
        :key="index"
        :class="{
          'active': currentIndex === index,
          'center': index === 4,
          'winner': winnerIndex === index
        }"
        @click="index === 4 && startLottery()"
      >
        <template v-if="index === 4">
          <view class="start-btn" :class="{ 'disabled': isRunning }">
            <text class="btn-text">{{ isRunning ? '抽奖中' : '开始' }}</text>
          </view>
        </template>
        <template v-else>
          <text class="item-icon">{{ item.icon }}</text>
          <text class="item-name">{{ item.name }}</text>
        </template>
      </view>
    </view>

    <view class="result-tip" v-if="result">
      <text>🎊 恭喜获得：{{ result.icon }} {{ result.name }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'NineGrid',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '一等奖', icon: '🏆', probability: 0.02 },
        { name: '二等奖', icon: '🎁', probability: 0.05 },
        { name: '三等奖', icon: '🎈', probability: 0.1 },
        { name: '优惠券', icon: '🎫', probability: 0.15 },
        { name: '积分', icon: '⭐', probability: 0.2 },
        { name: '加油卡', icon: '⛽', probability: 0.15 },
        { name: '保养券', icon: '🔧', probability: 0.18 },
        { name: '谢谢参与', icon: '💫', probability: 0.15 }
      ]
    }
  },
  data() {
    return {
      gridItems: [],
      currentIndex: -1,
      winnerIndex: -1,
      isRunning: false,
      result: null,
      // 转动顺序：0,1,2,5,8,7,6,3 (顺时针)
      sequence: [0, 1, 2, 5, 8, 7, 6, 3]
    }
  },
  created() {
    this.initGrid()
  },
  methods: {
    initGrid() {
      // 填充8个奖品位置 + 1个中心按钮
      const items = []
      for (let i = 0; i < 9; i++) {
        if (i === 4) {
          items.push({ name: '开始', icon: '🎯' })
        } else {
          const prizeIndex = i > 4 ? i - 1 : i
          items.push(this.prizes[prizeIndex] || { name: '奖品', icon: '🎁' })
        }
      }
      this.gridItems = items
    },

    drawPrize() {
      const random = Math.random()
      let sum = 0
      for (let i = 0; i < this.prizes.length; i++) {
        sum += this.prizes[i].probability
        if (random < sum) return i
      }
      return this.prizes.length - 1
    },

    startLottery() {
      if (this.isRunning) return

      this.isRunning = true
      this.result = null
      this.winnerIndex = -1

      // 决定中奖位置
      const prizeIndex = this.drawPrize()
      // 转换为grid位置
      const targetGridIndex = prizeIndex >= 4 ? prizeIndex + 1 : prizeIndex
      // 找到在sequence中的位置
      const targetSeqIndex = this.sequence.indexOf(targetGridIndex)

      // 计算总步数：至少转3圈 + 到达目标位置
      const minRounds = 3
      const totalSteps = minRounds * 8 + targetSeqIndex

      let currentStep = 0
      let speed = 50

      const animate = () => {
        const seqIndex = currentStep % 8
        this.currentIndex = this.sequence[seqIndex]
        currentStep++

        if (currentStep < totalSteps) {
          // 逐渐减速
          if (currentStep > totalSteps - 10) {
            speed += 30
          } else if (currentStep > totalSteps - 5) {
            speed += 50
          }
          setTimeout(animate, speed)
        } else {
          // 结束
          this.isRunning = false
          this.winnerIndex = this.currentIndex
          this.result = this.gridItems[this.currentIndex]

          this.$emit('finish', this.result)

          setTimeout(() => {
            uni.showModal({
              title: '🎊 恭喜',
              content: `您获得了：${this.result.icon} ${this.result.name}`,
              showCancel: false
            })
          }, 300)
        }
      }

      animate()
    }
  }
}
</script>

<style scoped>
.nine-grid-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.grid-wrapper {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16rpx;
  padding: 20rpx;
  background: linear-gradient(135deg, #ee0979, #ff6a00);
  border-radius: 24rpx;
  box-shadow: 0 10rpx 40rpx rgba(0,0,0,0.3);
}

.grid-item {
  width: 180rpx;
  height: 180rpx;
  background: linear-gradient(180deg, #fff8e1, #ffecb3);
  border-radius: 16rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.15s;
  border: 4rpx solid transparent;
}

.grid-item.active {
  background: linear-gradient(180deg, #FFD700, #FFA500);
  transform: scale(1.05);
  border-color: #fff;
  box-shadow: 0 0 30rpx rgba(255,215,0,0.8);
}

.grid-item.winner {
  animation: flash 0.5s ease-in-out 3;
}

@keyframes flash {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.grid-item.center {
  background: linear-gradient(180deg, #ff416c, #ff4b2b);
}

.item-icon {
  font-size: 56rpx;
  margin-bottom: 8rpx;
}

.item-name {
  font-size: 24rpx;
  color: #333;
  font-weight: 500;
}

.start-btn {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
}

.start-btn.disabled {
  opacity: 0.7;
}

.btn-text {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.3);
}

.result-tip {
  margin-top: 40rpx;
  padding: 20rpx 40rpx;
  background: rgba(255,215,0,0.2);
  border-radius: 30rpx;
  border: 2rpx solid #FFD700;
}

.result-tip text {
  font-size: 30rpx;
  color: #FFD700;
  font-weight: bold;
}
</style>
