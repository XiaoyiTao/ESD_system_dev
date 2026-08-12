export interface PageResponse<T> {
  list: T[]
  total: number
}

export interface PersonProfile {
  id: number
  siteCode: string
  platformUserId: number
  employeeNo: string
  employeeName: string
  deptId?: number
  deptName?: string
  supervisorUserId?: number
  supervisorName?: string
  floorCode?: string
  shiftCode?: string
  esdStatus: number
  garmentCount: number
  shoesCount: number
}

export interface Asset {
  id: number
  assetCode: string
  siteCode: string
  assetType: number
  colorCode: string
  sizeCode: string
  lifecycleStatus: number
  lifecycleStatusName: string
  currentHolderNo?: string
  currentHolderName?: string
  cleanCount: number
  version: number
}

