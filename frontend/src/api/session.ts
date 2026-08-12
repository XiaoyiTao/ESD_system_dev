import request from './request'

export interface PlatformUser {
  id: number
  nickname: string
  username: string
  avatar?: string
  deptId?: number
}

export interface PlatformPermissionInfo {
  user: PlatformUser
  roles: string[]
  permissions: string[]
  sites: string[]
}

export function getPlatformPermissionInfo() {
  return request.get<{ code: number; msg: string; data: PlatformPermissionInfo }>('/system/auth/get-permission-info')
}
