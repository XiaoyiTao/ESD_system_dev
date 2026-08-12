import { defineStore } from 'pinia'
import { getPlatformPermissionInfo, type PlatformUser } from '../api/session'

interface SessionState {
  user?: PlatformUser
  sites: string[]
  currentSite: string
  loading: boolean
  platformConnected: boolean
}

function debugSites() {
  const value = import.meta.env.VITE_ESD_DEBUG_SITES || (import.meta.env.DEV ? 'ZZ' : '')
  return value.split(',').map((site: string) => site.trim()).filter(Boolean)
}

export const useSessionStore = defineStore('session', {
  state: (): SessionState => ({
    sites: [],
    currentSite: '',
    loading: true,
    platformConnected: false,
  }),
  actions: {
    async initialize() {
      try {
        const response = await getPlatformPermissionInfo()
        if (response.data.code !== 0 || !response.data.data?.user) {
          throw new Error(response.data.msg || '平台登录信息不可用')
        }
        this.user = response.data.data.user
        this.sites = response.data.data.sites || []
        this.platformConnected = true
      } catch {
        this.sites = debugSites()
        const debugUserId = Number(import.meta.env.VITE_ESD_DEBUG_USER_ID || 1)
        if (import.meta.env.DEV) {
          this.user = { id: debugUserId, username: 'local-debug', nickname: '本地调试用户' }
        }
      } finally {
        const storedSite = sessionStorage.getItem('esd_current_site')
        this.currentSite = storedSite && this.sites.includes(storedSite) ? storedSite : (this.sites[0] || '')
        this.loading = false
      }
    },
    selectSite(siteCode: string) {
      if (!this.sites.includes(siteCode)) return
      this.currentSite = siteCode
      sessionStorage.setItem('esd_current_site', siteCode)
    },
  },
})
