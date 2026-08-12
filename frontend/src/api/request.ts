import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/admin-api',
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('esd_access_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
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
    if (error.response?.status === 401) {
      localStorage.removeItem('esd_access_token')
    }
    return Promise.reject(error)
  },
)

export default request
