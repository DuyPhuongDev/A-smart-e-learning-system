package com.hcmut.lms.coursedelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.coursedelivery", "com.hcmut.lms.common"})
@EnableDiscoveryClient
public class CourseDeliveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseDeliveryServiceApplication.class, args);
    }
}

