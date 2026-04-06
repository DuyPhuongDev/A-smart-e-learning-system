package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.client.LearningInternalClient;
import com.hcmut.lms.notification.client.UserManagementInternalClient;
import com.hcmut.lms.notification.client.dto.BatchClassStudentIdsRequest;
import com.hcmut.lms.notification.client.dto.BatchClassStudentIdsResponse;
import com.hcmut.lms.notification.client.dto.ClassStudentIdsResponse;
import com.hcmut.lms.notification.client.dto.InternalResolveUsersRequest;
import com.hcmut.lms.notification.client.dto.InternalUserSummaryResponse;
import com.hcmut.lms.notification.config.NotificationProperties;
import com.hcmut.lms.notification.dto.request.AdminCreateNotificationRequest;
import com.hcmut.lms.notification.dto.request.PreferenceItemRequest;
import com.hcmut.lms.notification.dto.request.PreferencesUpsertRequest;
import com.hcmut.lms.notification.dto.request.RuleUpdateRequest;
import com.hcmut.lms.notification.dto.request.TemplateUpdateRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationListItemResponse;
import com.hcmut.lms.notification.dto.response.InboxNotificationResponse;
import com.hcmut.lms.notification.dto.response.NotificationPreferenceResponse;
import com.hcmut.lms.notification.dto.response.NotificationRuleResponse;
import com.hcmut.lms.notification.dto.response.NotificationTemplateResponse;
import com.hcmut.lms.notification.dto.response.RealtimeNotificationPayload;
import com.hcmut.lms.notification.entity.NotificationDeliveryLogEntity;
import com.hcmut.lms.notification.entity.NotificationDlqEntity;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationOutboxEntity;
import com.hcmut.lms.notification.entity.NotificationPreferenceEntity;
import com.hcmut.lms.notification.entity.NotificationRuleEntity;
import com.hcmut.lms.notification.entity.NotificationTemplateEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.DeferReason;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.OutboxStatus;
import com.hcmut.lms.notification.enums.ReadStatus;
import com.hcmut.lms.notification.enums.SendMode;
import com.hcmut.lms.notification.enums.TargetMode;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.exception.ResourceNotFoundException;
import com.hcmut.lms.common.event.NotificationTargetType;
import com.hcmut.lms.common.event.SimpleNotificationEvent;
import com.hcmut.lms.notification.repository.NotificationDeliveryLogRepository;
import com.hcmut.lms.notification.repository.NotificationDlqRepository;
import com.hcmut.lms.notification.repository.NotificationOutboxRepository;
import com.hcmut.lms.notification.repository.NotificationPreferenceRepository;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.NotificationRuleRepository;
import com.hcmut.lms.notification.repository.NotificationTemplateRepository;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.NotificationApplicationService;
import com.hcmut.lms.notification.service.NotificationAsyncEmailService;
import com.hcmut.lms.notification.service.NotificationEmailDeliveryWorker;
import com.hcmut.lms.notification.service.NotificationMetricsService;
import com.hcmut.lms.notification.service.SseNotificationBroadcaster;
import com.hcmut.lms.notification.util.JsonCodec;
import com.hcmut.lms.notification.util.RoleGuard;
import com.hcmut.lms.notification.util.TimePolicy;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationApplicationServiceImpl implements NotificationApplicationService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Bangkok");

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final NotificationDeliveryLogRepository notificationDeliveryLogRepository;
    private final NotificationOutboxRepository notificationOutboxRepository;
    private final NotificationDlqRepository notificationDlqRepository;
    private final UserManagementInternalClient userManagementInternalClient;
    private final LearningInternalClient learningInternalClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SseNotificationBroadcaster sseNotificationBroadcaster;
    private final NotificationAsyncEmailService notificationAsyncEmailService;
    private final NotificationEmailDeliveryWorker notificationEmailDeliveryWorker;
    private final NotificationMetricsService notificationMetricsService;
    private final NotificationProperties notificationProperties;
    private final JsonCodec jsonCodec;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AdminNotificationCreateResponse createAdminNotification(
            CurrentUserInfo currentUser,
            AdminCreateNotificationRequest request,
            String idempotencyKey
    ) {
        RoleGuard.requireAdmin(currentUser);
        validateCreateRequest(request);

        if (StringUtils.hasText(idempotencyKey)) {
            NotificationEntity existing = notificationRepository
                    .findByCreatedByAndIdempotencyKey(currentUser.getId(), idempotencyKey.trim())
                    .orElse(null);
            if (existing != null) {
                return AdminNotificationCreateResponse.builder()
                        .notificationId(existing.getId())
                        .status(existing.getStatus())
                        .build();
            }
        }

        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle(request.getTitle().trim());
        entity.setContent(request.getContent().trim());
        entity.setType(request.getType());
        entity.setPriority(request.getPriority());
        entity.setCreatedBy(currentUser.getId());
        entity.setTargetMode(request.getTargetMode());
        entity.setTargetPayload(jsonCodec.toJsonString(
                request.getTargetPayload() == null ? Map.of() : request.getTargetPayload()));
        entity.setChannels(jsonCodec.toJsonString(
                request.getChannels().stream().map(NotificationChannel::name).collect(Collectors.toCollection(LinkedHashSet::new))));
        entity.setMetadata(jsonCodec.toJsonString(request.getMetadata() == null ? Map.of() : request.getMetadata()));
        entity.setScheduledAt(toInstant(request.getScheduledAt()));
        entity.setExpiresAt(toInstant(request.getExpiresAt()));
        entity.setStatus(resolveInitialStatus(request.getSendMode()));
        entity.setSourceService("notification-service");
        entity.setSourceEventId(null);
        entity.setIdempotencyKey(StringUtils.hasText(idempotencyKey) ? idempotencyKey.trim() : null);

        notificationRepository.save(entity);
        notificationMetricsService.incrementCreated(entity.getType());
        enqueueOutbox(entity, "notification.admin.created");

        if (request.getSendMode() == SendMode.IMMEDIATE) {
            dispatchNotification(entity);
        }

        return AdminNotificationCreateResponse.builder()
                .notificationId(entity.getId())
                .status(entity.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminNotificationListItemResponse> getAdminNotifications(
            CurrentUserInfo currentUser,
            String status,
            String type,
            String channel,
            String from,
            String to,
            String keyword,
            int page,
            int size
    ) {
        RoleGuard.requireAdmin(currentUser);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        Specification<NotificationEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), NotificationStatus.valueOf(status.trim().toUpperCase())));
            }

            if (StringUtils.hasText(type)) {
                predicates.add(cb.equal(root.get("type"), NotificationType.valueOf(type.trim().toUpperCase())));
            }

            if (StringUtils.hasText(keyword)) {
                String normalized = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), normalized),
                        cb.like(cb.lower(root.get("content")), normalized)
                ));
            }

            if (StringUtils.hasText(channel)) {
                String normalized = "%\"" + channel.trim().toUpperCase() + "\"%";
                predicates.add(cb.like(root.get("channels"), normalized));
            }

            Instant fromInstant = parseInstantNullable(from);
            if (fromInstant != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromInstant));
            }
            Instant toInstant = parseInstantNullable(to);
            if (toInstant != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toInstant));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Page<NotificationEntity> result = notificationRepository.findAll(specification, pageable);

        List<AdminNotificationListItemResponse> content = result.getContent().stream()
                .map(this::toAdminListItem)
                .toList();

        return PageResponse.<AdminNotificationListItemResponse>builder()
                .content(content)
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .empty(result.isEmpty())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminNotificationDetailResponse getAdminNotificationDetail(CurrentUserInfo currentUser, UUID notificationId) {
        RoleGuard.requireAdmin(currentUser);
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        return toAdminDetail(entity);
    }

    @Override
    @Transactional
    public void sendNow(CurrentUserInfo currentUser, UUID notificationId) {
        RoleGuard.requireAdmin(currentUser);
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (entity.getStatus() != NotificationStatus.DRAFT && entity.getStatus() != NotificationStatus.SCHEDULED) {
            throw new BadRequestException("Only DRAFT or SCHEDULED notifications can be sent now");
        }

        entity.setStatus(NotificationStatus.SENT);
        entity.setScheduledAt(null);
        notificationRepository.save(entity);
        enqueueOutbox(entity, "notification.admin.send_now");
        dispatchNotification(entity);
    }

    @Override
    @Transactional
    public void cancel(CurrentUserInfo currentUser, UUID notificationId) {
        RoleGuard.requireAdmin(currentUser);
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (entity.getStatus() != NotificationStatus.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED notifications can be cancelled");
        }

        entity.setStatus(NotificationStatus.CANCELLED);
        notificationRepository.save(entity);
        enqueueOutbox(entity, "notification.admin.cancelled");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InboxNotificationResponse> getMyInbox(CurrentUserInfo currentUser, String status, int page, int size) {
        UUID userId = currentUser.getId();
        ReadStatus readStatus = parseReadStatus(status);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<UserNotificationEntity> result = userNotificationRepository.findInboxByUserId(userId, readStatus, Instant.now(), pageable);

        List<InboxNotificationResponse> content = result.getContent().stream()
                .map(this::toInboxResponse)
                .toList();

        return PageResponse.<InboxNotificationResponse>builder()
                .content(content)
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .empty(result.isEmpty())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(CurrentUserInfo currentUser) {
        return userNotificationRepository.countUnread(currentUser.getId(), Instant.now());
    }

    @Override
    @Transactional
    public void markRead(CurrentUserInfo currentUser, UUID userNotificationId) {
        UserNotificationEntity entity = userNotificationRepository.findByIdAndUserId(userNotificationId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User notification not found: " + userNotificationId));
        if (entity.getReadStatus() == ReadStatus.UNREAD) {
            entity.setReadStatus(ReadStatus.READ);
            entity.setReadAt(Instant.now());
            userNotificationRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void markReadAll(CurrentUserInfo currentUser) {
        userNotificationRepository.markAllRead(currentUser.getId(), Instant.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreferenceResponse> getPreferences(CurrentUserInfo currentUser) {
        return getOrCreateDefaultPreferences(currentUser.getId()).stream()
                .map(this::toPreferenceResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<NotificationPreferenceResponse> upsertPreferences(CurrentUserInfo currentUser, PreferencesUpsertRequest request) {
        UUID userId = currentUser.getId();
        List<NotificationPreferenceEntity> entities = new ArrayList<>();

        for (PreferenceItemRequest item : request.getPreferences()) {
            NotificationPreferenceEntity entity = notificationPreferenceRepository
                    .findByUserIdAndNotificationType(userId, item.getNotificationType())
                    .orElseGet(() -> {
                        NotificationPreferenceEntity created = new NotificationPreferenceEntity();
                        created.setId(UUID.randomUUID());
                        created.setUserId(userId);
                        created.setNotificationType(item.getNotificationType());
                        return created;
                    });

            entity.setInAppEnabled(Boolean.TRUE.equals(item.getInAppEnabled()));
            entity.setEmailEnabled(Boolean.TRUE.equals(item.getEmailEnabled()));
            entity.setPushEnabled(Boolean.TRUE.equals(item.getPushEnabled()));
            entity.setFrequency(item.getFrequency());
            entity.setQuietHoursStart(item.getQuietHoursStart());
            entity.setQuietHoursEnd(item.getQuietHoursEnd());
            entities.add(entity);
        }

        notificationPreferenceRepository.saveAll(entities);
        return getPreferences(currentUser);
    }

    @Override
    @Transactional
    public List<NotificationPreferenceResponse> resetPreferences(CurrentUserInfo currentUser) {
        UUID userId = currentUser.getId();
        notificationPreferenceRepository.deleteByUserId(userId);
        return getPreferences(currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationRuleResponse> getRules(CurrentUserInfo currentUser) {
        RoleGuard.requireAdmin(currentUser);
        return notificationRuleRepository.findAll().stream()
                .map(this::toRuleResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationRuleResponse updateRule(CurrentUserInfo currentUser, String eventType, RuleUpdateRequest request) {
        RoleGuard.requireAdmin(currentUser);
        validateRuleEventType(eventType);
        String normalizedEventType = eventType.trim().toLowerCase();

        NotificationRuleEntity entity = notificationRuleRepository.findById(normalizedEventType)
                .orElseGet(() -> {
                    NotificationRuleEntity created = new NotificationRuleEntity();
                    created.setEventType(normalizedEventType);
                    return created;
                });

        entity.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        entity.setNotificationType(request.getNotificationType());
        entity.setChannels(jsonCodec.toJsonString(request.getChannels().stream().map(Enum::name).toList()));
        entity.setPriority(request.getPriority());
        entity.setFrequency(request.getFrequency());
        entity.setTemplateCode(request.getTemplateCode());
        entity.setTargetMode(request.getTargetMode());
        entity.setTargetPayload(jsonCodec.toJsonString(request.getTargetPayload() == null ? Map.of() : request.getTargetPayload()));

        notificationRuleRepository.save(entity);
        return toRuleResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> getTemplates(CurrentUserInfo currentUser) {
        RoleGuard.requireAdmin(currentUser);
        return notificationTemplateRepository.findAll().stream()
                .map(this::toTemplateResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationTemplateResponse updateTemplate(CurrentUserInfo currentUser, String code, TemplateUpdateRequest request) {
        RoleGuard.requireAdmin(currentUser);
        NotificationTemplateEntity entity = notificationTemplateRepository.findById(code)
                .orElseGet(() -> {
                    NotificationTemplateEntity created = new NotificationTemplateEntity();
                    created.setCode(code);
                    created.setVersion(0);
                    return created;
                });

        entity.setTitleTemplate(request.getTitleTemplate());
        entity.setContentTemplate(request.getContentTemplate());
        entity.setChannel(request.getChannel());
        entity.setActive(Boolean.TRUE.equals(request.getActive()));
        entity.setVersion(entity.getVersion() + 1);
        entity.setUpdatedBy(currentUser.getId());

        notificationTemplateRepository.save(entity);
        return toTemplateResponse(entity);
    }

    @Override
    @Transactional
    public void processScheduledNotifications() {
        Instant now = Instant.now();
        List<NotificationEntity> due = notificationRepository.findByStatusAndScheduledAtLessThanEqual(
                NotificationStatus.SCHEDULED,
                now
        );

        long maxLagSeconds = 0L;
        for (NotificationEntity entity : due) {
            if (entity.getScheduledAt() != null && entity.getScheduledAt().isBefore(now)) {
                long lag = Duration.between(entity.getScheduledAt(), now).toSeconds();
                if (lag > maxLagSeconds) {
                    maxLagSeconds = lag;
                }
            }
        }
        notificationMetricsService.setSchedulerLagSeconds(maxLagSeconds);

        for (NotificationEntity entity : due) {
            try {
                entity.setStatus(NotificationStatus.SENT);
                notificationRepository.save(entity);
                enqueueOutbox(entity, "notification.scheduler.sent");
                dispatchNotification(entity);
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
                    logDelivery(item, "smtp-gmail", "digest", "250", System.currentTimeMillis() - startedAt, true);
                    notificationMetricsService.incrementDelivery(NotificationChannel.EMAIL, DeliveryStatus.DELIVERED);
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
                        moveUserNotificationToDlq(item, ex.getMessage());
                    }

                    logDelivery(item, "smtp-gmail", "digest", "500", System.currentTimeMillis() - startedAt, false);
                    notificationMetricsService.incrementDelivery(NotificationChannel.EMAIL, DeliveryStatus.FAILED);
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
                    moveToDlq(event, ex.getMessage());
                } else {
                    event.setNextAttemptAt(Instant.now().plus(Duration.ofMinutes(1L * attempts)));
                    notificationOutboxRepository.save(event);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void refreshUnreadMetrics() {
        Instant now = Instant.now();
        for (String role : List.of("ADMIN", "TEACHER", "STUDENT")) {
            long unread = 0L;
            List<UUID> userIds = userManagementInternalClient.resolveUsers(
                            InternalResolveUsersRequest.builder().roleNames(List.of(role)).build())
                    .stream()
                    .map(InternalUserSummaryResponse::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            if (!userIds.isEmpty()) {
                unread = userNotificationRepository.countUnreadByUserIds(userIds, now);
            }
            notificationMetricsService.setUnreadCount(role, unread);
        }
    }

    @Override
    @Transactional
    public void handleInboundEvent(String payload) {
        SimpleNotificationEvent event;
        try {
            event = objectMapper.readValue(payload, SimpleNotificationEvent.class);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid notification event payload: " + ex.getMessage());
        }

        String semantic = event.resolvedSemanticType();
        if (!StringUtils.hasText(semantic)) {
            throw new BadRequestException("semanticType (or type) is required");
        }
        if (!StringUtils.hasText(event.getMessageId())) {
            throw new BadRequestException("messageId is required");
        }

        String sourceServiceRaw = StringUtils.hasText(event.getSourceService()) ? event.getSourceService().trim() : "";
        if ("notification-service".equalsIgnoreCase(sourceServiceRaw)) {
            log.debug("Skip internal notification event sourceService={}", sourceServiceRaw);
            return;
        }

        String sourceServiceKey = sourceServiceRaw.isEmpty() ? null : sourceServiceRaw;

        NotificationRuleEntity rule = notificationRuleRepository.findById(semantic).orElse(null);
        if (rule == null || !rule.isEnabled()) {
            log.info("No enabled rule for semanticType={}", semantic);
            return;
        }

        if (notificationRepository.findBySourceServiceAndSourceEventIdAndType(
                sourceServiceKey,
                event.getMessageId(),
                rule.getNotificationType()).isPresent()) {
            log.info("Duplicate inbound event skipped semanticType={} messageId={}", semantic, event.getMessageId());
            return;
        }

        if (event.getTargetType() == null) {
            throw new BadRequestException("targetType is required");
        }

        TargetMode targetMode = mapSimpleTargetToTargetMode(event.getTargetType());
        String targetPayloadJson = buildSimpleTargetPayload(event.getTargetType(), event.getTargetId());

        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setType(rule.getNotificationType());
        entity.setPriority(rule.getPriority());
        entity.setTitle(StringUtils.hasText(event.getTitle())
                ? event.getTitle().trim()
                : fallbackEventTitle(rule.getNotificationType()));
        entity.setContent(StringUtils.hasText(event.getContent())
                ? event.getContent().trim()
                : "You have a new notification.");
        entity.setCreatedBy(null);
        entity.setSourceService(sourceServiceKey);
        entity.setSourceEventId(event.getMessageId());
        entity.setTargetMode(targetMode);
        entity.setTargetPayload(targetPayloadJson);
        entity.setChannels(rule.getChannels());
        entity.setScheduledAt(null);
        entity.setExpiresAt(null);
        entity.setStatus(NotificationStatus.SENT);

        Map<String, Object> meta = new LinkedHashMap<>();
        if (event.getMetadata() != null) {
            meta.putAll(event.getMetadata());
        }
        meta.put("semanticType", semantic);
        meta.put("occurredAt", Instant.now().toString());
        entity.setMetadata(jsonCodec.toJsonString(meta));

        notificationRepository.save(entity);
        notificationMetricsService.incrementCreated(entity.getType());
        enqueueOutbox(entity, "notification.event.ingested");
        dispatchNotification(entity);
    }

    @Override
    @Transactional
    public void recordInboundFailure(String topic, String key, String payload, String errorMessage) {
        NotificationDlqEntity dlq = new NotificationDlqEntity();
        dlq.setId(UUID.randomUUID());
        dlq.setOutboxId(null);
        dlq.setEventType("kafka.inbound." + (StringUtils.hasText(topic) ? topic : "unknown"));
        dlq.setEventKey(StringUtils.hasText(key) ? key : UUID.randomUUID().toString());
        dlq.setPayload(normalizeInboundPayload(payload));
        dlq.setErrorMessage(errorMessage);
        dlq.setAttempts(1);
        dlq.setCreatedAt(Instant.now());
        notificationDlqRepository.save(dlq);
        notificationMetricsService.incrementDlq();
    }

    private void dispatchNotification(NotificationEntity notification) {
        Set<UUID> recipients = resolveRecipients(notification.getTargetMode(), jsonCodec.toJsonNode(notification.getTargetPayload()));
        if (recipients.isEmpty()) {
            log.info("Notification {} has no resolved recipients", notification.getId());
            return;
        }

        Set<NotificationChannel> channels = parseChannelSet(notification.getChannels());
        for (UUID recipientId : recipients) {
            Map<NotificationType, NotificationPreferenceEntity> preferenceMap = getPreferenceMap(recipientId);
            NotificationPreferenceEntity preference = preferenceMap.get(notification.getType());

            for (NotificationChannel channel : channels) {
                if (!isChannelEnabled(preference, notification.getType(), channel)) {
                    continue;
                }

                NotificationFrequency frequency = preference == null ? NotificationFrequency.IMMEDIATE : preference.getFrequency();
                UserNotificationEntity userNotification = createBaseUserNotification(notification, recipientId, channel, frequency);
                routeNotificationDelivery(userNotification, preference);
            }
        }
    }

    private void routeNotificationDelivery(UserNotificationEntity userNotification, NotificationPreferenceEntity preference) {
        if (userNotification.getChannel() == NotificationChannel.IN_APP) {
            userNotification.setDeliveryStatus(DeliveryStatus.DELIVERED);
            userNotification.setDeliveredAt(Instant.now());
            userNotificationRepository.save(userNotification);
            pushRealtime(userNotification);
            notificationMetricsService.incrementDelivery(NotificationChannel.IN_APP, DeliveryStatus.DELIVERED);
            return;
        }

        if (userNotification.getChannel() != NotificationChannel.EMAIL) {
            userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
            userNotificationRepository.save(userNotification);
            return;
        }

        if (userNotification.getFrequency() == NotificationFrequency.DIGEST_DAILY) {
            userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
            userNotification.setDeferReason(DeferReason.DIGEST);
            userNotification.setDigestBucketDate(LocalDate.now(DEFAULT_ZONE));
            userNotification.setNextAttemptAt(null);
            userNotificationRepository.save(userNotification);
            return;
        }

        LocalTime quietStart = preference != null && preference.getQuietHoursStart() != null
                ? preference.getQuietHoursStart()
                : notificationProperties.getEmail().getQuietHoursStart();
        LocalTime quietEnd = preference != null && preference.getQuietHoursEnd() != null
                ? preference.getQuietHoursEnd()
                : notificationProperties.getEmail().getQuietHoursEnd();

        if (TimePolicy.isInQuietHours(Instant.now(), DEFAULT_ZONE, quietStart, quietEnd)) {
            userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
            userNotification.setDeferReason(DeferReason.QUIET_HOURS);
            userNotification.setNextAttemptAt(Instant.now().plus(Duration.ofHours(1)));
            userNotificationRepository.save(userNotification);
            return;
        }

        userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
        userNotificationRepository.save(userNotification);
        attemptEmailDelivery(userNotification);
    }

    private UserNotificationEntity createBaseUserNotification(
            NotificationEntity notification,
            UUID userId,
            NotificationChannel channel,
            NotificationFrequency frequency
    ) {
        UserNotificationEntity userNotification = new UserNotificationEntity();
        userNotification.setId(UUID.randomUUID());
        userNotification.setNotification(notification);
        userNotification.setUserId(userId);
        userNotification.setChannel(channel);
        userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
        userNotification.setReadStatus(ReadStatus.UNREAD);
        userNotification.setReadAt(null);
        userNotification.setDeliveredAt(null);
        userNotification.setDeliveryAttempts(0);
        userNotification.setLastError(null);
        userNotification.setFrequency(frequency);
        userNotification.setNextAttemptAt(Instant.now());
        return userNotificationRepository.save(userNotification);
    }

    private void attemptEmailDelivery(UserNotificationEntity userNotification) {
        if (shouldDeferForQuietHours(userNotification)) {
            userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
            userNotification.setDeferReason(DeferReason.QUIET_HOURS);
            userNotification.setNextAttemptAt(Instant.now().plus(Duration.ofHours(1)));
            userNotificationRepository.save(userNotification);
            return;
        }

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

    private boolean shouldDeferForQuietHours(UserNotificationEntity userNotification) {
        NotificationType type = userNotification.getNotification().getType();
        NotificationPreferenceEntity preference = notificationPreferenceRepository
                .findByUserIdAndNotificationType(userNotification.getUserId(), type)
                .orElse(null);

        LocalTime quietStart = preference != null && preference.getQuietHoursStart() != null
                ? preference.getQuietHoursStart()
                : notificationProperties.getEmail().getQuietHoursStart();
        LocalTime quietEnd = preference != null && preference.getQuietHoursEnd() != null
                ? preference.getQuietHoursEnd()
                : notificationProperties.getEmail().getQuietHoursEnd();

        return TimePolicy.isInQuietHours(Instant.now(), DEFAULT_ZONE, quietStart, quietEnd);
    }

    private void pushRealtime(UserNotificationEntity userNotification) {
        NotificationEntity notification = userNotification.getNotification();
        RealtimeNotificationPayload payload = RealtimeNotificationPayload.builder()
                .userNotificationId(userNotification.getId())
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .priority(notification.getPriority())
                .createdAt(notification.getCreatedAt())
                .build();

        sseNotificationBroadcaster.broadcast(userNotification.getUserId(), payload);
    }

    private Set<UUID> resolveRecipients(TargetMode targetMode, JsonNode targetPayload) {
        return switch (targetMode) {
            case ALL -> resolveAllUsers();
            case GROUP -> resolveByGroups(targetPayload);
            case COURSE -> resolveByCourse(targetPayload);
            case PROGRAM -> resolveByProgram(targetPayload);
            case USER_LIST -> resolveByUserList(targetPayload);
        };
    }

    private Set<UUID> resolveAllUsers() {
        InternalResolveUsersRequest request = InternalResolveUsersRequest.builder()
                .roleNames(List.of("ADMIN", "TEACHER", "STUDENT"))
                .build();
        return userManagementInternalClient.resolveUsers(request).stream()
                .map(it -> it.getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<UUID> resolveByGroups(JsonNode payload) {
        List<String> groups = jsonArrayText(payload, "groups");
        if (groups.isEmpty()) {
            return Collections.emptySet();
        }
        List<String> roleNames = groups.stream()
                .map(this::groupToRole)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (roleNames.isEmpty()) {
            return Collections.emptySet();
        }

        InternalResolveUsersRequest request = InternalResolveUsersRequest.builder()
                .roleNames(roleNames)
                .build();

        return userManagementInternalClient.resolveUsers(request).stream()
                .map(it -> it.getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<UUID> resolveByCourse(JsonNode payload) {
        Set<UUID> recipients = new LinkedHashSet<>();

        UUID classId = jsonUuid(payload, "classId");
        if (classId != null) {
            recipients.addAll(safeList(learningInternalClient.resolveStudentsByClass(classId)));
        }

        List<UUID> classIds = jsonUuidList(payload, "classIds");
        if (!classIds.isEmpty()) {
            BatchClassStudentIdsResponse response = learningInternalClient.resolveStudentsByClassBatch(
                    BatchClassStudentIdsRequest.builder().classIds(classIds).build()
            );
            if (response != null && response.getItems() != null) {
                for (ClassStudentIdsResponse item : response.getItems()) {
                    recipients.addAll(safeList(item.getStudentIds()));
                }
            }
        }

        UUID courseId = jsonUuid(payload, "courseId");
        if (courseId != null) {
            recipients.addAll(safeList(learningInternalClient.resolveStudentsByCourse(courseId)));
        }

        return recipients;
    }

    private Set<UUID> resolveByProgram(JsonNode payload) {
        List<UUID> specializationIds = jsonUuidList(payload, "specializationIds");
        if (specializationIds.isEmpty()) {
            return Collections.emptySet();
        }

        InternalResolveUsersRequest request = InternalResolveUsersRequest.builder()
                .specializationIds(specializationIds)
                .build();

        return userManagementInternalClient.resolveUsers(request).stream()
                .map(it -> it.getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<UUID> resolveByUserList(JsonNode payload) {
        List<UUID> userIds = jsonUuidList(payload, "userIds");
        if (!userIds.isEmpty()) {
            return new LinkedHashSet<>(userIds);
        }

        UUID userId = jsonUuid(payload, "userId");
        UUID studentId = jsonUuid(payload, "studentId");
        Set<UUID> resolved = new LinkedHashSet<>();
        if (userId != null) {
            resolved.add(userId);
        }
        if (studentId != null) {
            resolved.add(studentId);
        }
        return resolved;
    }

    private Map<NotificationType, NotificationPreferenceEntity> getPreferenceMap(UUID userId) {
        return getOrCreateDefaultPreferences(userId).stream()
                .collect(Collectors.toMap(NotificationPreferenceEntity::getNotificationType, Function.identity()));
    }

    private List<NotificationPreferenceEntity> getOrCreateDefaultPreferences(UUID userId) {
        List<NotificationPreferenceEntity> existing = notificationPreferenceRepository.findByUserId(userId);
        if (!existing.isEmpty()) {
            return existing;
        }

        List<NotificationPreferenceEntity> defaults = Arrays.stream(NotificationType.values())
                .map(type -> {
                    NotificationPreferenceEntity pref = new NotificationPreferenceEntity();
                    pref.setId(UUID.randomUUID());
                    pref.setUserId(userId);
                    pref.setNotificationType(type);
                    pref.setInAppEnabled(true);
                    pref.setEmailEnabled(defaultEmailEnabled(type));
                    pref.setPushEnabled(false);
                    pref.setFrequency(NotificationFrequency.IMMEDIATE);
                    pref.setQuietHoursStart(notificationProperties.getEmail().getQuietHoursStart());
                    pref.setQuietHoursEnd(notificationProperties.getEmail().getQuietHoursEnd());
                    return pref;
                })
                .toList();

        return notificationPreferenceRepository.saveAll(defaults);
    }

    private boolean isChannelEnabled(
            NotificationPreferenceEntity preference,
            NotificationType type,
            NotificationChannel channel
    ) {
        if (preference == null) {
            return switch (channel) {
                case IN_APP -> true;
                case EMAIL -> defaultEmailEnabled(type);
                case PUSH -> false;
            };
        }

        return switch (channel) {
            case IN_APP -> preference.isInAppEnabled();
            case EMAIL -> preference.isEmailEnabled();
            case PUSH -> preference.isPushEnabled();
        };
    }

    private boolean defaultEmailEnabled(NotificationType type) {
        return EnumSet.of(
                NotificationType.DEADLINE_REMINDER,
                NotificationType.SUBMISSION_GRADED,
                NotificationType.SYSTEM_MAINTENANCE,
                NotificationType.SYSTEM_ANNOUNCEMENT
        ).contains(type);
    }

    private void enqueueOutbox(NotificationEntity entity, String eventType) {
        NotificationOutboxEntity outbox = new NotificationOutboxEntity();
        outbox.setId(UUID.randomUUID());
        outbox.setAggregateType("notification");
        outbox.setAggregateId(entity.getId());
        outbox.setEventType(eventType);
        outbox.setEventKey(entity.getId().toString() + ":" + eventType);
        outbox.setPayload(jsonCodec.toJsonString(Map.of(
                "eventId", UUID.randomUUID().toString(),
                "eventType", eventType,
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

    private void moveToDlq(NotificationOutboxEntity outbox, String errorMessage) {
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
        notificationMetricsService.incrementDlq();
    }

    private void moveUserNotificationToDlq(UserNotificationEntity userNotification, String errorMessage) {
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
        notificationMetricsService.incrementDlq();
    }

    private void logDelivery(
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

    private Set<NotificationChannel> parseChannelSet(String channelsJson) {
        Set<String> raw = jsonCodec.toStringSet(channelsJson);
        Set<NotificationChannel> channels = new LinkedHashSet<>();
        for (String value : raw) {
            channels.add(NotificationChannel.valueOf(value));
        }
        return channels;
    }

    private NotificationStatus resolveInitialStatus(SendMode sendMode) {
        return switch (sendMode) {
            case DRAFT -> NotificationStatus.DRAFT;
            case IMMEDIATE -> NotificationStatus.SENT;
            case SCHEDULED -> NotificationStatus.SCHEDULED;
        };
    }

    private void validateCreateRequest(AdminCreateNotificationRequest request) {
        if (request.getSendMode() == SendMode.SCHEDULED && request.getScheduledAt() == null) {
            throw new BadRequestException("scheduledAt is required for SCHEDULED notification");
        }
        if (request.getScheduledAt() != null && request.getScheduledAt().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException("scheduledAt must be in the future");
        }
        if (request.getExpiresAt() != null
                && request.getScheduledAt() != null
                && request.getExpiresAt().isBefore(request.getScheduledAt())) {
            throw new BadRequestException("expiresAt must be later than scheduledAt");
        }
    }

    private AdminNotificationListItemResponse toAdminListItem(NotificationEntity entity) {
        long totalRecipients = userNotificationRepository.countByNotification_Id(entity.getId());
        long deliveredCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.DELIVERED);
        long failedCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.FAILED);
        long readCount = userNotificationRepository.countByNotification_IdAndReadStatus(entity.getId(), ReadStatus.READ);

        return AdminNotificationListItemResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .channels(jsonCodec.toStringSet(entity.getChannels()))
                .createdAt(entity.getCreatedAt())
                .scheduledAt(entity.getScheduledAt())
                .expiresAt(entity.getExpiresAt())
                .totalRecipients(totalRecipients)
                .deliveredCount(deliveredCount)
                .failedCount(failedCount)
                .readCount(readCount)
                .build();
    }

    private AdminNotificationDetailResponse toAdminDetail(NotificationEntity entity) {
        long totalRecipients = userNotificationRepository.countByNotification_Id(entity.getId());
        long deliveredCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.DELIVERED);
        long failedCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.FAILED);
        long readCount = userNotificationRepository.countByNotification_IdAndReadStatus(entity.getId(), ReadStatus.READ);

        return AdminNotificationDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .targetMode(entity.getTargetMode())
                .targetPayload(jsonCodec.toJsonNode(entity.getTargetPayload()))
                .channels(jsonCodec.toStringSet(entity.getChannels()))
                .metadata(jsonCodec.toJsonNode(entity.getMetadata()))
                .scheduledAt(entity.getScheduledAt())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .totalRecipients(totalRecipients)
                .deliveredCount(deliveredCount)
                .failedCount(failedCount)
                .readCount(readCount)
                .build();
    }

    private InboxNotificationResponse toInboxResponse(UserNotificationEntity entity) {
        NotificationEntity notification = entity.getNotification();
        return InboxNotificationResponse.builder()
                .userNotificationId(entity.getId())
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .priority(notification.getPriority())
                .channel(entity.getChannel())
                .read(entity.getReadStatus() == ReadStatus.READ)
                .readAt(entity.getReadAt())
                .deliveredAt(entity.getDeliveredAt())
                .createdAt(notification.getCreatedAt())
                .expiresAt(notification.getExpiresAt())
                .build();
    }

    private NotificationPreferenceResponse toPreferenceResponse(NotificationPreferenceEntity entity) {
        return NotificationPreferenceResponse.builder()
                .notificationType(entity.getNotificationType())
                .inAppEnabled(entity.isInAppEnabled())
                .emailEnabled(entity.isEmailEnabled())
                .pushEnabled(entity.isPushEnabled())
                .frequency(entity.getFrequency())
                .quietHoursStart(entity.getQuietHoursStart())
                .quietHoursEnd(entity.getQuietHoursEnd())
                .build();
    }

    private NotificationRuleResponse toRuleResponse(NotificationRuleEntity entity) {
        return NotificationRuleResponse.builder()
                .eventType(entity.getEventType())
                .enabled(entity.isEnabled())
                .notificationType(entity.getNotificationType())
                .channels(jsonCodec.toStringSet(entity.getChannels()))
                .priority(entity.getPriority())
                .frequency(entity.getFrequency())
                .templateCode(entity.getTemplateCode())
                .targetMode(entity.getTargetMode())
                .targetPayload(jsonCodec.toJsonNode(entity.getTargetPayload()))
                .build();
    }

    private NotificationTemplateResponse toTemplateResponse(NotificationTemplateEntity entity) {
        return NotificationTemplateResponse.builder()
                .code(entity.getCode())
                .titleTemplate(entity.getTitleTemplate())
                .contentTemplate(entity.getContentTemplate())
                .channel(entity.getChannel())
                .active(entity.isActive())
                .version(entity.getVersion())
                .build();
    }

    private String fallbackEventTitle(NotificationType type) {
        return switch (type) {
            case ASSIGNMENT_CREATED -> "Bài tập mới";
            case DEADLINE_REMINDER -> "Nhắc hạn nộp bài";
            case SUBMISSION_GRADED -> "Bài nộp đã được chấm";
            case DISCUSSION_REPLY -> "Có phản hồi thảo luận mới";
            case SYSTEM_MAINTENANCE -> "Thông báo bảo trì hệ thống";
            case SYSTEM_ANNOUNCEMENT -> "Thông báo hệ thống";
        };
    }

    private TargetMode mapSimpleTargetToTargetMode(NotificationTargetType tt) {
        return switch (tt) {
            case CLASS, COURSE -> TargetMode.COURSE;
            case GLOBAL -> TargetMode.ALL;
            case USER -> TargetMode.USER_LIST;
        };
    }

    private String buildSimpleTargetPayload(NotificationTargetType tt, String targetId) {
        if (tt == NotificationTargetType.GLOBAL) {
            return jsonCodec.toJsonString(Map.of());
        }
        if (!StringUtils.hasText(targetId)) {
            throw new BadRequestException("targetId is required for targetType " + tt);
        }
        String id = targetId.trim();
        return switch (tt) {
            case CLASS -> jsonCodec.toJsonString(Map.of("classId", id));
            case COURSE -> jsonCodec.toJsonString(Map.of("courseId", id));
            case USER -> jsonCodec.toJsonString(Map.of("userIds", List.of(id)));
            case GLOBAL -> jsonCodec.toJsonString(Map.of());
        };
    }

    private ReadStatus parseReadStatus(String status) {
        if (!StringUtils.hasText(status) || "all".equalsIgnoreCase(status)) {
            return null;
        }
        if ("unread".equalsIgnoreCase(status)) {
            return ReadStatus.UNREAD;
        }
        if ("read".equalsIgnoreCase(status)) {
            return ReadStatus.READ;
        }
        throw new BadRequestException("status must be all|unread|read");
    }

    private Instant parseInstantNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String normalized = value.trim();
        try {
            return Instant.parse(normalized);
        } catch (Exception ignored) {
        }

        try {
            return OffsetDateTime.parse(normalized).toInstant();
        } catch (Exception ex) {
            throw new BadRequestException("Invalid timestamp format: " + value);
        }
    }

    private Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }

    private String groupToRole(String group) {
        return switch (group.toUpperCase()) {
            case "ALL_STUDENTS", "STUDENT" -> "STUDENT";
            case "ALL_TEACHERS", "ALL_INSTRUCTORS", "TEACHER", "INSTRUCTOR" -> "TEACHER";
            case "ALL_ADMINS", "ADMIN" -> "ADMIN";
            default -> null;
        };
    }

    private void validateRuleEventType(String eventType) {
        if (!StringUtils.hasText(eventType)) {
            throw new BadRequestException("eventType is required");
        }

        String normalized = eventType.trim().toLowerCase();
        if (normalized.startsWith("notification.")) {
            throw new BadRequestException("eventType with 'notification.' prefix is not allowed");
        }
    }

    private List<UUID> jsonUuidList(JsonNode node, String field) {
        if (node == null || node.get(field) == null || !node.get(field).isArray()) {
            return List.of();
        }
        List<UUID> result = new ArrayList<>();
        for (JsonNode item : node.get(field)) {
            try {
                result.add(UUID.fromString(item.asText()));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private UUID jsonUuid(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return null;
        }
        try {
            return UUID.fromString(node.get(field).asText());
        } catch (Exception ex) {
            return null;
        }
    }

    private List<String> jsonArrayText(JsonNode node, String field) {
        if (node == null || node.get(field) == null || !node.get(field).isArray()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (JsonNode item : node.get(field)) {
            if (!item.isNull() && StringUtils.hasText(item.asText())) {
                result.add(item.asText());
            }
        }
        return result;
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

    private <T> List<T> safeList(Collection<T> values) {
        return values == null ? List.of() : new ArrayList<>(values);
    }

    private String normalizeInboundPayload(String payload) {
        if (!StringUtils.hasText(payload)) {
            return jsonCodec.toJsonString(Map.of());
        }
        try {
            JsonNode parsed = objectMapper.readTree(payload);
            return objectMapper.writeValueAsString(parsed);
        } catch (Exception ex) {
            return jsonCodec.toJsonString(Map.of("rawPayload", payload));
        }
    }
}
