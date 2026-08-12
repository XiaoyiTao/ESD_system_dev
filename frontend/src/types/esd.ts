/** 与后端 PageResponse 对齐的分页数据。 */
export interface PageResponse<T> {
  list: T[]
  total: number
}

/** 人员扩展档及实时持有数量。 */
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

/** 资产主档及生命周期展示信息。 */
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
