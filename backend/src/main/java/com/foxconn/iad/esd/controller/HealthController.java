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

    private final EsdProperties properties;

    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "UP");
        result.put("service", properties.getServiceName());
        result.put("platformSystemService", properties.getPlatformSystemService());
        result.put("version", "0.1.0");
        return result;
    }
}

