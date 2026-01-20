<template>
  <view class="page-container">
    <FlipCard @finish="onFinish" />
  </view>
</template>

<script>
import FlipCard from '@/components/activity/FlipCard.vue'

export default {
  components: { FlipCard },
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
  background: linear-gradient(180deg, #654ea3 0%, #eaafc8 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
