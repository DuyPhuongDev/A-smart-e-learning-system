package com.hcmut.lms.usermanagement.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Web configuration for User Management Service
 */
@Configuration
public class WebConfig {

    /**
     * Register the UserContextFilter to run for all requests
     */
    @Bean
    public FilterRegistrationBean<UserContextFilter> userContextFilterRegistration(UserContextFilter filter) {
        FilterRegistrationBean<UserContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        registration.setName("userContextFilter");
        return registration;
    }
}
