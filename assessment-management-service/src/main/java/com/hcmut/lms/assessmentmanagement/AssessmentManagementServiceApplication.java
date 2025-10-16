package com.hcmut.lms.assessmentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.assessmentmanagement", "com.hcmut.lms.common"})
@EnableDiscoveryClient
public class AssessmentManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssessmentManagementServiceApplication.class, args);
    }
}

