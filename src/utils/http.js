import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (resp) => {
    const data = resp.data
    // 统一处理后端Result结构
    if (data && typeof data.code !== 'undefined' && data.code !== 200) {
      // 抛出业务错误，让调用方捕获
      return Promise.reject({ code: data.code, message: data.message || '请求失败' })
    }
    return data
  },
  (error) => {
    const resp = error.response
    if (resp && resp.data) {
      return Promise.reject(resp.data)
    }
    return Promise.reject({ code: -1, message: error.message || '网络错误' })
  }
)

export default http






