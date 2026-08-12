package com.foxconn.iad.esd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EsdApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsdApplication.class, args);
    }
}

