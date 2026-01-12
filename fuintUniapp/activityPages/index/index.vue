<template>
  <view class="container">
    <view class="header">
      <text class="title">🚗 汽车营销活动中心</text>
      <text class="subtitle">精选15款互动抽奖游戏</text>
    </view>

    <view class="activity-grid">
      <view
        class="activity-card"
        v-for="(item, index) in activities"
        :key="index"
        :style="{ background: item.gradient }"
        @click="goToActivity(item.path)"
      >
        <view class="card-icon">{{ item.icon }}</view>
        <view class="card-info">
          <text class="card-title">{{ item.name }}</text>
          <text class="card-desc">{{ item.desc }}</text>
        </view>
        <view class="card-tag" v-if="item.tag">{{ item.tag }}</view>
      </view>
    </view>

    <view class="footer">
      <text>— 更多精彩活动敬请期待 —</text>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      activities: [
        { name: '幸运大转盘', icon: '🎡', desc: '转动转盘赢大奖', path: '/activityPages/lucky-wheel/index', gradient: 'linear-gradient(135deg, #667eea, #764ba2)', tag: '经典' },
        { name: '老虎机', icon: '🎰', desc: '三个一样就中奖', path: '/activityPages/slot-machine/index', gradient: 'linear-gradient(135deg, #f093fb, #f5576c)', tag: '' },
        { name: '砸金蛋', icon: '🥚', desc: '砸开金蛋见惊喜', path: '/activityPages/golden-egg/index', gradient: 'linear-gradient(135deg, #FFD700, #FFA500)', tag: '喜庆' },
        { name: '刮刮卡', icon: '🎫', desc: '刮开涂层见好运', path: '/activityPages/scratch-card/index', gradient: 'linear-gradient(135deg, #11998e, #38ef7d)', tag: '' },
        { name: '九宫格抽奖', icon: '🎯', desc: '九选一幸运降临', path: '/activityPages/nine-grid/index', gradient: 'linear-gradient(135deg, #ee0979, #ff6a00)', tag: '热门' },
        { name: '幸运钥匙', icon: '🔑', desc: '选对钥匙开宝箱', path: '/activityPages/lucky-key/index', gradient: 'linear-gradient(135deg, #4776E6, #8E54E9)', tag: '汽车' },
        { name: '摇一摇', icon: '📱', desc: '摇动手机抽大奖', path: '/activityPages/shake-it/index', gradient: 'linear-gradient(135deg, #00c6ff, #0072ff)', tag: '' },
        { name: '红包雨', icon: '🧧', desc: '红包从天而降', path: '/activityPages/red-packet/index', gradient: 'linear-gradient(135deg, #ff416c, #ff4b2b)', tag: '节日' },
        { name: '翻牌配对', icon: '🃏', desc: '考验记忆力', path: '/activityPages/flip-card/index', gradient: 'linear-gradient(135deg, #654ea3, #eaafc8)', tag: '' },
        { name: '幸运扭蛋', icon: '🎱', desc: '扭出惊喜好礼', path: '/activityPages/gacha/index', gradient: 'linear-gradient(135deg, #a8edea, #fed6e3)', tag: '潮流' },
        { name: '方向盘转转', icon: '🚙', desc: '转动方向盘抽奖', path: '/activityPages/steering-wheel/index', gradient: 'linear-gradient(135deg, #3a7bd5, #3a6073)', tag: '汽车' },
        { name: '加油站挑战', icon: '⛽', desc: '加满油赢油卡', path: '/activityPages/gas-station/index', gradient: 'linear-gradient(135deg, #f7971e, #ffd200)', tag: '汽车' },
        { name: '赛道冲刺', icon: '🏎️', desc: '极速冲刺拿大奖', path: '/activityPages/race-track/index', gradient: 'linear-gradient(135deg, #c31432, #240b36)', tag: '汽车' },
        { name: '集卡兑奖', icon: '🎴', desc: '集齐卡片兑好礼', path: '/activityPages/card-collection/index', gradient: 'linear-gradient(135deg, #56ab2f, #a8e063)', tag: '' },
        { name: '幸运车牌', icon: '🚘', desc: '车牌号码抽大奖', path: '/activityPages/lucky-plate/index', gradient: 'linear-gradient(135deg, #2c3e50, #4ca1af)', tag: '汽车' }
      ]
    }
  },
  methods: {
    goToActivity(path) {
      uni.navigateTo({ url: path })
    }
  }
}
</script>

<style>
.container {
  min-height: 100vh;
  padding: 30rpx;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
}

.header {
  text-align: center;
  padding: 40rpx 0 50rpx;
}

.title {
  display: block;
  font-size: 52rpx;
  font-weight: bold;
  color: #fff;
  text-shadow: 0 4rpx 8rpx rgba(0,0,0,0.3);
}

.subtitle {
  display: block;
  font-size: 26rpx;
  color: rgba(255,255,255,0.7);
  margin-top: 16rpx;
}

.activity-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
  justify-content: space-between;
}

.activity-card {
  width: calc(50% - 12rpx);
  border-radius: 24rpx;
  padding: 30rpx;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8rpx 30rpx rgba(0,0,0,0.3);
}

.activity-card:active {
  transform: scale(0.98);
  opacity: 0.9;
}

.card-icon {
  font-size: 64rpx;
  margin-bottom: 16rpx;
}

.card-info {
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
}

.card-desc {
  font-size: 22rpx;
  color: rgba(255,255,255,0.8);
}

.card-tag {
  position: absolute;
  top: 16rpx;
  right: 16rpx;
  background: rgba(255,255,255,0.25);
  padding: 6rpx 16rpx;
  border-radius: 20rpx;
  font-size: 20rpx;
  color: #fff;
}

.footer {
  text-align: center;
  padding: 60rpx 0 40rpx;
}

.footer text {
  font-size: 24rpx;
  color: rgba(255,255,255,0.4);
}
</style>
