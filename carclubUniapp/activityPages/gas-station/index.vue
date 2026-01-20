<template>
  <view class="page-container">
    <GasStation @finish="onFinish" />
  </view>
</template>

<script>
import GasStation from '@/components/activity/GasStation.vue'

export default {
  components: { GasStation },
  methods: {
    onFinish(prize) {
      console.log('抽奖结果:', prize)
    }
  }
}
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
