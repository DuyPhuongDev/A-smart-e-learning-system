package com.hcmut.lms.notification.service;

import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationOutboxEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.NotificationOutboxEventType;

public interface NotificationOutboxService {

    void enqueue(NotificationEntity entity, NotificationOutboxEventType eventType);

    void moveToDlq(NotificationOutboxEntity outbox, String errorMessage);

    void moveUserNotificationToDlq(UserNotificationEntity userNotification, String errorMessage);

    void logDelivery(
            UserNotificationEntity userNotification,
            String provider,
            String responsePayload,
            String responseCode,
            long latencyMs,
            boolean success
    );
}
