package com.hcmut.lms.assessmentexecution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.assessmentexecution", "com.hcmut.lms.common"})
@EnableDiscoveryClient
public class AssessmentExecutionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssessmentExecutionServiceApplication.class, args);
    }
}

