package com.foxconn.iad.esd.platform;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${esd.platform-system-service:platform-system-server}", contextId = "platformDeptClient",
        path = "/rpc-api/system/dept")
public interface PlatformDeptClient {

    @GetMapping("/get")
    PlatformRpcResult<PlatformDeptResp> getDept(@RequestParam("id") Long id);
}
