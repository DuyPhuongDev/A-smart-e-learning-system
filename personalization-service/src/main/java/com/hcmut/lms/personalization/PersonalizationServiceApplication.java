package com.hcmut.lms.personalization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.personalization", "com.hcmut.lms.common"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
public class PersonalizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalizationServiceApplication.class, args);
    }
}


