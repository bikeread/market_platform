<template>
  <view class="page-container">
    <LuckyKey @finish="onFinish" />
  </view>
</template>

<script>
import LuckyKey from '@/components/activity/LuckyKey.vue'

export default {
  components: { LuckyKey },
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
  background: linear-gradient(180deg, #4776E6 0%, #8E54E9 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
