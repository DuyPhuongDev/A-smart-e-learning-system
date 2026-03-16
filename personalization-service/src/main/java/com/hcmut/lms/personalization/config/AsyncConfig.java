package com.hcmut.lms.personalization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        int cpu = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Math.max(4, cpu));
        executor.setMaxPoolSize(Math.max(8, cpu * 2));
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("valuation-async-");
        executor.initialize();
        return executor;
    }
}

