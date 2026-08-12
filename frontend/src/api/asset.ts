import request from './request'
import type { Asset, PageResponse } from '../types/esd'

/** 资产列表的服务端分页和过滤条件。 */
export interface AssetPageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
  assetType?: number
  lifecycleStatus?: number
}

/** 新增资产请求体；资产状态由后端固定初始化为可用库存。 */
export interface AssetCreatePayload {
  assetCode: string
  siteCode: string
  assetType: number
  colorCode: string
  sizeCode: string
}

/** 查询资产主档分页数据。 */
export function getAssetPage(params: AssetPageQuery) {
  return request.get<{ data: PageResponse<Asset> }>('/esd/assets', { params })
}

/** 新增一件资产，不允许前端指定生命周期和版本。 */
export function createAsset(payload: AssetCreatePayload) {
  return request.post<{ data: number }>('/esd/assets', payload)
}
