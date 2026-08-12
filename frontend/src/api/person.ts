import request from './request'
import type { PageResponse, PersonProfile } from '../types/esd'

export interface PersonPageQuery {
  siteCode: string
  pageNo: number
  pageSize: number
  keyword?: string
  esdStatus?: number
}

export interface PersonCreatePayload {
  siteCode: string
  platformUserId: number
  supervisorUserId?: number
  floorCode?: string
  shiftCode?: string
}

export function getPersonPage(params: PersonPageQuery) {
  return request.get<{ data: PageResponse<PersonProfile> }>('/esd/person-profiles', { params })
}

export function createPerson(payload: PersonCreatePayload) {
  return request.post<{ data: number }>('/esd/person-profiles', payload)
}

