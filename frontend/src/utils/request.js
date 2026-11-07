import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, setToken, removeToken } from './storage'
import router from '../router'

let isRefreshing = false
let refreshSubscribers = []

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// ✅ 响应拦截器 - 自动刷新Token
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res
    }
    // 如果是 401，尝试刷新 Token
    if (res.code === 401) {
      return handleTokenExpired(response.config)
    }
    ElMessage.error(res.msg || res.message || '请求失败')
    return Promise.reject(res)
  },
  error => {
    if (error.response?.status === 401) {
      return handleTokenExpired(error.config)
    }
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

// ✅ Token 过期处理
function handleTokenExpired(failedConfig) {
  if (!isRefreshing) {
    isRefreshing = true

    refreshToken().then(newToken => {
      isRefreshing = false
      // 重试所有排队请求
      refreshSubscribers.forEach(cb => cb(newToken))
      refreshSubscribers = []
      // 重试当前请求
      failedConfig.headers.Authorization = `Bearer ${newToken}`
      return request(failedConfig)
    }).catch(() => {
      isRefreshing = false
      refreshSubscribers = []
      ElMessage.error('登录已过期，请重新登录')
      removeToken()
      router.push('/login')
      return Promise.reject()
    })
  }

  return new Promise(resolve => {
    refreshSubscribers.push(token => {
      failedConfig.headers.Authorization = `Bearer ${token}`
      resolve(request(failedConfig))
    })
  })
}

// ✅ 刷新Token
async function refreshToken() {
  const token = getToken()
  if (!token) {
    return Promise.reject(new Error('No token'))
  }

  try {
    const response = await axios.post(
      'http://localhost:8080/auth/refresh',
      {},
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    )
    
    if (response.data.code === 200) {
      const newToken = response.data.data.token
      setToken(newToken)
      return newToken
    } else {
      return Promise.reject(new Error('Refresh failed'))
    }
  } catch (error) {
    return Promise.reject(error)
  }
}

export default request