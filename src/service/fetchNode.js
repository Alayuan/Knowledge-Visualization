import { get } from './base'

const URL = 'articles/associate/search'

export default function getNode (params) {
  return get(URL, params)
}
