package com.hcmut.lms.personalization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.personalization", "com.hcmut.lms.common"})
@EnableDiscoveryClient
public class StudentPersonalizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentPersonalizationServiceApplication.class, args);
    }
}

