package com.foxconn.iad.esd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EsdApplication {

    /**
     * ESD 业务服务启动入口。
     *
     * <p>平台账号认证由统一后台负责，本服务通过 Feign 调用平台主数据 RPC。</p>
     */
    public static void main(String[] args) {
        SpringApplication.run(EsdApplication.class, args);
    }
}
