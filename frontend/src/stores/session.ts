import { defineStore } from 'pinia'
import { getPlatformPermissionInfo, type PlatformUser } from '../api/session'

/** 前端保存的統一登錄上下文，不保存平台密碼。 */
interface SessionState {
  user?: PlatformUser
  sites: string[]
  currentSite: string
  loading: boolean
  platformConnected: boolean
}

/** 僅開發環境讀取本地廠區配置，生產環境沒有默認廠區。 */
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
     * 初始化平台會話。
     *
     * <p>平台可用時以服務端返回為準；開發環境平台不可用時才使用調試配置，
     * 這樣頁面能夠獨立聯調 ESD API，但不會改變生產鑑權。</p>
     */
    async initialize() {
      try {
        const response = await getPlatformPermissionInfo()
        if (response.data.code !== 0 || !response.data.data?.user) {
          throw new Error(response.data.msg || '平台登錄信息不可用')
        }
        // 服務端返回的廠區範圍是權限邊界，前端只能在其中選擇當前操作廠區。
        this.user = response.data.data.user
        this.sites = response.data.data.sites || []
        this.platformConnected = true
      } catch {
        // 不把平台失敗偽裝成正式登錄成功；僅開發模式展示本地調試用戶。
        this.sites = debugSites()
        const debugUserId = Number(import.meta.env.VITE_ESD_DEBUG_USER_ID || 1)
        if (import.meta.env.DEV) {
          this.user = { id: debugUserId, username: 'local-debug', nickname: '本地調試用戶' }
        }
      } finally {
        // 記住上次選擇，但必須重新檢查它仍屬於本次登錄的權限範圍。
        const storedSite = sessionStorage.getItem('esd_current_site')
        this.currentSite = storedSite && this.sites.includes(storedSite) ? storedSite : (this.sites[0] || '')
        this.loading = false
      }
    },
    /** 切換業務廠區並通知依賴 currentSite 的頁面刷新數據。 */
    selectSite(siteCode: string) {
      if (!this.sites.includes(siteCode)) return
      this.currentSite = siteCode
      sessionStorage.setItem('esd_current_site', siteCode)
    },
  },
})
