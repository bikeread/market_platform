<template>
  <view class="flip-card-container">
    <view class="game-info">
      <text class="info-text">翻开两张相同的卡片即可获奖</text>
      <text class="attempts-text">剩余翻牌次数：{{ maxAttempts - attempts }}</text>
    </view>

    <view class="cards-grid">
      <view
        class="card-wrapper"
        v-for="(card, index) in cards"
        :key="index"
        @click="flipCard(index)"
      >
        <view class="card" :class="{ 'flipped': card.flipped, 'matched': card.matched }">
          <view class="card-front">
            <text>🎴</text>
          </view>
          <view class="card-back">
            <text>{{ card.icon }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="result-box" v-if="gameEnded">
      <text class="result-icon">{{ prize && prize.icon ? prize.icon : '💫' }}</text>
      <text class="result-text">{{ prize && prize.name ? prize.name : '谢谢参与' }}</text>
      <view class="reset-btn" @click="resetGame">
        <text>再玩一次</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'FlipCard',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { icon: '🚗', name: '购车优惠券', matched: false },
        { icon: '🔧', name: '免费保养', matched: false },
        { icon: '⛽', name: '加油卡', matched: false },
        { icon: '🎁', name: '精品礼包', matched: false },
        { icon: '🎫', name: '代金券', matched: false },
        { icon: '💫', name: '谢谢参与', matched: false }
      ]
    },
    maxAttempts: {
      type: Number,
      default: 8
    }
  },
  data() {
    return {
      cards: [],
      flippedCards: [],
      attempts: 0,
      gameEnded: false,
      prize: null,
      isChecking: false
    }
  },
  created() {
    this.initGame()
  },
  methods: {
    initGame() {
      // 创建卡片对（每种两张）
      const cardPairs = []
      this.prizes.forEach((prize, index) => {
        cardPairs.push({
          id: index * 2,
          icon: prize.icon,
          name: prize.name,
          pairId: index,
          flipped: false,
          matched: false
        })
        cardPairs.push({
          id: index * 2 + 1,
          icon: prize.icon,
          name: prize.name,
          pairId: index,
          flipped: false,
          matched: false
        })
      })

      // 洗牌
      this.cards = this.shuffle(cardPairs)
    },

    shuffle(array) {
      const arr = [...array]
      for (let i = arr.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1))
        ;[arr[i], arr[j]] = [arr[j], arr[i]]
      }
      return arr
    },

    flipCard(index) {
      if (this.isChecking || this.gameEnded) return
      if (this.cards[index].flipped || this.cards[index].matched) return
      if (this.attempts >= this.maxAttempts) return

      this.cards[index].flipped = true
      this.flippedCards.push(index)

      if (this.flippedCards.length === 2) {
        this.attempts++
        this.checkMatch()
      }
    },

    checkMatch() {
      this.isChecking = true
      const [first, second] = this.flippedCards

      setTimeout(() => {
        if (this.cards[first].pairId === this.cards[second].pairId) {
          // 配对成功
          this.cards[first].matched = true
          this.cards[second].matched = true
          this.prize = {
            icon: this.cards[first].icon,
            name: this.cards[first].name
          }
          this.gameEnded = true

          this.$emit('finish', this.prize)

          setTimeout(() => {
            uni.showModal({
              title: '🎊 配对成功',
              content: `您获得了：${this.prize.icon} ${this.prize.name}`,
              showCancel: false
            })
          }, 300)
        } else {
          // 配对失败
          this.cards[first].flipped = false
          this.cards[second].flipped = false

          // 检查是否用完次数
          if (this.attempts >= this.maxAttempts) {
            this.gameEnded = true
            this.prize = { icon: '💫', name: '谢谢参与' }
            this.$emit('finish', this.prize)
          }
        }

        this.flippedCards = []
        this.isChecking = false
      }, 800)
    },

    resetGame() {
      this.attempts = 0
      this.gameEnded = false
      this.prize = null
      this.flippedCards = []
      this.initGame()
    }
  }
}
</script>

<style scoped>
.flip-card-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.game-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.info-text {
  font-size: 28rpx;
  color: rgba(255,255,255,0.8);
  margin-bottom: 16rpx;
}

.attempts-text {
  font-size: 32rpx;
  color: #FFD700;
  font-weight: bold;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
  padding: 20rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 20rpx;
}

.card-wrapper {
  perspective: 1000rpx;
}

.card {
  width: 140rpx;
  height: 180rpx;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.5s;
}

.card.flipped {
  transform: rotateY(180deg);
}

.card.matched {
  animation: matchPulse 0.5s ease-out;
}

@keyframes matchPulse {
  50% { transform: rotateY(180deg) scale(1.1); }
}

.card-front,
.card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  font-size: 60rpx;
}

.card-front {
  background: linear-gradient(135deg, #654ea3, #eaafc8);
  border: 4rpx solid rgba(255,255,255,0.3);
}

.card-back {
  background: linear-gradient(135deg, #fff, #f0f0f0);
  transform: rotateY(180deg);
  border: 4rpx solid #ddd;
}

.result-box {
  margin-top: 40rpx;
  padding: 40rpx 60rpx;
  background: rgba(255,255,255,0.95);
  border-radius: 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popIn 0.5s ease-out;
}

@keyframes popIn {
  0% { transform: scale(0); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.result-icon {
  font-size: 100rpx;
  margin-bottom: 20rpx;
}

.result-text {
  font-size: 36rpx;
  color: #333;
  font-weight: bold;
  margin-bottom: 30rpx;
}

.reset-btn {
  padding: 20rpx 60rpx;
  background: linear-gradient(135deg, #654ea3, #eaafc8);
  border-radius: 40rpx;
}

.reset-btn text {
  font-size: 28rpx;
  color: #fff;
  font-weight: bold;
}
</style>
