package com.hcmut.lms.coursemanagement.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "curriculum")
public class CurriculumFallbackConfig {
    private String fallbackIntakeYearCode = "22";
}