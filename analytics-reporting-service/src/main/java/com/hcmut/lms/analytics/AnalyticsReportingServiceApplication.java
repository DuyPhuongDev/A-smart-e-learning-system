package com.hcmut.lms.analytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.hcmut.lms.analytics", "com.hcmut.lms.common"})
@EnableDiscoveryClient
public class AnalyticsReportingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticsReportingServiceApplication.class, args);
    }
}

