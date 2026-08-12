package com.foxconn.iad.esd.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EsdProperties.class)
public class EsdConfiguration {
}

