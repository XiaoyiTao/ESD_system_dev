import request from './request'
import type { IssueRecord, PageResponse } from '../types/esd'

/** 發放列表的服務端分頁和過濾條件。 */
export interface IssuePageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
}

/** 單筆發放請求體，發放人由服務端登入上下文取得。 */
export interface IssueCreatePayload {
  siteCode: string
  employeeUserId: number
  assetCode: string
  issueDate: string
  remark?: string
}

/** 查詢發放記錄分頁數據，一個資產多次發放全部顯示。 */
export function getIssuePage(params: IssuePageQuery) {
  return request.get<{ data: PageResponse<IssueRecord> }>('/esd/issues/page', { params })
}

/** 單筆發放：庫存 -> 發放中。 */
export function createIssue(payload: IssueCreatePayload) {
  return request.post<{ data: number }>('/esd/issues', payload)
}
