package com.hcmut.lms.notification.scheduler;

import com.hcmut.lms.notification.service.NotificationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationApplicationService notificationApplicationService;
    @Scheduled(
            fixedDelayString = "${notification.scheduler.poll-interval-ms:30000}",
            initialDelayString = "${notification.scheduler.poll-interval-ms:30000}"
    )
    public void processScheduledNotifications() {
        notificationApplicationService.processScheduledNotifications();
    }

    @Scheduled(
            fixedDelayString = "${notification.scheduler.outbox-interval-ms:5000}",
            initialDelayString = "${notification.scheduler.outbox-interval-ms:5000}"
    )
    public void processOutbox() {
        notificationApplicationService.processOutbox();
    }

    @Scheduled(
            fixedDelayString = "${notification.scheduler.retry-interval-ms:60000}",
            initialDelayString = "${notification.scheduler.retry-interval-ms:60000}"
    )
    public void processEmailRetries() {
        notificationApplicationService.processEmailRetries();
    }

    @Scheduled(cron = "${notification.scheduler.digest-cron:0 15 0 * * *}")
    public void processDailyDigest() {
        notificationApplicationService.processDailyDigest();
    }

    @Scheduled(cron = "${notification.scheduler.retention-cron:0 30 1 * * *}")
    public void processRetention() {
        notificationApplicationService.processRetention();
    }

    @Scheduled(
            fixedDelayString = "${notification.scheduler.unread-metrics-interval-ms:60000}",
            initialDelayString = "${notification.scheduler.unread-metrics-interval-ms:60000}"
    )
    public void refreshUnreadMetrics() {
        notificationApplicationService.refreshUnreadMetrics();
    }
}
