<template>
  <view class="scratch-container">
    <view class="card-wrapper">
      <view class="card-back">
        <view class="prize-content">
          <text class="prize-icon">{{ prize.icon }}</text>
          <text class="prize-name">{{ prize.name }}</text>
        </view>
      </view>

      <canvas
        canvas-id="scratchCanvas"
        class="scratch-canvas"
        @touchstart="onTouchStart"
        @touchmove="onTouchMove"
        @touchend="onTouchEnd"
      ></canvas>
    </view>

    <view class="tip-box">
      <text v-if="!isRevealed">用手指刮开涂层查看奖品</text>
      <text v-else class="revealed-text">🎉 恭喜获得奖品！</text>
    </view>

    <view class="scratch-again" v-if="isRevealed" @click="resetCard">
      <text>再刮一次</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'ScratchCard',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '保养券', icon: '🔧', probability: 0.1 },
        { name: '加油卡', icon: '⛽', probability: 0.15 },
        { name: '精品礼包', icon: '🎁', probability: 0.2 },
        { name: '谢谢参与', icon: '💫', probability: 0.55 }
      ]
    }
  },
  data() {
    return {
      ctx: null,
      prize: { name: '加载中...', icon: '🎁' },
      isRevealed: false,
      scratchedPixels: 0,
      canvasWidth: 300,
      canvasHeight: 200,
      lastX: 0,
      lastY: 0
    }
  },
  mounted() {
    this.initCard()
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

    initCard() {
      this.prize = this.drawPrize()
      this.isRevealed = false
      this.scratchedPixels = 0

      setTimeout(() => {
        this.ctx = uni.createCanvasContext('scratchCanvas', this)
        this.drawCoverLayer()
      }, 100)
    },

    drawCoverLayer() {
      // 绘制银色涂层
      const gradient = this.ctx.createLinearGradient(0, 0, this.canvasWidth, this.canvasHeight)
      gradient.addColorStop(0, '#C0C0C0')
      gradient.addColorStop(0.5, '#A8A8A8')
      gradient.addColorStop(1, '#C0C0C0')

      this.ctx.setFillStyle(gradient)
      this.ctx.fillRect(0, 0, this.canvasWidth, this.canvasHeight)

      // 添加刮刮乐文字
      this.ctx.setFillStyle('#888')
      this.ctx.setFontSize(20)
      this.ctx.setTextAlign('center')
      this.ctx.fillText('刮开此区域', this.canvasWidth / 2, this.canvasHeight / 2 - 10)
      this.ctx.fillText('查看奖品', this.canvasWidth / 2, this.canvasHeight / 2 + 15)

      this.ctx.draw()
    },

    onTouchStart(e) {
      if (this.isRevealed) return
      const touch = e.touches[0]
      this.lastX = touch.x
      this.lastY = touch.y
    },

    onTouchMove(e) {
      if (this.isRevealed) return
      const touch = e.touches[0]
      const x = touch.x
      const y = touch.y

      this.ctx.setGlobalCompositeOperation('destination-out')
      this.ctx.setLineCap('round')
      this.ctx.setLineJoin('round')
      this.ctx.setLineWidth(30)

      this.ctx.beginPath()
      this.ctx.moveTo(this.lastX, this.lastY)
      this.ctx.lineTo(x, y)
      this.ctx.stroke()
      this.ctx.draw(true)

      this.lastX = x
      this.lastY = y
      this.scratchedPixels += 10
    },

    onTouchEnd() {
      if (this.isRevealed) return

      // 刮开超过30%自动显示
      const totalPixels = this.canvasWidth * this.canvasHeight
      if (this.scratchedPixels > totalPixels * 0.3) {
        this.revealPrize()
      }
    },

    revealPrize() {
      this.isRevealed = true
      this.ctx.clearRect(0, 0, this.canvasWidth, this.canvasHeight)
      this.ctx.draw()

      this.$emit('finish', this.prize)

      if (this.prize.name !== '谢谢参与') {
        setTimeout(() => {
          uni.showModal({
            title: '🎉 恭喜中奖',
            content: `您获得了：${this.prize.icon} ${this.prize.name}`,
            showCancel: false
          })
        }, 300)
      }
    },

    resetCard() {
      this.initCard()
    }
  }
}
</script>

<style scoped>
.scratch-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.card-wrapper {
  position: relative;
  width: 600rpx;
  height: 400rpx;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 10rpx 40rpx rgba(0,0,0,0.3);
}

.card-back {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #11998e, #38ef7d);
  display: flex;
  align-items: center;
  justify-content: center;
}

.prize-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.prize-icon {
  font-size: 120rpx;
  margin-bottom: 20rpx;
}

.prize-name {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 4rpx 8rpx rgba(0,0,0,0.2);
}

.scratch-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 600rpx;
  height: 400rpx;
}

.tip-box {
  margin-top: 40rpx;
  padding: 20rpx 40rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 30rpx;
}

.tip-box text {
  font-size: 28rpx;
  color: rgba(255,255,255,0.8);
}

.revealed-text {
  color: #FFD700 !important;
  font-weight: bold;
}

.scratch-again {
  margin-top: 30rpx;
  padding: 20rpx 60rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 40rpx;
}

.scratch-again text {
  font-size: 30rpx;
  color: #fff;
  font-weight: bold;
}
</style>
