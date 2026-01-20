<template>
  <view class="red-packet-container">
    <view class="game-area" v-if="gameState === 'playing'" @click="handleClick">
      <view
        class="red-packet"
        v-for="packet in packets"
        :key="packet.id"
        :style="{
          left: packet.x + 'rpx',
          top: packet.y + 'rpx',
          transform: 'rotate(' + packet.rotate + 'deg)'
        }"
        :class="{ 'caught': packet.caught }"
        @click.stop="catchPacket(packet)"
      >
        🧧
      </view>
    </view>

    <view class="start-screen" v-if="gameState === 'ready'">
      <text class="game-title">🧧 红包雨来袭</text>
      <text class="game-desc">在规定时间内抓取更多红包</text>
      <view class="start-btn" @click="startGame">
        <text>开始游戏</text>
      </view>
    </view>

    <view class="result-screen" v-if="gameState === 'ended'">
      <text class="result-title">🎊 游戏结束</text>
      <text class="result-score">抓到 {{ caughtCount }} 个红包</text>
      <view class="prize-info">
        <text class="prize-icon">{{ prize && prize.icon ? prize.icon : '🎁' }}</text>
        <text class="prize-name">{{ prize && prize.name ? prize.name : '感谢参与' }}</text>
      </view>
      <view class="play-again-btn" @click="resetGame">
        <text>再玩一次</text>
      </view>
    </view>

    <view class="game-hud" v-if="gameState === 'playing'">
      <view class="hud-item">
        <text>⏱️ {{ timeLeft }}s</text>
      </view>
      <view class="hud-item">
        <text>🧧 {{ caughtCount }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'RedPacketRain',
  props: {
    duration: {
      type: Number,
      default: 10
    },
    prizes: {
      type: Array,
      default: () => [
        { name: '大红包888元', icon: '💰', minCatch: 20 },
        { name: '中红包88元', icon: '🎁', minCatch: 15 },
        { name: '小红包8元', icon: '🧧', minCatch: 10 },
        { name: '优惠券5元', icon: '🎫', minCatch: 5 },
        { name: '谢谢参与', icon: '💫', minCatch: 0 }
      ]
    }
  },
  data() {
    return {
      gameState: 'ready', // ready, playing, ended
      packets: [],
      caughtCount: 0,
      timeLeft: 0,
      prize: null,
      gameTimer: null,
      spawnTimer: null,
      packetId: 0
    }
  },
  beforeDestroy() {
    this.clearTimers()
  },
  methods: {
    clearTimers() {
      if (this.gameTimer) clearInterval(this.gameTimer)
      if (this.spawnTimer) clearInterval(this.spawnTimer)
    },

    startGame() {
      this.gameState = 'playing'
      this.packets = []
      this.caughtCount = 0
      this.timeLeft = this.duration
      this.packetId = 0

      // 游戏计时器
      this.gameTimer = setInterval(() => {
        this.timeLeft--
        if (this.timeLeft <= 0) {
          this.endGame()
        }
      }, 1000)

      // 生成红包
      this.spawnTimer = setInterval(() => {
        this.spawnPacket()
      }, 300)

      // 红包下落动画
      setInterval(() => {
        this.updatePackets()
      }, 50)
    },

    spawnPacket() {
      if (this.gameState !== 'playing') return

      const packet = {
        id: this.packetId++,
        x: Math.random() * 600 + 40,
        y: -100,
        speed: Math.random() * 10 + 8,
        rotate: Math.random() * 30 - 15,
        caught: false
      }
      this.packets.push(packet)

      // 限制屏幕上的红包数量
      if (this.packets.length > 20) {
        this.packets = this.packets.filter(p => !p.caught && p.y < 1200)
      }
    },

    updatePackets() {
      if (this.gameState !== 'playing') return

      this.packets = this.packets.map(p => {
        if (!p.caught) {
          return { ...p, y: p.y + p.speed }
        }
        return p
      }).filter(p => p.y < 1200 || p.caught)
    },

    catchPacket(packet) {
      if (packet.caught || this.gameState !== 'playing') return

      packet.caught = true
      this.caughtCount++

      // 触觉反馈
      uni.vibrateShort()
    },

    handleClick(e) {
      // 点击空白处不处理
    },

    endGame() {
      this.clearTimers()
      this.gameState = 'ended'

      // 根据抓取数量确定奖品
      for (const prize of this.prizes) {
        if (this.caughtCount >= prize.minCatch) {
          this.prize = prize
          break
        }
      }

      this.$emit('finish', this.prize)

      setTimeout(() => {
        uni.showModal({
          title: '🧧 红包雨结束',
          content: `您抓到${this.caughtCount}个红包，获得：${this.prize.icon} ${this.prize.name}`,
          showCancel: false
        })
      }, 500)
    },

    resetGame() {
      this.gameState = 'ready'
      this.packets = []
      this.prize = null
    }
  }
}
</script>

<style scoped>
.red-packet-container {
  width: 100%;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(180deg, #ff416c, #ff4b2b);
}

.game-area {
  width: 100%;
  height: 100%;
  position: relative;
}

.red-packet {
  position: absolute;
  font-size: 80rpx;
  transition: transform 0.1s;
  z-index: 10;
}

.red-packet.caught {
  animation: catch 0.3s ease-out forwards;
}

@keyframes catch {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); }
  100% { transform: scale(0); opacity: 0; }
}

.start-screen,
.result-screen {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx;
  background: rgba(255,255,255,0.95);
  border-radius: 30rpx;
  box-shadow: 0 20rpx 60rpx rgba(0,0,0,0.3);
}

.game-title,
.result-title {
  font-size: 48rpx;
  font-weight: bold;
  color: #ff4b2b;
  margin-bottom: 20rpx;
}

.game-desc {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 40rpx;
}

.start-btn,
.play-again-btn {
  padding: 24rpx 80rpx;
  background: linear-gradient(135deg, #ff416c, #ff4b2b);
  border-radius: 50rpx;
  box-shadow: 0 8rpx 20rpx rgba(255,75,43,0.4);
}

.start-btn text,
.play-again-btn text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.result-score {
  font-size: 36rpx;
  color: #333;
  margin-bottom: 30rpx;
}

.prize-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx;
  background: linear-gradient(135deg, #fff5f5, #ffe5e5);
  border-radius: 20rpx;
  margin-bottom: 40rpx;
}

.prize-icon {
  font-size: 80rpx;
  margin-bottom: 10rpx;
}

.prize-name {
  font-size: 32rpx;
  color: #ff4b2b;
  font-weight: bold;
}

.game-hud {
  position: absolute;
  top: 40rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  padding: 0 40rpx;
  z-index: 100;
}

.hud-item {
  padding: 16rpx 30rpx;
  background: rgba(0,0,0,0.5);
  border-radius: 30rpx;
}

.hud-item text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}
</style>
