<template>
  <div class="echartLayout">
    <div id="container" class="container"></div>
  </div>
  <Input v-model="query" @refresh="initEchart"/>
  <!-- <ColorItems class="color_items" :colors="colors" :BigNodes="bigNodes"/> -->
</template>

<script>
import * as echarts from 'echarts'
import getNode from './service/fetchNodeOffline'
import { handleData, useRenderToolTipEffect, BIGSIZE } from './renderNode.js'
import Input from './components/Input.vue'

const SHOWING_NODE_COUNT = 57
const useRenderNodeEffect = (graphData, nodes) => {
  const categoryItemCount = graphData.nameArray.slice(0, 5)
  const option = {
    title: {
      text: 'COVID-19',
      subtext: 'Default layout',
      top: 'bottom',
      left: 'right'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: [
      // {
      //   // selectedMode: 'single',
      //   data: graphData.categories.map(function (item) {
      //     return item.name
      //   })
      //   // 后端版本
      // }
      {
        // selectedMode: 'single',
        width: 800,
        data: categoryItemCount,
        let: 80
      }
    ],
    animationDuration: 1500,
    animationEasingUpdate: 'quinticInOut',
    series: [
      {
        type: 'graph',
        layout: 'force',
        // data: graphData.nodes,
        // 后端版本
        // data: graphData.nodes,
        data: nodes,
        links: graphData.links,
        categories: graphData.categories,
        roam: true,
        label: {
          position: 'right',
          formatter: '{b}'
        },
        lineStyle: {
          color: 'source',
          curveness: 0.3
        },
        emphasis: {
          focus: 'adjacency',
          lineStyle: {
            width: 10
          }
        },
        force: {
          repulsion: 80,
          edgeLength: [60, 130],
          layoutAnimation: true
        },
        draggable: true,
        tooltip: {
          formatter: (params) => useRenderToolTipEffect(params, nodes)
        }
      }
    ]
  }
  return {
    option
  }
}

export default {
  name: 'personRelation',
  components: {
    Input
  },
  data () {
    return {
      myChart: null,
      chartData: [],
      chartLink: [],
      query: '',
      bigNodes: [],
      colors: []
    }
  },
  mounted () {
    this.initEchart()
  },
  methods: {
    async initEchart (keyword = 'Coronavirus') {
      const chartDom = document.getElementById('container')
      this.myChart = echarts.init(chartDom)
      this.myChart.showLoading()
      // const rawData = await getNode({
      //   keyword,
      //   size: 2
      // })
      const rawData = await getNode()
      // const graphData = handleData({ data: rawData.content }) // 联后端版本
      const graphData = handleData(rawData)
      this.renderData(graphData)
    },
    renderData (graphData) {
      this.myChart.hideLoading()
      // 设置echarts的option
      const nodes = graphData.nodes.slice(0, SHOWING_NODE_COUNT)
      // 初始化大节点颜色和name的items
      // this.colors = COLOR
      this.bigNodes = this.useFindBigNodes(nodes)
      // 初始化大节点颜色和name的items
      const { option } = useRenderNodeEffect(graphData, nodes)
      this.myChart.on('click', (parmas) => {
        const { name } = parmas
        this.query = name
        this.initEchart(name)
      })
      this.myChart.setOption(option)
    },
    useFindBigNodes (nodes) {
      const result = []
      nodes.map(item => {
        if (item.symbolSize === BIGSIZE) {
          result.push(item)
        }
      })
      return result
    }
  }
}

</script>

<style lang="scss">
.echartLayout {
  margin: auto;
  position: absolute;
  margin-top: 50px;
  margin-left: 200px;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
}
.container {
  width: 100%;
  height: 100%;
  overflow: hidden;
}
.tooltip-wordwrap {
  width: 200px;
}
.title {
  line-height: 14px;
  word-break: normal;
  // white-space: normal !important;
  color: rgb(34, 33, 33);
}
.content {
  line-height: 14px;
  word-break: normal;
  white-space: normal !important;
}
.color_items {
  width: 800px;
  height: 60px;
  position: absolute;
  top: 10px;
  left: 500px;
}
</style>
