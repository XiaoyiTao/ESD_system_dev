package com.foxconn.iad.esd.platform;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 平台用户 RPC 契约。按平台 API 扩展完成后启用调用，ESD 服务不保存账号密码。
 */
@FeignClient(name = "${esd.platform-system-service:platform-system-server}", path = "/rpc-api/system/user")
public interface PlatformUserClient {

    @GetMapping("/get")
    PlatformRpcResult<PlatformUserResp> getUser(@RequestParam("id") Long id);

    @GetMapping("/get-by-username")
    PlatformRpcResult<PlatformUserResp> getUserByUsername(@RequestParam("username") String username);
}
