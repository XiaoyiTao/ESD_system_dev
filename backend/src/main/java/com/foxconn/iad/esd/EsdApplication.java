package com.foxconn.iad.esd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EsdApplication {

    /**
     * ESD 業務服務啟動入口。
     *
     * <p>平台帳號認證由統一後台負責，本服務通過 Feign 調用平台主數據 RPC。</p>
     */
    public static void main(String[] args) {
        SpringApplication.run(EsdApplication.class, args);
    }
}
