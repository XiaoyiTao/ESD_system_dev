# platform-system RPC 擴展

ESD 不複製平台帳號、密碼或組織主數據。人員綁定時通過平台已有的 `AdminUserApi#getUser` 和 `DeptApi#getDept` 校驗用戶、部門狀態，並只保存業務快照。

## 必須補充的用戶字段

文件：

`platform-system/platform-module-system-api/src/main/java/com/foxconn/iad/module/system/api/user/dto/AdminUserRespDTO.java`

在 `AdminUserRespDTO` 中增加以下只讀字段：

```java
@Schema(description = "用戶帳號", requiredMode = Schema.RequiredMode.REQUIRED, example = "E10001")
private String username;

@Schema(description = "廠區，多個值使用逗號分隔", example = "ZZ,WH")
private String site;
```

`AdminUserApiImpl#getUser` 已使用 `BeanUtils.toBean(user, AdminUserRespDTO.class)`，因此無需改實現類；平台重新構建並發布 `platform-module-system-api` 和 `platform-module-system-server` 後，ESD 的 Feign 適配即可讀取這兩個字段。

## 已確認的平台接口

- `GET /rpc-api/system/user/get?id={id}`：用戶 ID、帳號、姓名、狀態、部門 ID、廠區。
- `GET /rpc-api/system/dept/get?id={id}`：部門名稱和狀態。
- `GET /admin-api/system/auth/get-permission-info`：當前登入用戶和可選廠區列表。

平台狀態枚舉 `CommonStatusEnum.ENABLE` 為 `0`，ESD 會拒絕停用用戶和部門。

## 聯調順序

1. 發布上述 DTO 擴展，並確認平台 RPC 服務可被 ESD 通過 Nacos 發現。
2. 為平台用戶配置 `site`，例如 `ZZ,WH`，並在前端登入權限接口返回同樣的廠區範圍。
3. 啟動 ESD 服務，使用人員綁定接口驗證用戶和部門快照。

ESD 本地調試模式只繞過登入上下文解析，不繞過平台用戶和部門校驗；需要真實平台 RPC，或在自動化測試中 Mock `PlatformUserClient` 與 `PlatformDeptClient`。
