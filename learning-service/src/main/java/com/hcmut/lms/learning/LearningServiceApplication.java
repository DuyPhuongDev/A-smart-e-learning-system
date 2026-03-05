package com.hcmut.lms.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.learning", "com.hcmut.lms.common"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
public class LearningServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningServiceApplication.class, args);
    }
}


