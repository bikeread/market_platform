<template>
  <view class="page-container">
    <GoldenEgg @finish="onFinish" />
  </view>
</template>

<script>
import GoldenEgg from '@/components/activity/GoldenEgg.vue'

export default {
  components: { GoldenEgg },
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
  background: linear-gradient(180deg, #8B0000 0%, #DC143C 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40rpx;
}
</style>
