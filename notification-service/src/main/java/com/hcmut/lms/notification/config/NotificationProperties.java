package com.hcmut.lms.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private int retentionDays = 180;
    private Scheduler scheduler = new Scheduler();
    private Email email = new Email();
    private Kafka kafka = new Kafka();
    private Websocket websocket = new Websocket();

    @Data
    public static class Scheduler {
        private long pollIntervalMs = 30000;
        private long outboxIntervalMs = 5000;
        private long retryIntervalMs = 60000;
        private String digestCron = "0 15 0 * * *";
        private String retentionCron = "0 30 1 * * *";
    }

    @Data
    public static class Email {
        private String from = "no-reply@welearning.local";
        private String retryDelaysMinutes = "1,5,15";
        private LocalTime quietHoursStart = LocalTime.of(22, 0);
        private LocalTime quietHoursEnd = LocalTime.of(7, 0);
    }

    @Data
    public static class Kafka {
        private String topicsPattern = "lms\\.events\\..*";
        private String outboundTopic = "lms.events.notification.created";
    }

    @Data
    public static class Websocket {
        private List<String> allowedOrigins = List.of("http://localhost:3000");
    }
}
