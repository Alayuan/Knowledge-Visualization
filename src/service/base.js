import axios from 'axios'

const instance = axios.create({
  // baseURL: '/data',
  // baseURL: 'http://34.96.156.43:7070/',
  timeout: 10000
})

export const get = (url, params = {}) => {
  return new Promise((resolve, reject) => {
    instance.get(url, { params }).then(response => {
      resolve(response.data)
    }, err => {
      reject(err)
    })
  })
}
