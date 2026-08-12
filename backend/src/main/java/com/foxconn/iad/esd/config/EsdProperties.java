package com.foxconn.iad.esd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "esd")
public class EsdProperties {

    /** 当前业务服务名，用于健康检查和服务治理标识。 */
    private String serviceName = "platform-esd-server";
    /** Nacos 中统一用户服务的服务名。 */
    private String platformSystemService = "platform-system-server";
}
