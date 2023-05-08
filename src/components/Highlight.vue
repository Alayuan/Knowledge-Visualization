<template>
  <div class="container">
    <div
      class="highlight"
      v-for="(item, index) in hightLightWords"
      :key="index"
    >
        <span class="word">
          {{ item }}
        </span>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Highlight',
  props: {
    hightLightWords: {
      type: Array,
      default: () => []
    }
  }
}

</script>

<style lang="scss" scoped>
.container {
  display: flex;
  flex-wrap: wrap;
  .highlight {
    margin-left: 4px;
    margin-top: 2px;
    .word {
      line-height: 20px;
      padding: 0 2px 0 2px;
      background: rgb(230, 227, 227);
      border-radius: 6px;
      font-size: 2px;
    }
  }
}
</style>
