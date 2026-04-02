package com.hcmut.lms.notification.service;

import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationType;

public interface NotificationMetricsService {

    void incrementCreated(NotificationType type);

    void incrementDelivery(NotificationChannel channel, DeliveryStatus status);

    void recordDeliveryLatency(NotificationChannel channel, long latencyMs);

    void incrementDlq();

    void setUnreadCount(String role, long value);

    void setSchedulerLagSeconds(long value);
}
