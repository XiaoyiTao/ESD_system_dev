import axios from 'axios'

// 所有前端請求統一從 /admin-api 出口發出，具體由開發代理或網關分流。
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/admin-api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  // 平台登錄成功後保存的令牌只用於平台接口；ESD 服務正式環境使用網關轉發的 login-user。
  const token = localStorage.getItem('esd_access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  // 本地調試頭只附加到 ESD 業務請求，避免把調試身份誤傳給統一平台。
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
    // 令牌失效時清理本地緩存，下一次請求交由平台登錄流程重新建立上下文。
    if (error.response?.status === 401) {
      localStorage.removeItem('esd_access_token')
    }
    return Promise.reject(error)
  },
)

export default request
