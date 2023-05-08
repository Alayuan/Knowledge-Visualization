export const BIGSIZE = 70
const MIDSIZE = 40
const SMALLSIZE = 20
const SMALL_NODE_COUNT = 8
// ES6 导出模块机制
export const handleData = (DescriptorRecords) => {
  const { nodes, bigNodes } = _handleArray(DescriptorRecords)
  const { categories, nameArray } = _initCategories(nodes)
  const links = _initLinks(nodes)
  const renderData = {
    nodes,
    categories,
    nameArray,
    links,
    bigNodes
  }
  return renderData
}

export const handleDataOffline = ({ data, categories, links }) => {
  return {
    nodes: data,
    categories,
    links
  }
}

const _initLinks = (nodes) => {
  const links = []
  for (const key in nodes) {
    const parentId = nodes[key].parentId
    if (parentId) {
      for (const innerKey in nodes) {
        if (nodes[innerKey]?.uid === parentId) {
          links.push({
            source: `${nodes[key].id}`,
            target: `${nodes[innerKey].id}`
          })
        }
      }
    }
  }
  return links
}

const _initCategories = (nodes) => {
  const categories = []
  const nameArray = []
  nodes.map((item, index) => {
    categories.push({
      name: item.category
    })
    if (item.symbolSize === BIGSIZE) {
      nameArray.push({
        name: item.name
      })
    }
  })
  return {
    categories,
    nameArray
  }
}

const _handleArray = (DescriptorRecords) => {
  const rawNodes = []
  const bigNodes = []
  for (const key in DescriptorRecords) {
    const TopX = Math.random() * 300
    const TopY = Math.random() * 300
    const superItem = DescriptorRecords[key]
    const isBig = superItem.conceptList ? BIGSIZE : MIDSIZE
    const tNode = {
      ...superItem,
      name: superItem.descriptorName,
      symbolSize: isBig,
      uid: `${key}`,
      category: superItem.descriptorName,
      x: TopX,
      y: TopY
    };
    (isBig && bigNodes.length < 5) && bigNodes.push(tNode)
    rawNodes.push(tNode)
    if (superItem.conceptList) {
      const scc = superItem.conceptList
      for (const midKey in scc) {
        const MidX = TopX + Math.random() * 200
        const Midy = TopY + Math.random() * 200
        const termList = scc[midKey].termList
        rawNodes.push({
          ...scc[midKey],
          name: scc[midKey].conceptName,
          uid: `${key}_${midKey}`,
          parentId: `${key}`,
          symbolSize: MIDSIZE,
          category: superItem.descriptorName,
          x: MidX,
          y: Midy
        })
        if (termList) {
          const SmallX = MidX + Math.random() * 300
          const Smally = MidX + Math.random() * 300
          for (const innerKey in termList) {
            if (innerKey > SMALL_NODE_COUNT) {
              break
            }
            rawNodes.push({
              ...termList[innerKey],
              parentId: `${key}_${midKey}`,
              symbolSize: SMALLSIZE,
              category: superItem.descriptorName,
              name: termList[innerKey].string,
              x: SmallX,
              y: Smally
            })
          }
        }
      }
    }
  }
  const nodes = rawNodes.map((item, index) => {
    return {
      ...item,
      id: index,
      label: {
        show: item.symbolSize !== SMALLSIZE ? true : (index % 2 === 1),
        width: 80,
        overflow: 'break',
        fontWeight: _isMainLabel(item) ? 'bolder' : 'normal',
        color: _isMainLabel(item) ? '#111' : '#111'
      }
    }
  })
  return {
    nodes,
    bigNodes
  }
}
const _isMainLabel = (item) => item.symbolSize === BIGSIZE
// const _isShowLabel = (list) => list.length > 0

export const useRenderToolTipEffect = (params, nodes) => {
  const { data: node, name: mName } = params
  const { symbolSize, scopeNote = '', name } = node

  // 连线间关系
  const handleRenderRelationsInLine = (nodes, source, target) => {
    // return nodes[source].name + '>' + nodes[target].name
    return nodes[target].name + '>' + nodes[source].name
  }
  if (symbolSize === MIDSIZE) {
    // const res = hanldeNote(name, scopeNote)
    return (`
      <div class="tooltip-wordwrap">
        <div class="title">${mName}</div>
        </br>
        <div class="content">${scopeNote}</div>
      </div> 
    `)
  } else if (symbolSize === SMALLSIZE || BIGSIZE) {
    const { source, target } = params.data
    return name || handleRenderRelationsInLine(nodes, source, target)
  }
}
