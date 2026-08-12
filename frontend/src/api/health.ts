import request from './request'
import type { HealthResponse } from '../types/api'

/** 查询 ESD 业务服务健康状态，用于首页连接状态展示。 */
export function getEsdHealth() {
  return request.get<HealthResponse>('/esd/health')
}
