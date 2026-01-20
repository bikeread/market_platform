<template>
  <view class="lucky-wheel-container">
    <!-- 最外层装饰圈 -->
    <view class="outer-decoration">
      <!-- 灯珠 -->
      <view
        v-for="(dot, index) in dotList"
        :key="index"
        class="light-dot"
        :style="dot.style"
        :class="{ 'light-on': lightOn === (index % 2) }">
      </view>
    </view>

    <!-- 内层装饰边框 -->
    <view class="inner-border"></view>

    <!-- 转盘主体 -->
    <view class="wheel-body" :style="wheelStyle">
      <!-- Canvas 绘制扇形 -->
      <canvas
        canvas-id="wheelCanvas"
        class="wheel-canvas"
        :style="canvasStyle">
      </canvas>

      <!-- 奖品文字层 -->
      <view
        v-for="(item, index) in prizeTextList"
        :key="index"
        class="prize-item"
        :style="item.style">
        <image
          v-if="item.imgSrc"
          class="prize-img"
          :src="item.imgSrc"
          mode="aspectFit">
        </image>
        <text v-else class="prize-icon">{{ item.icon }}</text>
        <text class="prize-name">{{ item.name }}</text>
      </view>
    </view>

    <!-- 中心指针装饰 -->
    <view class="pointer-wrapper">
      <view class="pointer-triangle"></view>
    </view>

    <!-- 中心抽奖按钮 -->
    <view class="center-btn" :class="{ disabled: isSpinning }" @click="handleSpin">
      <view class="btn-inner">
        <text class="btn-text">{{ isSpinning ? '...' : 'GO' }}</text>
      </view>
      <view class="btn-ring"></view>
    </view>

    <!-- 剩余次数 -->
    <view class="remain-count">
      <text class="count-text">剩余 </text>
      <text class="count-num">{{ remainCount }}</text>
      <text class="count-text"> 次</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'LuckyWheel',
  props: {
    prizes: {
      type: Array,
      default: function() {
        return [
          { id: 1, name: '一等奖', icon: '🎁', probability: 0.01 },
          { id: 2, name: '二等奖', icon: '🎈', probability: 0.05 },
          { id: 3, name: '三等奖', icon: '🎀', probability: 0.1 },
          { id: 4, name: '四等奖', icon: '💫', probability: 0.34 },
          { id: 5, name: '五等奖', icon: '🌟', probability: 0.15 },
          { id: 6, name: '谢谢参与', icon: '💝', probability: 0.35 }
        ]
      }
    },
    dailyLimit: {
      type: Number,
      default: 3
    }
  },
  data: function() {
    return {
      rotation: 0,
      isSpinning: false,
      isAnimating: false,
      lightOn: 0,
      lightTimer: null,
      usedCount: 0,
      wheelSize: 280,
      // 高级配色方案
      colorScheme: [
        { bg: '#FF6B6B', border: '#E85555' },
        { bg: '#FFEAA7', border: '#F5D58A' },
        { bg: '#FF6B6B', border: '#E85555' },
        { bg: '#FFEAA7', border: '#F5D58A' },
        { bg: '#FF6B6B', border: '#E85555' },
        { bg: '#FFEAA7', border: '#F5D58A' },
        { bg: '#FF6B6B', border: '#E85555' },
        { bg: '#FFEAA7', border: '#F5D58A' }
      ]
    }
  },
  computed: {
    prizeCount: function() {
      return this.prizes.length
    },
    sliceAngle: function() {
      return 360 / this.prizeCount
    },
    remainCount: function() {
      return Math.max(0, this.dailyLimit - this.usedCount)
    },
    canvasStyle: function() {
      return 'width: ' + this.wheelSize + 'px; height: ' + this.wheelSize + 'px;'
    },
    wheelStyle: function() {
      var transition = this.isAnimating
        ? 'transform 4s cubic-bezier(0.17, 0.67, 0.12, 0.99)'
        : 'none'
      return 'transform: rotate(' + this.rotation + 'deg); transition: ' + transition + ';'
    },
    dotList: function() {
      var list = []
      var dotCount = 20
      var radius = 152
      var centerX = 160
      var centerY = 160
      for (var i = 0; i < dotCount; i++) {
        var angle = (i * 360 / dotCount) - 90
        var rad = angle * Math.PI / 180
        var x = centerX + Math.cos(rad) * radius - 6
        var y = centerY + Math.sin(rad) * radius - 6
        list.push({
          style: 'left: ' + x + 'px; top: ' + y + 'px;'
        })
      }
      return list
    },
    prizeTextList: function() {
      var list = []
      var self = this
      var radius = 85
      var centerX = this.wheelSize / 2
      var centerY = this.wheelSize / 2
      this.prizes.forEach(function(prize, idx) {
        var angle = self.sliceAngle * idx + self.sliceAngle / 2 - 90
        var rad = angle * Math.PI / 180
        var x = centerX + Math.cos(rad) * radius
        var y = centerY + Math.sin(rad) * radius
        var rotate = angle + 90
        list.push({
          icon: prize.icon,
          name: prize.name,
          imgSrc: prize.imgSrc || '',
          style: 'left: ' + x + 'px; top: ' + y + 'px; transform: translate(-50%, -50%) rotate(' + rotate + 'deg);'
        })
      })
      return list
    }
  },
  mounted: function() {
    this.initLightEffect()
    this.loadUsedCount()
    var self = this
    this.$nextTick(function() {
      setTimeout(function() {
        self.drawWheel()
      }, 150)
    })
  },
  beforeDestroy: function() {
    this.stopLightEffect()
  },
  methods: {
    drawWheel: function() {
      var ctx = uni.createCanvasContext('wheelCanvas', this)
      var cx = this.wheelSize / 2
      var cy = this.wheelSize / 2
      var radius = this.wheelSize / 2
      var count = this.prizeCount
      var sliceRad = (2 * Math.PI) / count
      var self = this

      // 绘制扇形区域
      for (var i = 0; i < count; i++) {
        var startAngle = i * sliceRad - Math.PI / 2
        var endAngle = startAngle + sliceRad
        var colorIdx = i % self.colorScheme.length

        // 绘制主扇形
        ctx.beginPath()
        ctx.moveTo(cx, cy)
        ctx.arc(cx, cy, radius, startAngle, endAngle)
        ctx.closePath()
        ctx.setFillStyle(self.colorScheme[colorIdx].bg)
        ctx.fill()

        // 绘制扇形边框
        ctx.beginPath()
        ctx.moveTo(cx, cy)
        ctx.arc(cx, cy, radius, startAngle, endAngle)
        ctx.closePath()
        ctx.setStrokeStyle(self.colorScheme[colorIdx].border)
        ctx.setLineWidth(1)
        ctx.stroke()
      }

      // 绘制中心圆形装饰
      ctx.beginPath()
      ctx.arc(cx, cy, 50, 0, 2 * Math.PI)
      ctx.setFillStyle('#FFFFFF')
      ctx.fill()

      ctx.beginPath()
      ctx.arc(cx, cy, 50, 0, 2 * Math.PI)
      ctx.setStrokeStyle('#FFD700')
      ctx.setLineWidth(3)
      ctx.stroke()

      ctx.draw()
    },

    initLightEffect: function() {
      var self = this
      this.lightTimer = setInterval(function() {
        self.lightOn = self.lightOn === 0 ? 1 : 0
      }, 300)
    },

    stopLightEffect: function() {
      if (this.lightTimer) {
        clearInterval(this.lightTimer)
        this.lightTimer = null
      }
    },

    loadUsedCount: function() {
      try {
        var today = new Date().toDateString()
        var stored = uni.getStorageSync('lucky_wheel_data')
        if (stored && stored.date === today) {
          this.usedCount = stored.count || 0
        } else {
          this.usedCount = 0
        }
      } catch (e) {
        this.usedCount = 0
      }
    },

    saveUsedCount: function() {
      try {
        var today = new Date().toDateString()
        uni.setStorageSync('lucky_wheel_data', { date: today, count: this.usedCount })
      } catch (e) {
        // 忽略存储错误
      }
    },

    selectPrize: function() {
      var random = Math.random()
      var cumulative = 0
      for (var i = 0; i < this.prizes.length; i++) {
        cumulative += this.prizes[i].probability
        if (random < cumulative) {
          return i
        }
      }
      return this.prizes.length - 1
    },

    handleSpin: function() {
      if (this.isSpinning) return
      if (this.usedCount >= this.dailyLimit) {
        uni.showToast({ title: '今日次数已用完', icon: 'none' })
        return
      }

      this.isSpinning = true
      this.usedCount++
      this.saveUsedCount()

      var prizeIndex = this.selectPrize()
      var prize = this.prizes[prizeIndex]
      var rounds = 6

      // 计算目标扇形中心相对于初始顶部的角度
      // 扇形i的中心初始位置 = i * sliceAngle + sliceAngle/2 (从顶部顺时针)
      var targetAngle = this.sliceAngle * prizeIndex + this.sliceAngle / 2

      // 关键：考虑当前转盘已累积的旋转角度
      // 扇形中心当前实际位置 = (初始位置 + 已旋转角度) % 360
      var prizeCurrentAngle = (targetAngle + this.rotation) % 360

      // 计算还需要旋转多少度才能让扇形中心对准顶部(0度)
      var neededRotation = (360 - prizeCurrentAngle) % 360
      if (neededRotation === 0) neededRotation = 360 // 避免不转动

      var finalRotation = this.rotation + 360 * rounds + neededRotation

      var self = this
      this.isAnimating = true

      setTimeout(function() {
        self.rotation = finalRotation
      }, 50)

      setTimeout(function() {
        self.isSpinning = false
        self.isAnimating = false
        self.$emit('finish', prize)
        uni.showModal({
          title: '恭喜您',
          content: prize.icon + ' ' + prize.name,
          showCancel: false,
          confirmText: '确定'
        })
      }, 4200)
    }
  }
}
</script>

<style scoped>
.lucky-wheel-container {
  position: relative;
  width: 320px;
  height: 380px;
  margin: 0 auto;
}

/* 最外层装饰圈 */
.outer-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 320px;
  height: 320px;
  border-radius: 50%;
  background: linear-gradient(180deg, #FF4757 0%, #C0392B 100%);
  box-shadow:
    0 8px 30px rgba(192, 57, 43, 0.5),
    inset 0 2px 0 rgba(255, 255, 255, 0.2),
    inset 0 -2px 0 rgba(0, 0, 0, 0.1);
}

/* 灯珠样式 */
.light-dot {
  position: absolute;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.2);
  transition: all 0.15s ease;
}

.light-dot.light-on {
  background: #FFD700;
  box-shadow:
    0 0 8px 3px rgba(255, 215, 0, 0.8),
    0 0 15px 5px rgba(255, 215, 0, 0.4),
    inset 0 1px 2px rgba(255, 255, 255, 0.5);
}

/* 内层边框 */
.inner-border {
  position: absolute;
  top: 15px;
  left: 15px;
  width: 290px;
  height: 290px;
  border-radius: 50%;
  background: linear-gradient(180deg, #F39C12 0%, #E67E22 100%);
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 转盘主体 */
.wheel-body {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  overflow: hidden;
  background: #FFF;
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1);
}

.wheel-canvas {
  position: absolute;
  top: 0;
  left: 0;
}

/* 奖品项 */
.prize-item {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 60px;
  z-index: 5;
}

.prize-img {
  width: 28px;
  height: 28px;
}

.prize-icon {
  font-size: 24px;
}

.prize-name {
  font-size: 10px;
  color: #333;
  font-weight: 500;
  margin-top: 2px;
  white-space: nowrap;
  max-width: 55px;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 指针装饰 */
.pointer-wrapper {
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 25;
}

.pointer-triangle {
  width: 0;
  height: 0;
  border-left: 14px solid transparent;
  border-right: 14px solid transparent;
  border-top: 28px solid #C0392B;
  filter: drop-shadow(0 2px 3px rgba(0, 0, 0, 0.3));
}

/* 中心按钮 */
.center-btn {
  position: absolute;
  top: 160px;
  left: 160px;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  z-index: 30;
  cursor: pointer;
}

.btn-inner {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 66px;
  height: 66px;
  border-radius: 50%;
  background: linear-gradient(180deg, #FF6B6B 0%, #C0392B 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 4px 15px rgba(192, 57, 43, 0.5),
    inset 0 2px 0 rgba(255, 255, 255, 0.3),
    inset 0 -2px 0 rgba(0, 0, 0, 0.2);
  z-index: 2;
}

.btn-ring {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(180deg, #FFD700 0%, #F39C12 100%);
  box-shadow: 0 3px 8px rgba(243, 156, 18, 0.4);
  z-index: 1;
}

.btn-text {
  color: #FFF;
  font-size: 22px;
  font-weight: bold;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.center-btn.disabled {
  opacity: 0.7;
}

.center-btn.disabled .btn-inner {
  background: linear-gradient(180deg, #999 0%, #666 100%);
}

/* 剩余次数 */
.remain-count {
  position: absolute;
  bottom: 15px;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 14px;
}

.count-text {
  color: #FFF;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.count-num {
  color: #FFD700;
  font-weight: bold;
  font-size: 18px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}
</style>
