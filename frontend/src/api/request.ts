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

