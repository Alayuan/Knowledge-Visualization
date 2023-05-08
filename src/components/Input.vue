<template>
  <div class="container">
    <input class="input" v-model="query" :placeholder="placeHolder" />
    <button class="search" @click="() => handleSearch(query)">Search</button>
  </div>
  <div class="article" v-if="articleList?.length">
    <div class="article__items" v-for="item in articleList" :key="item._id">
      <div class="article__items__item">
        <div class="title" v-html="item._source.title"></div>
        <div class="content" v-html="item._source.content"></div>
        <div class="hightlight_container">
          <Highlight :hightLightWords="item.highlight"/>
        </div>
        <input type="checkbox" class="check" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, toRefs, watch } from 'vue'
import { debounce } from 'throttle-debounce'
import { search } from '../service/search'
import Highlight from './Highlight'

const useSearchEffect = () => {
  const articleList = ref([])
  const handleSearch = async (query) => {
    const { data } = await search()
    articleList.value = data
  }
  return {
    handleSearch,
    articleList
  }
}

export default {
  name: 'Input',
  components: {
    Highlight
  },
  props: {
    placeHolder: {
      type: String,
      default: '请输入查询信息'
    },
    modelValue: String
  },
  emits: ['refresh'],
  setup (props, { emit }) {
    const { modelValue } = toRefs(props)
    const query = ref(modelValue.value)
    const { handleSearch, articleList } = useSearchEffect()
    watch(
      query,
      debounce(300, (newQuery) => {
        if (newQuery === '') {
          emit('refresh')
        } else {
          const query = newQuery.trim()
          emit('refresh', query)
          handleSearch(query)
        }
      })
    )
    watch(modelValue, (newVal) => {
      query.value = newVal
    })
    return {
      query,
      handleSearch,
      articleList
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  font-size: 0;
}
.input {
  width: 300px;
  height: 30px;
  box-sizing: border-box;
  outline: 0;
  border: 2px solid #111;
  color: #111;
  font-size: 5px;
}
.search {
  width: 50px;
  height: 30px;
  border: none;
  margin-left: 4px;
  vertical-align: top;
  border-radius: 5px;
  background: rgb(198, 201, 202);
}
.article {
  width: 340px;
  margin-top: 20px;
  &__items {
    &__item {
      position: relative;
      height: 130px;
      color: #111;
      background-color: #fff;
      border-radius: 5px;
      margin-top: 5px;
      .title {
        margin: 10px;
        width: 300px;
        line-height: 20px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      .content {
        width: 300px;
        margin: 4px;
        line-height: 23px;
        font-size: 3px;
        overflow: hidden;
        display: inline-block;
        text-align: left;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
      .hightlight_container {
        position: absolute;
        top: 80px;
        left: 0;
        bottom: 0;
      }
      .check {
        position: absolute;
        top: 6px;
        right: 0;
        background-color: rgb(185, 94, 94);
      }
    }
  }
}
</style>
