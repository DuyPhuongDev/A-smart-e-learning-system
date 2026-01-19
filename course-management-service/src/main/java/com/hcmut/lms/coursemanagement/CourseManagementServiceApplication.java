package com.hcmut.lms.coursemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.coursemanagement", "com.hcmut.lms.common"})
@EnableFeignClients
//@EnableDiscoveryClient
public class CourseManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseManagementServiceApplication.class, args);
    }
}

