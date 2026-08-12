import request from './request'
import type { PageResponse, PersonProfile } from '../types/esd'

/** 人员档列表的服务端分页和过滤条件。 */
export interface PersonPageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
  esdStatus?: number
}

/** 绑定平台用户时保存的本地业务属性。 */
export interface PersonCreatePayload {
  siteCode: string
  platformUserId: number
  supervisorUserId?: number
  floorCode?: string
  shiftCode?: string
}

/** 查询 ESD 人员扩展档。 */
export function getPersonPage(params: PersonPageQuery) {
  return request.get<{ data: PageResponse<PersonProfile> }>('/esd/person-profiles', { params })
}

/** 将平台用户绑定为当前厂区的 ESD 业务人员。 */
export function createPerson(payload: PersonCreatePayload) {
  return request.post<{ data: number }>('/esd/person-profiles', payload)
}
