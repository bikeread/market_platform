<template>
  <view class="golden-egg-container">
    <view class="stage">
      <view class="eggs-row">
        <view
          class="egg-wrapper"
          v-for="(egg, index) in eggs"
          :key="index"
          @click="smashEgg(index)"
        >
          <view class="egg" :class="{ 'smashed': egg.smashed, 'shake': egg.shaking }">
            <view class="egg-top" v-if="!egg.smashed">
              <view class="egg-shine"></view>
            </view>
            <view class="egg-bottom" v-if="!egg.smashed"></view>
            <view class="egg-broken" v-if="egg.smashed">
              <view class="prize-reveal">
                <text class="prize-icon">{{ egg.prize && egg.prize.icon ? egg.prize.icon : '🎁' }}</text>
                <text class="prize-name">{{ egg.prize && egg.prize.name ? egg.prize.name : '谢谢参与' }}</text>
              </view>
            </view>
          </view>
          <view class="egg-shadow"></view>
        </view>
      </view>
    </view>

    <view class="hammer" :class="{ 'swing': isSwinging }">🔨</view>

    <view class="tip-text" v-if="!hasSmashed">
      <text>点击金蛋砸开领取奖品</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'GoldenEgg',
  props: {
    prizes: {
      type: Array,
      default: () => [
        { name: '一等奖', icon: '🏆', probability: 0.05 },
        { name: '二等奖', icon: '🎁', probability: 0.1 },
        { name: '三等奖', icon: '🎈', probability: 0.2 },
        { name: '谢谢参与', icon: '💫', probability: 0.65 }
      ]
    }
  },
  data() {
    return {
      eggs: [
        { smashed: false, shaking: false, prize: null },
        { smashed: false, shaking: false, prize: null },
        { smashed: false, shaking: false, prize: null }
      ],
      hasSmashed: false,
      isSwinging: false
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

    smashEgg(index) {
      if (this.hasSmashed || this.eggs[index].smashed) return

      this.isSwinging = true
      this.eggs[index].shaking = true

      setTimeout(() => {
        this.isSwinging = false
        this.eggs[index].shaking = false
        this.eggs[index].smashed = true
        this.eggs[index].prize = this.drawPrize()
        this.hasSmashed = true

        this.$emit('finish', this.eggs[index].prize)

        setTimeout(() => {
          uni.showModal({
            title: '🎊 恭喜',
            content: `您获得了：${this.eggs[index].prize.icon} ${this.eggs[index].prize.name}`,
            showCancel: false
          })
        }, 500)
      }, 600)
    }
  }
}
</script>

<style scoped>
.golden-egg-container {
  padding: 40rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stage {
  background: linear-gradient(180deg, #8B0000, #DC143C);
  border-radius: 30rpx;
  padding: 60rpx 40rpx;
  width: 100%;
  box-shadow: 0 20rpx 60rpx rgba(0,0,0,0.4);
}

.eggs-row {
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
}

.egg-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.egg {
  width: 160rpx;
  height: 200rpx;
  position: relative;
  cursor: pointer;
}

.egg.shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: rotate(0deg); }
  25% { transform: rotate(-10deg); }
  75% { transform: rotate(10deg); }
}

.egg-top {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 140rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #FFD700, #FFA500, #FF8C00);
  border-radius: 50% 50% 50% 50% / 60% 60% 40% 40%;
  box-shadow: inset -10rpx -10rpx 30rpx rgba(0,0,0,0.2);
}

.egg-shine {
  position: absolute;
  top: 20rpx;
  left: 25rpx;
  width: 30rpx;
  height: 40rpx;
  background: rgba(255,255,255,0.6);
  border-radius: 50%;
  transform: rotate(-30deg);
}

.egg-bottom {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 160rpx;
  height: 140rpx;
  background: linear-gradient(135deg, #FFD700, #FFA500, #FF8C00);
  border-radius: 50% 50% 50% 50% / 40% 40% 60% 60%;
  box-shadow: inset -10rpx 10rpx 30rpx rgba(0,0,0,0.2);
}

.egg-shadow {
  width: 120rpx;
  height: 30rpx;
  background: rgba(0,0,0,0.3);
  border-radius: 50%;
  margin-top: 10rpx;
}

.egg-broken {
  width: 160rpx;
  height: 200rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.prize-reveal {
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: popIn 0.5s ease-out;
}

@keyframes popIn {
  0% { transform: scale(0); opacity: 0; }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); opacity: 1; }
}

.prize-icon {
  font-size: 80rpx;
}

.prize-name {
  font-size: 24rpx;
  color: #FFD700;
  margin-top: 10rpx;
  font-weight: bold;
}

.hammer {
  font-size: 100rpx;
  margin-top: 40rpx;
  transition: transform 0.3s;
}

.hammer.swing {
  animation: swing 0.5s ease-in-out;
}

@keyframes swing {
  0% { transform: rotate(0deg); }
  50% { transform: rotate(-45deg); }
  100% { transform: rotate(0deg); }
}

.tip-text {
  margin-top: 30rpx;
}

.tip-text text {
  font-size: 28rpx;
  color: rgba(255,255,255,0.8);
}
</style>
