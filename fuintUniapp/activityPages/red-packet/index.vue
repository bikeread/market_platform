<template>
  <view class="page-container">
    <RedPacketRain @finish="onFinish" />
  </view>
</template>

<script>
import RedPacketRain from '@/components/activity/RedPacketRain.vue'

export default {
  components: { RedPacketRain },
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
  background: linear-gradient(180deg, #ff416c 0%, #ff4b2b 100%);
}
</style>
