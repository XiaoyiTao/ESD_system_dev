package com.foxconn.iad.esd.controller;

import com.foxconn.iad.esd.config.EsdProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin-api/esd/health")
@RequiredArgsConstructor
public class HealthController {

    /** 當前服務的運行配置。 */
    private final EsdProperties properties;

    /** 提供輕量級業務服務和平台服務標識，供網關或部署探針檢查。 */
    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("service", properties.getServiceName());
        result.put("platformSystemService", properties.getPlatformSystemService());
        result.put("version", "0.2.0");
        return result;
    }
}
