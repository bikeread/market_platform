<template>
  <div title="帮助文档" class="nav-icon">
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'CarclubDoc',
  data() {
    return {
      url: 'https://carclub.cn/docs/index/'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
