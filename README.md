# ESD System

靜電衣鞋管理系統，按《開發設計書》分版本開發。

## 版本規劃

| 版本 | 交付內容 |
| --- | --- |
| `0.1.0` | 前後端工程骨架、狀態機契約、平台整合契約、MySQL 初始化腳本、健康檢查 |
| `0.2.0` | 人員擴展檔、資產入庫/查詢/詳情、平台用戶/部門 RPC 適配、統一登入廠區上下文 |
| `0.3.0` | 發放、回收、送洗、完成清洗及 MySQL 併發控制 |
| `0.4.0` | 報警中心、庫存 Dashboard、報廢/遺失及撤銷 |
| `0.5.0` | Excel 原子導入導出、六類報表 |
| `1.0.0` | 備份恢復、清空、安全加固、整合測試和上線文檔 |

每個版本使用同名 Git 分支，例如 `0.1.0`、`0.2.0`。

## 工程結構

```text
backend/     Spring Boot 2.7 / JDK 8 服務
frontend/    Vue 3 / TypeScript / Vite 管理端
db/mysql/    MySQL 初始化和版本腳本
```

## 本地要求

- JDK 8
- Maven 3.8+
- Node.js 18 LTS 或以上
- MySQL 8.0+

## 啟動後端

```bash
cd backend
mvn spring-boot:run
```

默認端口 `48082`，健康接口：

```text
GET http://localhost:48082/actuator/health
GET http://localhost:48082/admin-api/esd/health
```

## 初始化 MySQL

第一階段只提供結構腳本，未自動執行，避免誤改現有資料庫：

```bash
mysql -u root -p < db/mysql/V1__esd_core.sql
```

腳本建立獨立資料庫 `platform_esd` 及 ESD 業務表，不建立平台帳號表，不保存密碼。

## 啟動前端

```bash
cd frontend
npm install
npm run dev
```

前端默認訪問 `http://localhost:5173`。API 地址可由 `VITE_API_BASE_URL` 配置，默認為 `/admin-api`。

## 平台整合

ESD 服務預留平台用戶查詢契約，後續版本接入 `platform-system-server-master` 的 RPC：

- `GET /rpc-api/system/user/get`
- `GET /rpc-api/system/dept/get`（平台已有）
- `/system/auth/get-permission-info` 登入權限、菜單和廠區範圍

平台現有 `AdminUserApi#getUser` 已滿足用戶 ID 查詢，但 DTO 未返回帳號和廠區字段。ESD 需要這兩個只讀字段來生成業務快照，具體補丁見 [`docs/platform-system-rpc-extension.md`](docs/platform-system-rpc-extension.md)。

## 安全提示

不要把資料庫密碼、Nacos 密碼、令牌或正式環境密鑰提交到 Git。正式配置使用環境變量或 Nacos 受控配置。
