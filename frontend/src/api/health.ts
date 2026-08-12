import request from './request'
import type { HealthResponse } from '../types/api'

export function getEsdHealth() {
  return request.get<HealthResponse>('/esd/health')
}

