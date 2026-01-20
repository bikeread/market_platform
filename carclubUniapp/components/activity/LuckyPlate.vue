<template>
  <view class="lucky-plate-container">
    <view class="plate-header">
      <text class="header-title">🚘 幸运车牌</text>
      <text class="header-desc">输入车牌号码参与抽奖</text>
    </view>

    <view class="plate-input-area">
      <view class="plate-frame">
        <view class="plate-province" @click="showProvincePicker">
          <text>{{ province || '粤' }}</text>
        </view>
        <view class="plate-numbers">
          <input
            class="plate-input"
            v-model="plateNumber"
            placeholder="A12345"
            maxlength="6"
            @input="onPlateInput"
          />
        </view>
      </view>
      <text class="plate-preview">{{ fullPlate }}</text>
    </view>

    <view class="lucky-display" v-if="luckyInfo">
      <view class="lucky-score">
        <text class="score-label">车牌幸运值</text>
        <text class="score-value">{{ luckyInfo.score }}</text>
        <view class="score-stars">
          <text v-for="i in 5" :key="i">{{ i <= luckyInfo.stars ? '⭐' : '☆' }}</text>
        </view>
      </view>
      <view class="lucky-meaning">
        <text>{{ luckyInfo.meaning }}</text>
      </view>
    </view>

    <view class="action-area">
      <view class="analyze-btn" @click="analyzePlate" :class="{ 'disabled': !isValidPlate }">
        <text>分析车牌运势</text>
      </view>
      <view class="draw-btn" @click="drawPrize" :class="{ 'disabled': !luckyInfo }" v-if="luckyInfo">
        <text>抽取幸运奖品</text>
      </view>
    </view>

    <view class="result-box" v-if="prize">
      <text class="result-title">🎊 恭喜获得</text>
      <view class="result-prize">
        <text class="prize-icon">{{ prize.icon }}</text>
        <text class="prize-name">{{ prize.name }}</text>
      </view>
      <view class="reset-btn" @click="reset">
        <text>再试一次</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'LuckyPlate',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '终身免费保养', icon: '🔧', minScore: 90 },
        { name: '10000元购车券', icon: '🎫', minScore: 80 },
        { name: '5000元加油卡', icon: '⛽', minScore: 70 },
        { name: '精品大礼包', icon: '🎁', minScore: 60 },
        { name: '200元代金券', icon: '💳', minScore: 40 },
        { name: '谢谢参与', icon: '💫', minScore: 0 }
      ]
    }
  },
  data() {
    return {
      province: '粤',
      plateNumber: '',
      luckyInfo: null,
      prize: null,
      provinces: ['京', '津', '沪', '渝', '冀', '豫', '云', '辽', '黑', '湘', '皖', '鲁', '新', '苏', '浙', '赣', '鄂', '桂', '甘', '晋', '蒙', '陕', '吉', '闽', '贵', '粤', '青', '藏', '川', '宁', '琼']
    }
  },
  computed: {
    fullPlate() {
      return `${this.province}·${this.plateNumber.toUpperCase()}`
    },
    isValidPlate() {
      return this.plateNumber.length >= 5
    }
  },
  methods: {
    onPlateInput(e) {
      this.plateNumber = e.detail.value.toUpperCase()
      this.luckyInfo = null
      this.prize = null
    },

    showProvincePicker() {
      uni.showActionSheet({
        itemList: this.provinces,
        success: (res) => {
          this.province = this.provinces[res.tapIndex]
          this.luckyInfo = null
        }
      })
    },

    analyzePlate() {
      if (!this.isValidPlate) {
        uni.showToast({
          title: '请输入完整车牌号',
          icon: 'none'
        })
        return
      }

      // 模拟分析过程
      uni.showLoading({ title: '分析中...' })

      setTimeout(() => {
        uni.hideLoading()

        // 根据车牌计算幸运值
        const score = this.calculateScore()
        const stars = Math.ceil(score / 20)
        const meaning = this.getMeaning(score)

        this.luckyInfo = { score, stars, meaning }
      }, 1500)
    },

    calculateScore() {
      // 简单的幸运值计算逻辑
      let score = 50
      const plate = this.plateNumber.toUpperCase()

      // 连号加分
      if (/(\d)\1{2,}/.test(plate)) score += 20
      // 顺子加分
      if (/123|234|345|456|567|678|789/.test(plate)) score += 15
      // 豹子号
      if (/(\d)\1{4,}/.test(plate)) score += 30
      // 吉祥数字
      if (/[68]/.test(plate)) score += 10
      // 随机因素
      score += Math.floor(Math.random() * 20)

      return Math.min(100, Math.max(20, score))
    },

    getMeaning(score) {
      if (score >= 90) return '🌟 极品靓号！财运亨通，一路顺风'
      if (score >= 80) return '✨ 上等吉号！事业顺遂，出行平安'
      if (score >= 70) return '⭐ 吉祥好号！福气满满，万事如意'
      if (score >= 60) return '🌙 中上之选！稳中向好，前途光明'
      if (score >= 40) return '💫 普通号码，平平安安就是福'
      return '🌱 一般号码，但安全驾驶最重要'
    },

    drawPrize() {
      if (!this.luckyInfo) return

      // 根据幸运值确定奖品
      for (const prize of this.prizes) {
        if (this.luckyInfo.score >= prize.minScore) {
          this.prize = prize
          break
        }
      }

      this.$emit('finish', this.prize)

      setTimeout(() => {
        uni.showModal({
          title: '🎊 恭喜',
          content: `您的幸运车牌 ${this.fullPlate} 获得：${this.prize.icon} ${this.prize.name}`,
          showCancel: false
        })
      }, 300)
    },

    reset() {
      this.plateNumber = ''
      this.luckyInfo = null
      this.prize = null
    }
  }
}
</script>

<style scoped>
.lucky-plate-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40rpx;
}

.plate-header {
  text-align: center;
  margin-bottom: 50rpx;
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

.plate-input-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40rpx;
}

.plate-frame {
  display: flex;
  align-items: center;
  background: linear-gradient(180deg, #0066cc, #004499);
  padding: 20rpx 30rpx;
  border-radius: 16rpx;
  border: 6rpx solid #fff;
  box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.3);
}

.plate-province {
  width: 80rpx;
  height: 80rpx;
  background: transparent;
  border-right: 4rpx solid rgba(255,255,255,0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.plate-province text {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
}

.plate-numbers {
  flex: 1;
}

.plate-input {
  width: 300rpx;
  height: 80rpx;
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
  letter-spacing: 8rpx;
  text-transform: uppercase;
}

.plate-preview {
  margin-top: 20rpx;
  font-size: 32rpx;
  color: #FFD700;
  font-weight: bold;
}

.lucky-display {
  width: 100%;
  max-width: 500rpx;
  padding: 40rpx;
  background: rgba(255,255,255,0.1);
  border-radius: 20rpx;
  margin-bottom: 40rpx;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  0% { opacity: 0; transform: translateY(20rpx); }
  100% { opacity: 1; transform: translateY(0); }
}

.lucky-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30rpx;
}

.score-label {
  font-size: 26rpx;
  color: rgba(255,255,255,0.7);
  margin-bottom: 10rpx;
}

.score-value {
  font-size: 80rpx;
  font-weight: bold;
  color: #FFD700;
  font-family: monospace;
}

.score-stars {
  font-size: 36rpx;
  letter-spacing: 8rpx;
}

.lucky-meaning {
  text-align: center;
  padding: 20rpx;
  background: rgba(255,215,0,0.1);
  border-radius: 10rpx;
}

.lucky-meaning text {
  font-size: 28rpx;
  color: #FFD700;
}

.action-area {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.analyze-btn,
.draw-btn {
  padding: 30rpx 80rpx;
  border-radius: 50rpx;
  box-shadow: 0 8rpx 20rpx rgba(0,0,0,0.3);
}

.analyze-btn {
  background: linear-gradient(135deg, #2c3e50, #4ca1af);
}

.draw-btn {
  background: linear-gradient(135deg, #FFD700, #FFA500);
}

.analyze-btn.disabled,
.draw-btn.disabled {
  opacity: 0.5;
}

.analyze-btn text,
.draw-btn text {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.result-box {
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

.result-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.result-prize {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30rpx;
}

.prize-icon {
  font-size: 100rpx;
  margin-bottom: 10rpx;
}

.prize-name {
  font-size: 32rpx;
  color: #4ca1af;
  font-weight: bold;
}

.reset-btn {
  padding: 20rpx 60rpx;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 40rpx;
}

.reset-btn text {
  font-size: 28rpx;
  color: #fff;
}
</style>
