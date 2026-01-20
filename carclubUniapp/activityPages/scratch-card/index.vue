<template>
  <view class="page-container">
    <ScratchCard @finish="onFinish" />
  </view>
</template>

<script>
import ScratchCard from '@/components/activity/ScratchCard.vue'

export default {
  components: { ScratchCard },
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
  background: linear-gradient(180deg, #11998e 0%, #38ef7d 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
