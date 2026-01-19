package com.hcmut.lms.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.usermanagement", "com.hcmut.lms.common"})
@ComponentScan(
    basePackages = {"com.hcmut.lms.usermanagement", "com.hcmut.lms.common"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = com.hcmut.lms.common.exception.GlobalExceptionHandler.class
    )
)
@EnableDiscoveryClient
@EnableFeignClients
public class UserManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementServiceApplication.class, args);
    }
}

