<template>
  <view class="card-collection-container">
    <view class="collection-header">
      <text class="header-title">🎴 集卡兑奖</text>
      <text class="header-desc">集齐全部卡片兑换大奖</text>
    </view>

    <view class="cards-grid">
      <view
        class="collect-card"
        v-for="(card, index) in cards"
        :key="index"
        :class="{ 'collected': card.collected }"
      >
        <view class="card-inner" v-if="card.collected">
          <text class="card-icon">{{ card.icon }}</text>
          <text class="card-name">{{ card.name }}</text>
        </view>
        <view class="card-placeholder" v-else>
          <text class="placeholder-icon">❓</text>
          <text class="placeholder-text">{{ card.name }}</text>
        </view>
        <view class="card-count" v-if="card.count > 1">x{{ card.count }}</view>
      </view>
    </view>

    <view class="progress-section">
      <text class="progress-text">已收集 {{ collectedCount }}/{{ cards.length }}</text>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
      </view>
    </view>

    <view class="action-area">
      <view class="draw-btn" @click="drawCard" :class="{ 'disabled': isDrawing }">
        <text>{{ isDrawing ? '抽卡中...' : '抽取卡片' }}</text>
      </view>
      <view class="exchange-btn" @click="exchangePrize" :class="{ 'disabled': !canExchange }">
        <text>兑换大奖</text>
      </view>
    </view>

    <view class="draw-result" v-if="lastDrawn">
      <text class="result-title">获得卡片</text>
      <view class="result-card">
        <text class="result-icon">{{ lastDrawn.icon }}</text>
        <text class="result-name">{{ lastDrawn.name }}</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'CardCollection',
  props: {
    cardTypes: {
      type: Array,
      default: () => [
        { id: 1, name: '发动机', icon: '🔧', probability: 0.2 },
        { id: 2, name: '方向盘', icon: '🚗', probability: 0.2 },
        { id: 3, name: '轮胎', icon: '⚙️', probability: 0.2 },
        { id: 4, name: '车灯', icon: '💡', probability: 0.2 },
        { id: 5, name: '钥匙', icon: '🔑', probability: 0.15 },
        { id: 6, name: '奖杯', icon: '🏆', probability: 0.05 }
      ]
    },
    grandPrize: {
      type: Object,
      default: () => ({ name: '购车优惠券10000元', icon: '🎊' })
    }
  },
  data() {
    return {
      cards: [],
      isDrawing: false,
      lastDrawn: null
    }
  },
  computed: {
    collectedCount() {
      return this.cards.filter(c => c.collected).length
    },
    progressPercent() {
      return (this.collectedCount / this.cards.length) * 100
    },
    canExchange() {
      return this.cards.every(c => c.collected)
    }
  },
  created() {
    this.initCards()
  },
  methods: {
    initCards() {
      // 从存储加载或初始化
      const saved = uni.getStorageSync('collected_cards') || {}
      this.cards = this.cardTypes.map(type => ({
        ...type,
        collected: saved[type.id] || false,
        count: saved[type.id] ? (saved[`count_${type.id}`] || 1) : 0
      }))
    },

    saveCards() {
      const saveData = {}
      this.cards.forEach(card => {
        if (card.collected) {
          saveData[card.id] = true
          saveData[`count_${card.id}`] = card.count
        }
      })
      uni.setStorageSync('collected_cards', saveData)
    },

    drawCard() {
      if (this.isDrawing) return

      this.isDrawing = true
      this.lastDrawn = null

      setTimeout(() => {
        // 根据概率抽取
        const random = Math.random()
        let sum = 0
        let drawnCard = null

        for (const card of this.cardTypes) {
          sum += card.probability
          if (random < sum) {
            drawnCard = card
            break
          }
        }

        if (!drawnCard) {
          drawnCard = this.cardTypes[this.cardTypes.length - 1]
        }

        // 更新卡片状态
        const cardIndex = this.cards.findIndex(c => c.id === drawnCard.id)
        if (cardIndex !== -1) {
          this.cards[cardIndex].collected = true
          this.cards[cardIndex].count++
        }

        this.lastDrawn = drawnCard
        this.isDrawing = false
        this.saveCards()

        this.$emit('draw', drawnCard)
      }, 1000)
    },

    exchangePrize() {
      if (!this.canExchange) {
        uni.showToast({
          title: '请先集齐所有卡片',
          icon: 'none'
        })
        return
      }

      uni.showModal({
        title: '🎊 恭喜',
        content: `集卡成功！获得：${this.grandPrize.icon} ${this.grandPrize.name}`,
        showCancel: false,
        success: () => {
          // 重置卡片
          uni.removeStorageSync('collected_cards')
          this.initCards()
          this.$emit('finish', this.grandPrize)
        }
      })
    }
  }
}
</script>

<style scoped>
.card-collection-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.collection-header {
  text-align: center;
  margin-bottom: 40rpx;
}

.header-title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 10rpx;
}

.header-desc {
  font-size: 26rpx;
  color: rgba(255,255,255,0.7);
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20rpx;
  padding: 30rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 20rpx;
  margin-bottom: 30rpx;
}

.collect-card {
  width: 180rpx;
  height: 220rpx;
  border-radius: 16rpx;
  position: relative;
  overflow: hidden;
}

.card-inner {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #FFD700, #FFA500);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  animation: cardIn 0.5s ease-out;
}

@keyframes cardIn {
  0% { transform: rotateY(90deg); }
  100% { transform: rotateY(0deg); }
}

.card-icon {
  font-size: 60rpx;
  margin-bottom: 10rpx;
}

.card-name {
  font-size: 24rpx;
  color: #8B4513;
  font-weight: bold;
}

.card-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #444, #333);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 4rpx dashed #666;
}

.placeholder-icon {
  font-size: 50rpx;
  margin-bottom: 10rpx;
}

.placeholder-text {
  font-size: 22rpx;
  color: #888;
}

.card-count {
  position: absolute;
  top: 10rpx;
  right: 10rpx;
  background: #ff4444;
  color: #fff;
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
}

.progress-section {
  width: 100%;
  max-width: 500rpx;
  margin-bottom: 40rpx;
}

.progress-text {
  display: block;
  text-align: center;
  font-size: 28rpx;
  color: #FFD700;
  margin-bottom: 16rpx;
}

.progress-bar {
  width: 100%;
  height: 20rpx;
  background: rgba(255,255,255,0.2);
  border-radius: 10rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #56ab2f, #a8e063);
  transition: width 0.3s;
}

.action-area {
  display: flex;
  gap: 30rpx;
  margin-bottom: 40rpx;
}

.draw-btn,
.exchange-btn {
  padding: 24rpx 50rpx;
  border-radius: 40rpx;
  box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.3);
}

.draw-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.exchange-btn {
  background: linear-gradient(135deg, #56ab2f, #a8e063);
}

.draw-btn.disabled,
.exchange-btn.disabled {
  opacity: 0.5;
}

.draw-btn text,
.exchange-btn text {
  font-size: 28rpx;
  color: #fff;
  font-weight: bold;
}

.draw-result {
  padding: 30rpx;
  background: rgba(255,215,0,0.2);
  border-radius: 20rpx;
  border: 4rpx solid #FFD700;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popIn 0.5s ease-out;
}

@keyframes popIn {
  0% { transform: scale(0); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.result-title {
  font-size: 26rpx;
  color: rgba(255,255,255,0.8);
  margin-bottom: 16rpx;
}

.result-card {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.result-icon {
  font-size: 80rpx;
}

.result-name {
  font-size: 28rpx;
  color: #FFD700;
  font-weight: bold;
}
</style>
