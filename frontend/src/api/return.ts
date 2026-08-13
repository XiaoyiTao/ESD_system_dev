import request from './request'
import type { PageResponse, ReturnRecord } from '../types/esd'

/** 回收列表的服務端分頁和過濾條件。 */
export interface ReturnPageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
}

/** 單筆回收請求體，後續處置 1 直接入庫，2 送洗。 */
export interface ReturnCreatePayload {
  siteCode: string
  assetCode: string
  disposition: number
  returnerName?: string
  returnDate: string
}

/** 查詢回收記錄分頁數據。 */
export function getReturnPage(params: ReturnPageQuery) {
  return request.get<{ data: PageResponse<ReturnRecord> }>('/esd/returns/page', { params })
}

/** 單筆回收：發放中 -> 庫存 或 發放中 -> 待送洗。 */
export function createReturn(payload: ReturnCreatePayload) {
  return request.post<{ data: number }>('/esd/returns', payload)
}
