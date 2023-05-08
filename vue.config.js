module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://34.96.156.43:7070/', // 路径指向本地主机地址及端口号
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/' // 路径转发代理
        }
      }
    }
  }
}
