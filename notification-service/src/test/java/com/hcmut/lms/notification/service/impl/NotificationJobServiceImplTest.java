package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.notification.config.NotificationProperties;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationOutboxEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.*;
import com.hcmut.lms.notification.repository.*;
import com.hcmut.lms.notification.service.NotificationAsyncEmailService;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationEmailDeliveryWorker;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationJobServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserNotificationRepository userNotificationRepository;
    @Mock private NotificationOutboxRepository notificationOutboxRepository;
    @Mock private NotificationDlqRepository notificationDlqRepository;
    @Mock private NotificationDeliveryLogRepository notificationDeliveryLogRepository;
    @Mock private NotificationDispatchService notificationDispatchService;
    @Mock private NotificationOutboxService notificationOutboxService;
    @Mock private NotificationAsyncEmailService notificationAsyncEmailService;
    @Mock private NotificationEmailDeliveryWorker notificationEmailDeliveryWorker;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @Mock private com.hcmut.lms.notification.client.UserManagementInternalClient userManagementInternalClient;
    @Mock private NotificationProperties notificationProperties;

    @InjectMocks
    private NotificationJobServiceImpl notificationJobService;

    @Test
    void processScheduledNotifications_shouldDispatchDueNotifications() {
        setDefaultProperties();
        NotificationEntity due = createScheduledNotification();
        when(notificationRepository.findByStatusAndScheduledAtLessThanEqual(eq(NotificationStatus.SCHEDULED), any()))
                .thenReturn(List.of(due));
        doNothing().when(notificationDispatchService).dispatch(due);

        notificationJobService.processScheduledNotifications();

        assertThat(due.getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(notificationDispatchService).dispatch(due);
    }

    @Test
    void processScheduledNotifications_shouldMarkFailed_onException() {
        setDefaultProperties();
        NotificationEntity due = createScheduledNotification();
        when(notificationRepository.findByStatusAndScheduledAtLessThanEqual(eq(NotificationStatus.SCHEDULED), any()))
                .thenReturn(List.of(due));
        doThrow(new RuntimeException("dispatch error")).when(notificationDispatchService).dispatch(due);

        notificationJobService.processScheduledNotifications();

        assertThat(due.getStatus()).isEqualTo(NotificationStatus.FAILED);
    }

    @Test
    void processScheduledNotifications_shouldDoNothing_whenEmpty() {
        setDefaultProperties();
        when(notificationRepository.findByStatusAndScheduledAtLessThanEqual(any(), any()))
                .thenReturn(List.of());

        notificationJobService.processScheduledNotifications();

        verify(notificationDispatchService, never()).dispatch(any());
    }

    @Test
    void processEmailRetries_shouldRetryPendingCandidates() {
        setDefaultProperties();
        UserNotificationEntity candidate = createEmailCandidate(DeliveryStatus.PENDING);
        when(userNotificationRepository.findRetryCandidates(
                eq(NotificationChannel.EMAIL), anyList(), any()))
                .thenReturn(List.of(candidate));

        notificationJobService.processEmailRetries();

        verify(notificationAsyncEmailService).deliverWhenReady(candidate.getId());
    }

    @Test
    void processEmailRetries_shouldSkipDigestDeferred() {
        setDefaultProperties();
        UserNotificationEntity candidate = createEmailCandidate(DeliveryStatus.PENDING);
        candidate.setDeferReason(DeferReason.DIGEST);
        when(userNotificationRepository.findRetryCandidates(
                eq(NotificationChannel.EMAIL), anyList(), any()))
                .thenReturn(List.of(candidate));

        notificationJobService.processEmailRetries();

        verify(notificationAsyncEmailService, never()).deliverWhenReady(any());
    }

    @Test
    void processDailyDigest_shouldGroupAndSend() {
        setDefaultProperties();
        UUID userId = UUID.randomUUID();
        UserNotificationEntity item = createDigestCandidate(userId);
        when(userNotificationRepository.findDigestCandidates(eq(DeferReason.DIGEST), any()))
                .thenReturn(List.of(item));

        notificationJobService.processDailyDigest();

        verify(notificationEmailDeliveryWorker).sendDigest(eq(userId), anyString(), anyString());
        assertThat(item.getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERED);
        assertThat(item.getDeferReason()).isNull();
    }

    @Test
    void processDailyDigest_shouldMarkFailed_onException() {
        setDefaultProperties();
        UUID userId = UUID.randomUUID();
        UserNotificationEntity item = createDigestCandidate(userId);
        when(userNotificationRepository.findDigestCandidates(eq(DeferReason.DIGEST), any()))
                .thenReturn(List.of(item));
        doThrow(new RuntimeException("send failed")).when(notificationEmailDeliveryWorker)
                .sendDigest(eq(userId), anyString(), anyString());

        notificationJobService.processDailyDigest();

        assertThat(item.getDeliveryStatus()).isEqualTo(DeliveryStatus.FAILED);
        assertThat(item.getDeferReason()).isEqualTo(DeferReason.RETRY);
    }

    @Test
    void processRetention_shouldCleanupOldRecords() {
        notificationJobService.processRetention();

        verify(notificationDeliveryLogRepository).deleteByCreatedAtBefore(any());
        verify(notificationDlqRepository).deleteByCreatedAtBefore(any());
        verify(notificationOutboxRepository).deleteByCreatedAtBefore(any());
        verify(userNotificationRepository).deleteByCreatedAtBefore(any());
        verify(notificationRepository).deleteByCreatedAtBefore(any());
    }

    @Test
    void processOutbox_shouldSendPendingEvents() throws Exception {
        setDefaultProperties();
        NotificationOutboxEntity event = createOutboxEvent();
        when(notificationOutboxRepository.findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                eq(OutboxStatus.PENDING), any()))
                .thenReturn(List.of(event));

        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        notificationJobService.processOutbox();

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.SENT);
    }

    @Test
    void processOutbox_shouldRetry_onFailure() throws Exception {
        setDefaultProperties();
        NotificationOutboxEntity event = createOutboxEvent();
        when(notificationOutboxRepository.findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                eq(OutboxStatus.PENDING), any()))
                .thenReturn(List.of(event));

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        notificationJobService.processOutbox();

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(event.getAttempts()).isEqualTo(1);
    }

    @Test
    void processOutbox_shouldMoveToDlq_whenMaxAttemptsReached() throws Exception {
        setDefaultProperties();
        NotificationOutboxEntity event = createOutboxEvent();
        event.setAttempts(3);
        when(notificationOutboxRepository.findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                eq(OutboxStatus.PENDING), any()))
                .thenReturn(List.of(event));

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("kafka down"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(future);

        notificationJobService.processOutbox();

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.FAILED);
        verify(notificationOutboxService).moveToDlq(eq(event), anyString());
    }

    @Test
    void nextRetryTime_shouldReturnNull_whenExceedsMaxRetries() {
        setDefaultProperties();
        Object result = ReflectionTestUtils.invokeMethod(notificationJobService, "nextRetryTime", 4);
        assertThat(result).isNull();
    }

    @Test
    void nextRetryTime_shouldReturnTime_whenValidAttempt() {
        setDefaultProperties();
        Instant before = Instant.now();
        Object result = ReflectionTestUtils.invokeMethod(notificationJobService, "nextRetryTime", 1);
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(Instant.class);
        assertThat((Instant) result).isAfter(before);
    }

    @Test
    void buildDigestEmailBody_shouldContainNotificationTitles() {
        setDefaultProperties();
        NotificationEntity notif = new NotificationEntity();
        notif.setTitle("Test Title");
        notif.setContent("Test Content");

        UserNotificationEntity item = new UserNotificationEntity();
        item.setNotification(notif);

        String body = ReflectionTestUtils.invokeMethod(notificationJobService, "buildDigestEmailBody", List.of(item));

        assertThat(body).contains("Test Title");
        assertThat(body).contains("Test Content");
        assertThat(body).contains("WeLearning");
    }

    private void setDefaultProperties() {
        NotificationProperties.Email emailProps = new NotificationProperties.Email();
        emailProps.setRetryDelaysMinutes("1,5,15");
        NotificationProperties.Kafka kafkaProps = new NotificationProperties.Kafka();
        kafkaProps.setOutboundTopic("lms.events.notification.created");
        lenient().when(notificationProperties.getEmail()).thenReturn(emailProps);
        lenient().when(notificationProperties.getKafka()).thenReturn(kafkaProps);
        lenient().when(notificationProperties.getRetentionDays()).thenReturn(180);
    }

    private NotificationEntity createScheduledNotification() {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Scheduled");
        entity.setContent("Content");
        entity.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
        entity.setPriority(NotificationPriority.MEDIUM);
        entity.setStatus(NotificationStatus.SCHEDULED);
        entity.setTargetMode(TargetMode.ALL);
        entity.setTargetPayload("{}");
        entity.setChannels("[\"IN_APP\"]");
        entity.setMetadata("{}");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }

    private UserNotificationEntity createEmailCandidate(DeliveryStatus status) {
        NotificationEntity notif = new NotificationEntity();
        notif.setId(UUID.randomUUID());
        notif.setTitle("Email Test");

        UserNotificationEntity entity = new UserNotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setNotification(notif);
        entity.setUserId(UUID.randomUUID());
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setDeliveryStatus(status);
        entity.setDeliveryAttempts(0);
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    private UserNotificationEntity createDigestCandidate(UUID userId) {
        NotificationEntity notif = new NotificationEntity();
        notif.setId(UUID.randomUUID());
        notif.setTitle("Digest Item");
        notif.setContent("Digest Content");

        UserNotificationEntity entity = new UserNotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setNotification(notif);
        entity.setUserId(userId);
        entity.setChannel(NotificationChannel.EMAIL);
        entity.setDeliveryStatus(DeliveryStatus.PENDING);
        entity.setDeliveryAttempts(0);
        entity.setDeferReason(DeferReason.DIGEST);
        entity.setDigestBucketDate(LocalDate.now());
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    private NotificationOutboxEntity createOutboxEvent() {
        NotificationOutboxEntity event = new NotificationOutboxEntity();
        event.setId(UUID.randomUUID());
        event.setAggregateType("notification");
        event.setAggregateId(UUID.randomUUID());
        event.setEventType("notification.admin.created");
        event.setEventKey(UUID.randomUUID() + ":notification.admin.created");
        event.setPayload("{}");
        event.setStatus(OutboxStatus.PENDING);
        event.setAttempts(0);
        event.setNextAttemptAt(Instant.now().minusSeconds(60));
        event.setCreatedAt(Instant.now());
        return event;
    }
}
