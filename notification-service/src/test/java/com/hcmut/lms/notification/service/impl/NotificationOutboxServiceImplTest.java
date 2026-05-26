package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.notification.entity.NotificationDeliveryLogEntity;
import com.hcmut.lms.notification.entity.NotificationDlqEntity;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationOutboxEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationOutboxEventType;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.repository.NotificationDeliveryLogRepository;
import com.hcmut.lms.notification.repository.NotificationDlqRepository;
import com.hcmut.lms.notification.repository.NotificationOutboxRepository;
import com.hcmut.lms.notification.util.JsonCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationOutboxServiceImplTest {

    @Mock private NotificationOutboxRepository notificationOutboxRepository;
    @Mock private NotificationDlqRepository notificationDlqRepository;
    @Mock private NotificationDeliveryLogRepository notificationDeliveryLogRepository;
    @Mock private JsonCodec jsonCodec;

    @InjectMocks
    private NotificationOutboxServiceImpl notificationOutboxService;

    @Test
    void enqueue_shouldCreateAndSaveOutboxEntity() {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(UUID.randomUUID());
        notification.setStatus(NotificationStatus.SENT);
        notification.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationOutboxRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationOutboxService.enqueue(notification, NotificationOutboxEventType.ADMIN_CREATED);

        ArgumentCaptor<NotificationOutboxEntity> captor = ArgumentCaptor.forClass(NotificationOutboxEntity.class);
        verify(notificationOutboxRepository).save(captor.capture());
        NotificationOutboxEntity saved = captor.getValue();
        assertThat(saved.getAggregateId()).isEqualTo(notification.getId());
        assertThat(saved.getEventType()).isEqualTo(NotificationOutboxEventType.ADMIN_CREATED.getValue());
    }

    @Test
    void moveToDlq_shouldCreateDlqFromOutbox() {
        UUID outboxId = UUID.randomUUID();
        NotificationOutboxEntity outbox = new NotificationOutboxEntity();
        outbox.setId(outboxId);
        outbox.setEventType("test.event");
        outbox.setEventKey("key-1");
        outbox.setPayload("{}");
        outbox.setAttempts(3);

        notificationOutboxService.moveToDlq(outbox, "test error");

        ArgumentCaptor<NotificationDlqEntity> captor = ArgumentCaptor.forClass(NotificationDlqEntity.class);
        verify(notificationDlqRepository).save(captor.capture());
        NotificationDlqEntity dlq = captor.getValue();
        assertThat(dlq.getOutboxId()).isEqualTo(outboxId);
        assertThat(dlq.getEventType()).isEqualTo("test.event");
        assertThat(dlq.getErrorMessage()).isEqualTo("test error");
        assertThat(dlq.getAttempts()).isEqualTo(3);
    }

    @Test
    void moveUserNotificationToDlq_shouldCreateDlqAndSave() {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(UUID.randomUUID());
        UserNotificationEntity userNoti = new UserNotificationEntity();
        userNoti.setId(UUID.randomUUID());
        userNoti.setUserId(UUID.randomUUID());
        userNoti.setNotification(notification);
        userNoti.setDeliveryAttempts(2);
        when(jsonCodec.toJsonString(any())).thenReturn("{}");

        notificationOutboxService.moveUserNotificationToDlq(userNoti, "delivery failed");

        ArgumentCaptor<NotificationDlqEntity> captor = ArgumentCaptor.forClass(NotificationDlqEntity.class);
        verify(notificationDlqRepository).save(captor.capture());
        assertThat(captor.getValue().getErrorMessage()).isEqualTo("delivery failed");
    }

    @Test
    void logDelivery_shouldCreateDeliveryLog() {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(UUID.randomUUID());
        notification.setContent("Hello");
        UserNotificationEntity userNoti = new UserNotificationEntity();
        userNoti.setId(UUID.randomUUID());
        userNoti.setChannel(NotificationChannel.EMAIL);
        userNoti.setNotification(notification);

        notificationOutboxService.logDelivery(userNoti, "smtp-gmail", "250 OK", "250", 100L, true);

        ArgumentCaptor<NotificationDeliveryLogEntity> captor = ArgumentCaptor.forClass(NotificationDeliveryLogEntity.class);
        verify(notificationDeliveryLogRepository).save(captor.capture());
        NotificationDeliveryLogEntity log = captor.getValue();
        assertThat(log.getProvider()).isEqualTo("smtp-gmail");
        assertThat(log.getResponseCode()).isEqualTo("250");
        assertThat(log.getLatencyMs()).isEqualTo(100L);
        assertThat(log.isSuccess()).isTrue();
    }

    @Test
    void logDelivery_shouldHandleNegativeLatency() {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(UUID.randomUUID());
        notification.setContent("Hello");
        UserNotificationEntity userNoti = new UserNotificationEntity();
        userNoti.setId(UUID.randomUUID());
        userNoti.setChannel(NotificationChannel.IN_APP);
        userNoti.setNotification(notification);

        notificationOutboxService.logDelivery(userNoti, "provider", "resp", "200", -50L, false);

        ArgumentCaptor<NotificationDeliveryLogEntity> captor = ArgumentCaptor.forClass(NotificationDeliveryLogEntity.class);
        verify(notificationDeliveryLogRepository).save(captor.capture());
        assertThat(captor.getValue().getLatencyMs()).isEqualTo(0L);
        assertThat(captor.getValue().isSuccess()).isFalse();
    }
}
