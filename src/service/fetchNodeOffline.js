import { get } from './base'

export default function getNode (params) {
  return get('/data/model-v.json', params)
}
