import { get } from './base'
const BASE = '/data/article.json'
// const BASE = 'http://34.96.156.43:7070/articles/search'

export const search = (params = {}) => {
  return get(BASE, params)
}
