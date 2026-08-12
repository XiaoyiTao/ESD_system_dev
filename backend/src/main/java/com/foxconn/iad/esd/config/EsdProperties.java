package com.foxconn.iad.esd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "esd")
public class EsdProperties {

    private String serviceName = "platform-esd-server";
    private String platformSystemService = "platform-system-server";
}

