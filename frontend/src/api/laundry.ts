import request from './request'
import type { LaundryRecord } from '../types/esd'

/** 查詢待送洗與清洗中的進行中清洗列表。 */
export function getActiveLaundries(siteCode: string) {
  return request.get<{ data: LaundryRecord[] }>('/esd/laundries/active-page', { params: { siteCode } })
}

/** 送洗登記：待送洗 -> 清洗中。 */
export function startLaundry(id: number, siteCode: string) {
  return request.put<{ data: boolean }>(`/esd/laundries/${id}/start`, null, { params: { siteCode } })
}

/** 完成清洗：清洗中 -> 庫存，清洗次數 +1。 */
export function completeLaundry(id: number, siteCode: string) {
  return request.put<{ data: boolean }>(`/esd/laundries/${id}/complete`, null, { params: { siteCode } })
}
