<template>
  <view class="page-container">
    <LuckyPlate @finish="onFinish" />
  </view>
</template>

<script>
import LuckyPlate from '@/components/activity/LuckyPlate.vue'

export default {
  components: { LuckyPlate },
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
  background: linear-gradient(180deg, #2c3e50 0%, #4ca1af 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
