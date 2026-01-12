<template>
  <view class="page-container">
    <RaceTrack @finish="onFinish" />
  </view>
</template>

<script>
import RaceTrack from '@/components/activity/RaceTrack.vue'

export default {
  components: { RaceTrack },
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
}
</style>
