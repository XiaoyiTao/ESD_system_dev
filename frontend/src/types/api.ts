/** ESD 健康接口的返回數據。 */
export interface HealthResponse {
  status: string
  service: string
  platformSystemService: string
  version: string
}

/** 後續 Dashboard 狀態統計接口復用的摘要結構。 */
export interface AssetStatusSummary {
  code: number
  name: string
  count: number
}
