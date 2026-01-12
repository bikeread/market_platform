<template>
  <view class="race-container">
    <view class="race-track">
      <view class="track-lanes">
        <view class="lane" v-for="i in 3" :key="i">
          <view class="lane-line"></view>
        </view>
      </view>

      <view class="car player-car" :style="{ left: playerPosition + 'rpx' }">
        🏎️
      </view>

      <view
        class="obstacle"
        v-for="obs in obstacles"
        :key="obs.id"
        :style="{ left: obs.x + 'rpx', top: obs.lane * 180 + 'rpx' }"
      >
        🚧
      </view>

      <view class="finish-line" v-if="distance > 800">
        🏁
      </view>
    </view>

    <view class="game-hud">
      <view class="hud-item">
        <text>距离：{{ distance }}m</text>
      </view>
      <view class="hud-item">
        <text>速度：{{ speed }}km/h</text>
      </view>
    </view>

    <view class="control-area" v-if="gameState === 'playing'">
      <view class="control-btn" @click="accelerate">
        <text>🚀 加速</text>
      </view>
    </view>

    <view class="start-overlay" v-if="gameState === 'ready'">
      <text class="start-title">🏎️ 赛道冲刺</text>
      <text class="start-desc">点击加速冲过终点线</text>
      <view class="start-btn" @click="startRace">
        <text>开始比赛</text>
      </view>
    </view>

    <view class="result-overlay" v-if="gameState === 'ended'">
      <text class="result-title">{{ isWin ? '🎉 冲线成功' : '💥 撞到障碍' }}</text>
      <text class="result-time">用时：{{ raceTime.toFixed(1) }}秒</text>
      <view class="result-prize">
        <text class="prize-icon">{{ prize && prize.icon ? prize.icon : '💫' }}</text>
        <text class="prize-name">{{ prize && prize.name ? prize.name : '谢谢参与' }}</text>
      </view>
      <view class="restart-btn" @click="resetRace">
        <text>再来一次</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'RaceTrack',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '冠军大礼包', icon: '🏆', maxTime: 5 },
        { name: '亚军礼品', icon: '🥈', maxTime: 8 },
        { name: '季军礼品', icon: '🥉', maxTime: 12 },
        { name: '参与奖', icon: '🎁', maxTime: 20 },
        { name: '谢谢参与', icon: '💫', maxTime: 999 }
      ]
    }
  },
  data() {
    return {
      gameState: 'ready',
      playerPosition: 100,
      distance: 0,
      speed: 0,
      raceTime: 0,
      obstacles: [],
      prize: null,
      isWin: false,
      gameTimer: null,
      obstacleTimer: null
    }
  },
  beforeDestroy() {
    this.clearTimers()
  },
  methods: {
    clearTimers() {
      if (this.gameTimer) clearInterval(this.gameTimer)
      if (this.obstacleTimer) clearInterval(this.obstacleTimer)
    },

    startRace() {
      this.gameState = 'playing'
      this.distance = 0
      this.speed = 50
      this.raceTime = 0
      this.obstacles = []
      this.playerPosition = 100
      this.isWin = false

      // 游戏主循环
      this.gameTimer = setInterval(() => {
        this.raceTime += 0.1
        this.speed = Math.max(30, this.speed - 2)
        this.distance += Math.floor(this.speed / 10)
        this.playerPosition += this.speed / 5

        // 移动障碍物
        this.obstacles = this.obstacles
          .map(obs => ({ ...obs, x: obs.x - 20 }))
          .filter(obs => obs.x > -100)

        // 碰撞检测
        for (const obs of this.obstacles) {
          if (obs.x < 200 && obs.x > 50) {
            this.endRace(false)
            return
          }
        }

        // 到达终点
        if (this.distance >= 1000) {
          this.endRace(true)
        }
      }, 100)

      // 生成障碍物
      this.obstacleTimer = setInterval(() => {
        if (Math.random() > 0.5) {
          this.obstacles.push({
            id: Date.now(),
            x: 700,
            lane: Math.floor(Math.random() * 3)
          })
        }
      }, 1000)
    },

    accelerate() {
      this.speed = Math.min(180, this.speed + 20)
      uni.vibrateShort()
    },

    endRace(win) {
      this.clearTimers()
      this.gameState = 'ended'
      this.isWin = win

      // 根据时间确定奖品
      if (win) {
        for (const prize of this.prizes) {
          if (this.raceTime <= prize.maxTime) {
            this.prize = prize
            break
          }
        }
      } else {
        this.prize = { name: '谢谢参与', icon: '💫' }
      }

      this.$emit('finish', this.prize)
    },

    resetRace() {
      this.gameState = 'ready'
      this.prize = null
    }
  }
}
</script>

<style scoped>
.race-container {
  width: 100%;
  height: 100vh;
  position: relative;
  overflow: hidden;
  background: linear-gradient(180deg, #1a1a2e, #16213e);
}

.race-track {
  width: 100%;
  height: 600rpx;
  background: #333;
  position: relative;
  margin-top: 100rpx;
  overflow: hidden;
}

.track-lanes {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.lane {
  flex: 1;
  border-bottom: 4rpx dashed rgba(255,255,255,0.3);
  position: relative;
}

.car {
  position: absolute;
  font-size: 80rpx;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
  transition: left 0.1s;
}

.obstacle {
  position: absolute;
  font-size: 60rpx;
  transition: left 0.1s;
}

.finish-line {
  position: absolute;
  right: 50rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 100rpx;
}

.game-hud {
  position: absolute;
  top: 30rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
}

.hud-item {
  padding: 16rpx 30rpx;
  background: rgba(0,0,0,0.6);
  border-radius: 20rpx;
}

.hud-item text {
  font-size: 28rpx;
  color: #fff;
  font-family: monospace;
}

.control-area {
  position: absolute;
  bottom: 100rpx;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
}

.control-btn {
  padding: 40rpx 100rpx;
  background: linear-gradient(135deg, #c31432, #240b36);
  border-radius: 50rpx;
  box-shadow: 0 10rpx 30rpx rgba(195,20,50,0.5);
}

.control-btn:active {
  transform: scale(0.95);
}

.control-btn text {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
}

.start-overlay,
.result-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.start-title,
.result-title {
  font-size: 56rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 20rpx;
}

.start-desc {
  font-size: 28rpx;
  color: rgba(255,255,255,0.7);
  margin-bottom: 50rpx;
}

.start-btn,
.restart-btn {
  padding: 30rpx 100rpx;
  background: linear-gradient(135deg, #c31432, #240b36);
  border-radius: 50rpx;
}

.start-btn text,
.restart-btn text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.result-time {
  font-size: 32rpx;
  color: #FFD700;
  margin-bottom: 30rpx;
}

.result-prize {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 20rpx;
  margin-bottom: 40rpx;
}

.prize-icon {
  font-size: 100rpx;
  margin-bottom: 10rpx;
}

.prize-name {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
}
</style>
