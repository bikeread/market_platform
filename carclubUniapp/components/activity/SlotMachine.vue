<template>
  <view class="slot-machine-container">
    <!-- 老虎机外框 -->
    <view class="machine-frame">
      <!-- 顶部装饰 -->
      <view class="machine-top">
        <view class="top-light" v-for="i in 7" :key="i" :class="{ 'light-on': lightOn === (i % 2) }"></view>
      </view>

      <!-- 老虎机显示窗口 -->
      <view class="reels-window">
        <view class="reels-container">
          <!-- 三个滚轴 -->
          <view class="reel" v-for="(reel, reelIndex) in reels" :key="reelIndex">
            <view
              class="reel-strip"
              :style="{
                transform: 'translateY(-' + reel.position + 'rpx)',
                transition: reel.spinning ? 'none' : 'transform 0.5s ease-out'
              }"
            >
              <view
                class="symbol"
                v-for="(symbol, symIndex) in reel.symbols"
                :key="symIndex"
              >
                {{ symbol }}
              </view>
            </view>
          </view>
        </view>

        <!-- 中线指示器 -->
        <view class="center-line"></view>
      </view>

      <!-- 底部装饰 -->
      <view class="machine-bottom">
        <view class="bottom-light" v-for="i in 7" :key="i" :class="{ 'light-on': lightOn !== (i % 2) }"></view>
      </view>
    </view>

    <!-- 开始按钮 -->
    <view class="spin-button" @click="startSpin" :class="{ 'disabled': isSpinning }">
      <text class="button-text">{{ isSpinning ? '转动中...' : 'SPIN' }}</text>
    </view>

    <!-- 赢得提示 -->
    <view class="win-display" v-if="lastWin">
      <text class="win-text">{{ lastWin }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'SlotMachine',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { symbols: ['7️⃣', '7️⃣', '7️⃣'], name: '超级大奖', probability: 0.01 },
        { symbols: ['💎', '💎', '💎'], name: '钻石大奖', probability: 0.02 },
        { symbols: ['🍒', '🍒', '🍒'], name: '樱桃奖', probability: 0.05 },
        { symbols: ['🍋', '🍋', '🍋'], name: '柠檬奖', probability: 0.08 },
        { symbols: ['🍊', '🍊', '🍊'], name: '橙子奖', probability: 0.1 },
        { symbols: ['🍇', '🍇', '🍇'], name: '葡萄奖', probability: 0.12 },
        { symbols: ['⭐', '⭐', '⭐'], name: '星星奖', probability: 0.15 }
      ]
    },
    dailyLimit: {
      type: Number,
      default: 5
    }
  },
  data() {
    return {
      allSymbols: ['7️⃣', '💎', '🍒', '🍋', '🍊', '🍇', '⭐', '🔔', '💰', '🎰'],
      reels: [
        { symbols: [], position: 0, spinning: false },
        { symbols: [], position: 0, spinning: false },
        { symbols: [], position: 0, spinning: false }
      ],
      isSpinning: false,
      lightOn: 0,
      lightTimer: null,
      spinTimers: [],
      lastWin: '',
      todayCount: 0,
      symbolHeight: 160 // rpx
    }
  },
  mounted() {
    this.initReels()
    this.startLightAnimation()
    this.loadTodayCount()
  },
  beforeDestroy() {
    this.clearAllTimers()
  },
  methods: {
    clearAllTimers() {
      if (this.lightTimer) {
        clearInterval(this.lightTimer)
      }
      this.spinTimers.forEach(timer => clearInterval(timer))
      this.spinTimers = []
    },

    initReels() {
      const newReels = []
      for (let i = 0; i < 3; i++) {
        const symbols = []
        for (let j = 0; j < 30; j++) {
          symbols.push(this.allSymbols[Math.floor(Math.random() * this.allSymbols.length)])
        }
        newReels.push({ symbols, position: 0, spinning: false })
      }
      this.reels = newReels
    },

    loadTodayCount() {
      const today = new Date().toDateString()
      const saved = uni.getStorageSync('slot_record') || {}
      if (saved.date === today) {
        this.todayCount = saved.count || 0
      } else {
        this.todayCount = 0
      }
    },

    saveTodayCount() {
      const today = new Date().toDateString()
      uni.setStorageSync('slot_record', {
        date: today,
        count: this.todayCount
      })
    },

    startLightAnimation() {
      this.lightTimer = setInterval(() => {
        this.lightOn = this.lightOn === 0 ? 1 : 0
      }, 400)
    },

    drawPrize() {
      const random = Math.random()
      let sum = 0
      for (let i = 0; i < this.prizes.length; i++) {
        sum += this.prizes[i].probability
        if (random < sum) {
          return this.prizes[i]
        }
      }
      return null
    },

    generateLosingCombination() {
      const result = []
      for (let i = 0; i < 3; i++) {
        result.push(this.allSymbols[Math.floor(Math.random() * this.allSymbols.length)])
      }
      if (result[0] === result[1] && result[1] === result[2]) {
        const otherSymbols = this.allSymbols.filter(s => s !== result[0])
        result[2] = otherSymbols[Math.floor(Math.random() * otherSymbols.length)]
      }
      return result
    },

    startSpin() {
      if (this.isSpinning) return

      if (this.todayCount >= this.dailyLimit) {
        uni.showToast({
          title: `今日已用完${this.dailyLimit}次机会`,
          icon: 'none'
        })
        return
      }

      this.isSpinning = true
      this.lastWin = ''
      this.todayCount++
      this.saveTodayCount()

      // 决定结果
      const prize = this.drawPrize()
      let targetSymbols
      if (prize) {
        targetSymbols = prize.symbols
      } else {
        targetSymbols = this.generateLosingCombination()
      }

      // 设置目标符号到位置15
      const newReels = JSON.parse(JSON.stringify(this.reels))
      for (let i = 0; i < 3; i++) {
        newReels[i].symbols[15] = targetSymbols[i]
        newReels[i].spinning = true
        newReels[i].position = 0
      }
      this.reels = newReels

      // 开始滚动每个轴
      const stopDelays = [1500, 2000, 2500]
      const targetPosition = 14 * this.symbolHeight // 停在第15个符号（索引14）

      for (let i = 0; i < 3; i++) {
        this.animateReel(i, stopDelays[i], targetPosition)
      }

      // 全部停止后显示结果
      setTimeout(() => {
        this.isSpinning = false

        if (prize) {
          this.lastWin = `🎊 ${prize.name}!`
          this.$emit('finish', prize)

          uni.showModal({
            title: '🎰 恭喜中奖!',
            content: `您获得了：${prize.name}`,
            showCancel: false,
            confirmText: '太棒了'
          })
        } else {
          this.lastWin = '再接再厉!'
          this.$emit('finish', null)
        }
      }, 3000)
    },

    animateReel(reelIndex, stopDelay, targetPosition) {
      const totalSymbols = this.reels[reelIndex].symbols.length
      const maxPosition = totalSymbols * this.symbolHeight
      let currentPosition = 0
      const speed = 80 // rpx per frame
      const frameInterval = 16 // ~60fps

      const timer = setInterval(() => {
        currentPosition += speed
        if (currentPosition >= maxPosition) {
          currentPosition = currentPosition % maxPosition
        }

        // 使用新对象触发响应式更新
        const newReels = [...this.reels]
        newReels[reelIndex] = {
          ...newReels[reelIndex],
          position: currentPosition
        }
        this.reels = newReels
      }, frameInterval)

      this.spinTimers.push(timer)

      // 到时间后停止
      setTimeout(() => {
        clearInterval(timer)

        // 平滑停止到目标位置
        const newReels = [...this.reels]
        newReels[reelIndex] = {
          ...newReels[reelIndex],
          position: targetPosition,
          spinning: false
        }
        this.reels = newReels
      }, stopDelay)
    }
  }
}
</script>

<style scoped>
.slot-machine-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.machine-frame {
  background: linear-gradient(180deg, #8B0000, #B22222, #8B0000);
  border-radius: 40rpx;
  padding: 30rpx;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.5);
  border: 8rpx solid #FFD700;
}

.machine-top,
.machine-bottom {
  display: flex;
  justify-content: space-around;
  padding: 20rpx 10rpx;
}

.top-light,
.bottom-light {
  width: 36rpx;
  height: 36rpx;
  border-radius: 50%;
  background: #333;
  transition: all 0.3s;
}

.top-light.light-on,
.bottom-light.light-on {
  background: #FFD700;
  box-shadow: 0 0 30rpx #FFD700, 0 0 60rpx #FFA500;
}

.reels-window {
  position: relative;
  background: linear-gradient(180deg, #1a1a2e, #16213e);
  border-radius: 20rpx;
  padding: 20rpx;
  border: 6rpx solid #FFD700;
  overflow: hidden;
}

.reels-container {
  display: flex;
  gap: 16rpx;
  height: 480rpx;
  overflow: hidden;
}

.reel {
  width: 160rpx;
  height: 480rpx;
  background: linear-gradient(180deg, #0f0f23, #1a1a2e, #0f0f23);
  border-radius: 16rpx;
  overflow: hidden;
  position: relative;
  border: 4rpx solid #333;
}

.reel-strip {
  display: flex;
  flex-direction: column;
}

.symbol {
  width: 160rpx;
  height: 160rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 80rpx;
}

.center-line {
  position: absolute;
  top: 50%;
  left: 10rpx;
  right: 10rpx;
  height: 8rpx;
  background: linear-gradient(90deg, transparent, #FFD700, transparent);
  transform: translateY(-50%);
  z-index: 3;
  box-shadow: 0 0 20rpx #FFD700;
}

.spin-button {
  margin-top: 50rpx;
  padding: 30rpx 120rpx;
  background: linear-gradient(180deg, #FFD700, #FFA500);
  border-radius: 60rpx;
  border: 6rpx solid #8B4513;
  box-shadow: 0 12rpx 0 #8B4513, 0 20rpx 40rpx rgba(0, 0, 0, 0.3);
}

.spin-button:active:not(.disabled) {
  transform: translateY(6rpx);
  box-shadow: 0 6rpx 0 #8B4513, 0 10rpx 20rpx rgba(0, 0, 0, 0.3);
}

.spin-button.disabled {
  opacity: 0.6;
}

.button-text {
  font-size: 48rpx;
  font-weight: bold;
  color: #8B4513;
}

.win-display {
  margin-top: 40rpx;
  padding: 30rpx 60rpx;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  border-radius: 30rpx;
  animation: pulse 0.5s ease-in-out infinite alternate;
}

.win-text {
  font-size: 48rpx;
  font-weight: bold;
  color: #8B0000;
}

@keyframes pulse {
  from {
    transform: scale(1);
    box-shadow: 0 0 40rpx #FFD700;
  }
  to {
    transform: scale(1.05);
    box-shadow: 0 0 80rpx #FFD700, 0 0 120rpx #FFA500;
  }
}
</style>
