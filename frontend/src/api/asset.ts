import request from './request'
import type { Asset, PageResponse } from '../types/esd'

export interface AssetPageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
  assetType?: number
  lifecycleStatus?: number
}

export interface AssetCreatePayload {
  assetCode: string
  siteCode: string
  assetType: number
  colorCode: string
  sizeCode: string
}

export function getAssetPage(params: AssetPageQuery) {
  return request.get<{ data: PageResponse<Asset> }>('/esd/assets', { params })
}

export function createAsset(payload: AssetCreatePayload) {
  return request.post<{ data: number }>('/esd/assets', payload)
}

