import request from './request'
import type { HealthResponse } from '../types/api'

/** 查詢 ESD 業務服務健康狀態，用於首頁連接狀態展示。 */
export function getEsdHealth() {
  return request.get<HealthResponse>('/esd/health')
}
