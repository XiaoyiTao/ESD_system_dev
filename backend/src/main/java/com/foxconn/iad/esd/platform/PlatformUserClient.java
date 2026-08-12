package com.foxconn.iad.esd.platform;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 平台用户 RPC 契约。ESD 服务只读取主数据，不保存账号密码。
 */
@FeignClient(name = "${esd.platform-system-service:platform-system-server}", contextId = "platformUserClient",
        path = "/rpc-api/system/user")
public interface PlatformUserClient {

    /** 通过平台用户 ID 查询用户主数据。 */
    @GetMapping("/get")
    PlatformRpcResult<PlatformUserResp> getUser(@RequestParam("id") Long id);
}
