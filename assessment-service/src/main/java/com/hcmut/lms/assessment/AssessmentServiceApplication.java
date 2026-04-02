package com.hcmut.lms.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.assessment", "com.hcmut.lms.common"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class AssessmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssessmentServiceApplication.class, args);
    }
}


