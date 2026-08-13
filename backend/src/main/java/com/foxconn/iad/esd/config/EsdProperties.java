package com.foxconn.iad.esd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "esd")
public class EsdProperties {

    /** 當前業務服務名，用於健康檢查和服務治理標識。 */
    private String serviceName = "platform-esd-server";
    /** Nacos 中統一用戶服務的服務名。 */
    private String platformSystemService = "platform-system-server";
}
