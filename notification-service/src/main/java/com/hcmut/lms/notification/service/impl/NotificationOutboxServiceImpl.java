package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.notification.entity.NotificationDeliveryLogEntity;
import com.hcmut.lms.notification.entity.NotificationDlqEntity;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationOutboxEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.OutboxStatus;
import com.hcmut.lms.notification.enums.NotificationOutboxEventType;
import com.hcmut.lms.notification.repository.NotificationDeliveryLogRepository;
import com.hcmut.lms.notification.repository.NotificationDlqRepository;
import com.hcmut.lms.notification.repository.NotificationOutboxRepository;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.util.JsonCodec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationOutboxServiceImpl implements NotificationOutboxService {

    private final NotificationOutboxRepository notificationOutboxRepository;
    private final NotificationDlqRepository notificationDlqRepository;
    private final NotificationDeliveryLogRepository notificationDeliveryLogRepository;
    private final JsonCodec jsonCodec;

    @Override
    @Transactional
    public void enqueue(NotificationEntity entity, NotificationOutboxEventType eventType) {
        String eventTypeValue = eventType.getValue();
        NotificationOutboxEntity outbox = new NotificationOutboxEntity();
        outbox.setId(UUID.randomUUID());
        outbox.setAggregateType("notification");
        outbox.setAggregateId(entity.getId());
        outbox.setEventType(eventTypeValue);
        outbox.setEventKey(entity.getId().toString() + ":" + eventTypeValue);
        outbox.setPayload(jsonCodec.toJsonString(Map.of(
                "eventId", UUID.randomUUID().toString(),
                "eventType", eventTypeValue,
                "occurredAt", Instant.now().toString(),
                "sourceService", "notification-service",
                "version", "1.0",
                "data", Map.of(
                        "notificationId", entity.getId().toString(),
                        "status", entity.getStatus().name(),
                        "type", entity.getType().name()
                )
        )));
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setAttempts(0);
        outbox.setNextAttemptAt(Instant.now());
        notificationOutboxRepository.save(outbox);
    }

    @Override
    @Transactional
    public void moveToDlq(NotificationOutboxEntity outbox, String errorMessage) {
        NotificationDlqEntity dlq = new NotificationDlqEntity();
        dlq.setId(UUID.randomUUID());
        dlq.setOutboxId(outbox.getId());
        dlq.setEventType(outbox.getEventType());
        dlq.setEventKey(outbox.getEventKey());
        dlq.setPayload(outbox.getPayload());
        dlq.setErrorMessage(errorMessage);
        dlq.setAttempts(outbox.getAttempts());
        dlq.setCreatedAt(Instant.now());
        notificationDlqRepository.save(dlq);
    }

    @Override
    @Transactional
    public void moveUserNotificationToDlq(UserNotificationEntity userNotification, String errorMessage) {
        NotificationDlqEntity dlq = new NotificationDlqEntity();
        dlq.setId(UUID.randomUUID());
        dlq.setOutboxId(null);
        dlq.setEventType("delivery.email");
        dlq.setEventKey(userNotification.getId().toString());
        dlq.setPayload(jsonCodec.toJsonString(Map.of(
                "userNotificationId", userNotification.getId(),
                "notificationId", userNotification.getNotification().getId(),
                "userId", userNotification.getUserId()
        )));
        dlq.setErrorMessage(errorMessage);
        dlq.setAttempts(userNotification.getDeliveryAttempts());
        dlq.setCreatedAt(Instant.now());
        notificationDlqRepository.save(dlq);
    }

    @Override
    @Transactional
    public void logDelivery(
            UserNotificationEntity userNotification,
            String provider,
            String responsePayload,
            String responseCode,
            long latencyMs,
            boolean success
    ) {
        NotificationDeliveryLogEntity logEntity = new NotificationDeliveryLogEntity();
        logEntity.setId(UUID.randomUUID());
        logEntity.setUserNotification(userNotification);
        logEntity.setProvider(provider);
        logEntity.setChannel(userNotification.getChannel());
        logEntity.setRequestPayload(userNotification.getNotification().getContent());
        logEntity.setResponsePayload(responsePayload);
        logEntity.setResponseCode(responseCode);
        logEntity.setLatencyMs(Math.max(latencyMs, 0));
        logEntity.setSuccess(success);
        logEntity.setCreatedAt(Instant.now());
        notificationDeliveryLogRepository.save(logEntity);
    }
}
