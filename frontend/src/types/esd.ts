/** 與後端 PageResponse 對齊的分頁數據。 */
export interface PageResponse<T> {
  list: T[]
  total: number
}

/** 人員擴展檔及實時持有數量。 */
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

/** 資產主檔及生命周期展示信息。 */
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
