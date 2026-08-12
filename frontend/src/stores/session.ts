import { defineStore } from 'pinia'
import { getPlatformPermissionInfo, type PlatformUser } from '../api/session'

/** 前端保存的统一登录上下文，不保存平台密码。 */
interface SessionState {
  user?: PlatformUser
  sites: string[]
  currentSite: string
  loading: boolean
  platformConnected: boolean
}

/** 仅开发环境读取本地厂区配置，生产环境没有默认厂区。 */
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
    /**
     * 初始化平台会话。
     *
     * <p>平台可用时以服务端返回为准；开发环境平台不可用时才使用调试配置，
     * 这样页面能够独立联调 ESD API，但不会改变生产鉴权。</p>
     */
    async initialize() {
      try {
        const response = await getPlatformPermissionInfo()
        if (response.data.code !== 0 || !response.data.data?.user) {
          throw new Error(response.data.msg || '平台登录信息不可用')
        }
        // 服务端返回的厂区范围是权限边界，前端只能在其中选择当前操作厂区。
        this.user = response.data.data.user
        this.sites = response.data.data.sites || []
        this.platformConnected = true
      } catch {
        // 不把平台失败伪装成正式登录成功；仅开发模式展示本地调试用户。
        this.sites = debugSites()
        const debugUserId = Number(import.meta.env.VITE_ESD_DEBUG_USER_ID || 1)
        if (import.meta.env.DEV) {
          this.user = { id: debugUserId, username: 'local-debug', nickname: '本地调试用户' }
        }
      } finally {
        // 记住上次选择，但必须重新检查它仍属于本次登录的权限范围。
        const storedSite = sessionStorage.getItem('esd_current_site')
        this.currentSite = storedSite && this.sites.includes(storedSite) ? storedSite : (this.sites[0] || '')
        this.loading = false
      }
    },
    /** 切换业务厂区并通知依赖 currentSite 的页面刷新数据。 */
    selectSite(siteCode: string) {
      if (!this.sites.includes(siteCode)) return
      this.currentSite = siteCode
      sessionStorage.setItem('esd_current_site', siteCode)
    },
  },
})
