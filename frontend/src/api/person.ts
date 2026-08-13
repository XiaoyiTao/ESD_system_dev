import request from './request'
import type { PageResponse, PersonProfile } from '../types/esd'

/** 人員檔列表的服務端分頁和過濾條件。 */
export interface PersonPageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
  esdStatus?: number
}

/** 綁定平台用戶時保存的本地業務屬性。 */
export interface PersonCreatePayload {
  siteCode: string
  platformUserId: number
  supervisorUserId?: number
  floorCode?: string
  shiftCode?: string
}

/** 查詢 ESD 人員擴展檔。 */
export function getPersonPage(params: PersonPageQuery) {
  return request.get<{ data: PageResponse<PersonProfile> }>('/esd/person-profiles', { params })
}

/** 將平台用戶綁定為當前廠區的 ESD 業務人員。 */
export function createPerson(payload: PersonCreatePayload) {
  return request.post<{ data: number }>('/esd/person-profiles', payload)
}
