<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'CarclubGit',
  data() {
    return {
      url: 'https://github.com/bikeread/carclub'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
