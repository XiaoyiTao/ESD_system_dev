import request from './request'

/** 平台认证接口返回的当前用户摘要。 */
export interface PlatformUser {
  id: number
  nickname: string
  username: string
  avatar?: string
  deptId?: number
}

/** 平台权限接口返回的登录上下文；sites 是可切换厂区范围。 */
export interface PlatformPermissionInfo {
  user: PlatformUser
  roles: string[]
  permissions: string[]
  sites: string[]
}

/** 获取统一平台的用户、角色、权限和厂区范围。 */
export function getPlatformPermissionInfo() {
  return request.get<{ code: number; msg: string; data: PlatformPermissionInfo }>('/system/auth/get-permission-info')
}
