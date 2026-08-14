/** 與後端 PageResult 對齊的分頁數據。 */
export interface PageResult<T> {
  list: T[]
  total: number
}

/** 人員擴展檔及實時持有數量（持有數量僅供報警內部使用，前端不展示）。 */
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
  latestIssue?: LatestIssue
  latestLaundry?: LatestLaundry
}

/** 資產詳情中的最近一次發放快照。 */
export interface LatestIssue {
  issueDate: string
  employeeNo: string
  employeeName: string
  issueOperatorName?: string
  returnDate?: string
}

/** 資產詳情中的最近一次清洗快照。 */
export interface LatestLaundry {
  sendTime?: string
  completeTime?: string
}

/** 發放記錄，一個資產多次發放全部顯示。 */
export interface IssueRecord {
  id: number
  assetCode: string
  assetType: number
  employeeUserId: number
  employeeNo: string
  employeeName: string
  supervisorName?: string
  deptName?: string
  floorCode?: string
  issueOperatorName?: string
  issueDate: string
  recordStatus: number
  closeTime?: string
}

/** 回收記錄。 */
export interface ReturnRecord {
  id: number
  assetCode: string
  assetType: number
  employeeNo: string
  employeeName: string
  returnerName?: string
  receiverName?: string
  returnDate: string
  disposition: number
  dispositionName: string
}

/** 進行中的清洗記錄（待送洗 / 清洗中）。 */
export interface LaundryRecord {
  id: number
  assetCode: string
  assetType: number
  colorCode: string
  sizeCode: string
  lifecycleStatus: number
  lifecycleStatusName: string
  sendTime?: string
  sendOperatorName?: string
}
