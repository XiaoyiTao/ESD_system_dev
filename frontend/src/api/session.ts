import request from './request'

/** 平台認證接口返回的當前用戶摘要。 */
export interface PlatformUser {
  id: number
  nickname: string
  username: string
  avatar?: string
  deptId?: number
}

/** 平台權限接口返回的登錄上下文；sites 是可切換廠區範圍。 */
export interface PlatformPermissionInfo {
  user: PlatformUser
  roles: string[]
  permissions: string[]
  sites: string[]
}

/** 獲取統一平台的用戶、角色、權限和廠區範圍。 */
export function getPlatformPermissionInfo() {
  return request.get<{ code: number; msg: string; data: PlatformPermissionInfo }>('/system/auth/get-permission-info')
}
