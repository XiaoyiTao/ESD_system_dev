import request from './request'
import type { Asset, PageResult } from '../types/esd'

/** 資產列表的服務端分頁和過濾條件。 */
export interface AssetPageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
  assetType?: number
  lifecycleStatus?: number
}

/** 新增資產請求體；資產狀態由後端固定初始化為可用庫存。 */
export interface AssetCreatePayload {
  assetCode: string
  siteCode: string
  assetType: number
  colorCode: string
  sizeCode: string
}

/** 查詢資產主檔分頁數據。 */
export function getAssetPage(params: AssetPageQuery) {
  return request.get<{ data: PageResult<Asset> }>('/esd/assets', { params })
}

/** 新增一件資產，不允許前端指定生命周期和版本。 */
export function createAsset(payload: AssetCreatePayload) {
  return request.post<{ data: number }>('/esd/assets', payload)
}
