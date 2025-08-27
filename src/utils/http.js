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
    
    // 处理token失效的情况（401状态码）
    if (resp && resp.status === 401) {
      // 清除本地存储的token和用户信息
      localStorage.removeItem('token')
      
      // 使用Element Plus的消息提示（如果在Vue组件外，需要单独引入）
      if (window.ElMessage) {
        window.ElMessage.error('登录已失效，请重新登录')
      } else {
        alert('登录已失效，请重新登录')
      }
      
      // 跳转到登录页面
      if (window.router) {
        window.router.push('/login')
      } else {
        window.location.href = '/login'
      }
      
      return Promise.reject({ code: 401, message: '登录已失效，请重新登录' })
    }
    
    if (resp && resp.data) {
      return Promise.reject(resp.data)
    }
    return Promise.reject({ code: -1, message: error.message || '网络错误' })
  }
)

export default http






