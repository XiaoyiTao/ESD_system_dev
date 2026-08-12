export interface HealthResponse {
  status: string
  service: string
  platformSystemService: string
  version: string
}

export interface AssetStatusSummary {
  code: number
  name: string
  count: number
}

