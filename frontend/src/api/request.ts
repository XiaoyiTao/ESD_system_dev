import axios from 'axios'

// 所有前端请求统一从 /admin-api 出口发出，具体由开发代理或网关分流。
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/admin-api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  // 平台登录成功后保存的令牌只用于平台接口；ESD 服务正式环境使用网关转发的 login-user。
  const token = localStorage.getItem('esd_access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  // 本地调试头只附加到 ESD 业务请求，避免把调试身份误传给统一平台。
  if (config.url?.startsWith('/esd/')) {
    const debugUserId = import.meta.env.VITE_ESD_DEBUG_USER_ID
    const debugSites = import.meta.env.VITE_ESD_DEBUG_SITES
    if (debugUserId && debugSites) {
      config.headers['X-ESD-Debug-User-Id'] = debugUserId
      config.headers['X-ESD-Debug-Sites'] = debugSites
    }
  }
  return config
})

request.interceptors.response.use(
  (response) => response,
  (error) => {
    // 令牌失效时清理本地缓存，下一次请求交由平台登录流程重新建立上下文。
    if (error.response?.status === 401) {
      localStorage.removeItem('esd_access_token')
    }
    return Promise.reject(error)
  },
)

export default request
