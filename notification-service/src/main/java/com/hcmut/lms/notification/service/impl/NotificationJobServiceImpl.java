package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.notification.client.UserManagementInternalClient;
import com.hcmut.lms.notification.client.dto.InternalResolveUsersRequest;
import com.hcmut.lms.notification.client.dto.InternalUserSummaryResponse;
import com.hcmut.lms.notification.config.NotificationProperties;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationOutboxEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.DeferReason;
import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.OutboxStatus;
import com.hcmut.lms.notification.repository.NotificationDeliveryLogRepository;
import com.hcmut.lms.notification.repository.NotificationDlqRepository;
import com.hcmut.lms.notification.repository.NotificationOutboxRepository;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.enums.NotificationOutboxEventType;
import com.hcmut.lms.notification.service.NotificationAsyncEmailService;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationEmailDeliveryWorker;
import com.hcmut.lms.notification.service.NotificationJobService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationJobServiceImpl implements NotificationJobService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Saigon");

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationOutboxRepository notificationOutboxRepository;
    private final NotificationDlqRepository notificationDlqRepository;
    private final NotificationDeliveryLogRepository notificationDeliveryLogRepository;
    private final NotificationDispatchService notificationDispatchService;
    private final NotificationOutboxService notificationOutboxService;
    private final NotificationAsyncEmailService notificationAsyncEmailService;
    private final NotificationEmailDeliveryWorker notificationEmailDeliveryWorker;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserManagementInternalClient userManagementInternalClient;
    private final NotificationProperties notificationProperties;

    @Override
    @Transactional
    public void processScheduledNotifications() {
        Instant now = Instant.now();
        List<NotificationEntity> due = notificationRepository.findByStatusAndScheduledAtLessThanEqual(
                NotificationStatus.SCHEDULED, now);

        for (NotificationEntity entity : due) {
            try {
                entity.setStatus(NotificationStatus.SENT);
                notificationRepository.save(entity);
                notificationOutboxService.enqueue(entity, NotificationOutboxEventType.SCHEDULER_SENT);
                notificationDispatchService.dispatch(entity);
            } catch (Exception ex) {
                log.error("Failed to process scheduled notification {}: {}", entity.getId(), ex.getMessage(), ex);
                entity.setStatus(NotificationStatus.FAILED);
                notificationRepository.save(entity);
            }
        }
    }

    @Override
    @Transactional
    public void processEmailRetries() {
        List<UserNotificationEntity> candidates = userNotificationRepository.findRetryCandidates(
                NotificationChannel.EMAIL,
                List.of(DeliveryStatus.PENDING, DeliveryStatus.FAILED),
                Instant.now()
        );

        for (UserNotificationEntity candidate : candidates) {
            if (candidate.getDeferReason() == DeferReason.DIGEST) {
                continue;
            }
            attemptEmailDelivery(candidate);
        }
    }

    @Override
    @Transactional
    public void processDailyDigest() {
        List<UserNotificationEntity> candidates = userNotificationRepository.findDigestCandidates(
                DeferReason.DIGEST,
                LocalDate.now(DEFAULT_ZONE)
        );

        Map<UUID, List<UserNotificationEntity>> grouped = candidates.stream()
                .collect(Collectors.groupingBy(UserNotificationEntity::getUserId));

        for (Map.Entry<UUID, List<UserNotificationEntity>> entry : grouped.entrySet()) {
            UUID userId = entry.getKey();
            List<UserNotificationEntity> userItems = entry.getValue();
            if (userItems.isEmpty()) {
                continue;
            }

            String subject = "WeLearning - Daily notification digest";
            String body = buildDigestEmailBody(userItems);
            long startedAt = System.currentTimeMillis();
            try {
                notificationEmailDeliveryWorker.sendDigest(userId, subject, body);
                Instant deliveredAt = Instant.now();
                for (UserNotificationEntity item : userItems) {
                    item.setDeliveryStatus(DeliveryStatus.DELIVERED);
                    item.setDeliveredAt(deliveredAt);
                    item.setDeliveryAttempts(item.getDeliveryAttempts() + 1);
                    item.setNextAttemptAt(null);
                    item.setLastError(null);
                    item.setDeferReason(null);
                    item.setDigestBucketDate(null);
                    userNotificationRepository.save(item);
                    notificationOutboxService.logDelivery(item, "smtp-gmail", "digest", "250",
                            System.currentTimeMillis() - startedAt, true);
                }
            } catch (Exception ex) {
                for (UserNotificationEntity item : userItems) {
                    int attempts = item.getDeliveryAttempts() + 1;
                    item.setDeliveryStatus(DeliveryStatus.FAILED);
                    item.setDeliveryAttempts(attempts);
                    item.setLastError(ex.getMessage());
                    item.setDeferReason(DeferReason.RETRY);
                    item.setNextAttemptAt(nextRetryTime(attempts));
                    userNotificationRepository.save(item);

                    if (item.getNextAttemptAt() == null) {
                        notificationOutboxService.moveUserNotificationToDlq(item, ex.getMessage());
                    }

                    notificationOutboxService.logDelivery(item, "smtp-gmail", "digest", "500",
                            System.currentTimeMillis() - startedAt, false);
                }
            }
        }
    }

    @Override
    @Transactional
    public void processRetention() {
        Instant threshold = Instant.now().minus(Duration.ofDays(notificationProperties.getRetentionDays()));
        long logs = notificationDeliveryLogRepository.deleteByCreatedAtBefore(threshold);
        long dlq = notificationDlqRepository.deleteByCreatedAtBefore(threshold);
        long outbox = notificationOutboxRepository.deleteByCreatedAtBefore(threshold);
        long userNoti = userNotificationRepository.deleteByCreatedAtBefore(threshold);
        long notifications = notificationRepository.deleteByCreatedAtBefore(threshold);
        log.info(
                "Retention cleanup completed. deleted logs={}, dlq={}, outbox={}, userNotifications={}, notifications={}",
                logs, dlq, outbox, userNoti, notifications
        );
    }

    @Override
    @Transactional
    public void processOutbox() {
        List<NotificationOutboxEntity> events = notificationOutboxRepository
                .findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(OutboxStatus.PENDING, Instant.now());

        for (NotificationOutboxEntity event : events) {
            try {
                kafkaTemplate.send(
                                notificationProperties.getKafka().getOutboundTopic(),
                                event.getEventKey(),
                                event.getPayload())
                        .get(5, TimeUnit.SECONDS);
                event.setStatus(OutboxStatus.SENT);
                event.setAttempts(event.getAttempts() + 1);
                event.setLastError(null);
                notificationOutboxRepository.save(event);
            } catch (Exception ex) {
                int attempts = event.getAttempts() + 1;
                event.setAttempts(attempts);
                event.setLastError(ex.getMessage());
                if (attempts >= 4) {
                    event.setStatus(OutboxStatus.FAILED);
                    notificationOutboxRepository.save(event);
                    notificationOutboxService.moveToDlq(event, ex.getMessage());
                } else {
                    event.setNextAttemptAt(Instant.now().plus(Duration.ofMinutes(1L * attempts)));
                    notificationOutboxRepository.save(event);
                }
            }
        }
    }

    private void attemptEmailDelivery(UserNotificationEntity userNotification) {
        UUID id = userNotification.getId();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    notificationAsyncEmailService.deliverWhenReady(id);
                }
            });
        } else {
            notificationAsyncEmailService.deliverWhenReady(id);
        }
    }

    private Instant nextRetryTime(int attempts) {
        List<Integer> retryDelays = parseRetryDelays();
        if (attempts <= 0 || attempts > retryDelays.size()) {
            return null;
        }
        return Instant.now().plus(Duration.ofMinutes(retryDelays.get(attempts - 1)));
    }

    private List<Integer> parseRetryDelays() {
        return Arrays.stream(notificationProperties.getEmail().getRetryDelaysMinutes().split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(Integer::parseInt)
                .toList();
    }

    private String buildDigestEmailBody(List<UserNotificationEntity> items) {
        StringBuilder builder = new StringBuilder();
        builder.append("Xin chào,\n\n");
        builder.append("Bạn có ").append(items.size()).append(" thông báo mới:\n\n");
        for (UserNotificationEntity item : items) {
            builder.append("- ")
                    .append(item.getNotification().getTitle())
                    .append(": ")
                    .append(item.getNotification().getContent())
                    .append("\n");
        }
        builder.append("\nTrân trọng,\nWeLearning LMS");
        return builder.toString();
    }
}
