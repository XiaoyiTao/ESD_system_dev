/** ESD 健康接口的返回数据。 */
export interface HealthResponse {
  status: string
  service: string
  platformSystemService: string
  version: string
}

/** 后续 Dashboard 状态统计接口复用的摘要结构。 */
export interface AssetStatusSummary {
  code: number
  name: string
  count: number
}
