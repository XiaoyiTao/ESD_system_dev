package com.foxconn.iad.esd.platform;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 平台用戶 RPC 契約。ESD 服務只讀取主數據，不保存帳號密碼。
 */
@FeignClient(name = "${esd.platform-system-service:platform-system-server}", contextId = "platformUserClient",
        path = "/rpc-api/system/user")
public interface PlatformUserClient {

    /** 通過平台用戶 ID 查詢用戶主數據。 */
    @GetMapping("/get")
    PlatformRpcResult<PlatformUserResp> getUser(@RequestParam("id") Long id);
}
