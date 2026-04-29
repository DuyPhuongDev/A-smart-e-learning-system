package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.common.event.NotificationTargetType;
import com.hcmut.lms.common.event.SimpleNotificationEvent;
import com.hcmut.lms.notification.entity.NotificationDlqEntity;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.TargetMode;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.repository.NotificationDlqRepository;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.NotificationRuleRepository;
import com.hcmut.lms.notification.entity.NotificationRuleEntity;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.NotificationOutboxEventType;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationInboundService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.util.JsonCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationInboundServiceImpl implements NotificationInboundService {

    private final NotificationRepository notificationRepository;
    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationDlqRepository notificationDlqRepository;
    private final NotificationDispatchService notificationDispatchService;
    private final NotificationOutboxService notificationOutboxService;
    private final JsonCodec jsonCodec;
    private final ObjectMapper objectMapper;

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

        TargetMode targetMode = mapTargetType(event.getTargetType());
        String targetPayloadJson = buildTargetPayload(event.getTargetType(), event.getTargetId());

        Map<String, Object> meta = new LinkedHashMap<>();
        if (event.getMetadata() != null) {
            meta.putAll(event.getMetadata());
        }
        meta.put("semanticType", semantic);
        meta.put("occurredAt", Instant.now().toString());

        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setType(rule.getNotificationType());
        entity.setPriority(rule.getPriority());
        entity.setTitle(StringUtils.hasText(event.getTitle())
                ? event.getTitle().trim()
                : fallbackTitle(rule.getNotificationType()));
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
        entity.setMetadata(jsonCodec.toJsonString(meta));

        notificationRepository.save(entity);
        notificationOutboxService.enqueue(entity, NotificationOutboxEventType.EVENT_INGESTED);
        notificationDispatchService.disưpatch(entity);
    }

    @Override
    @Transactional
    public void recordInboundFailure(String topic, String key, String payload, String errorMessage) {
        NotificationDlqEntity dlq = new NotificationDlqEntity();
        dlq.setId(UUID.randomUUID());
        dlq.setOutboxId(null);
        dlq.setEventType("kafka.inbound." + (StringUtils.hasText(topic) ? topic : "unknown"));
        dlq.setEventKey(StringUtils.hasText(key) ? key : UUID.randomUUID().toString());
        dlq.setPayload(normalizePayload(payload));
        dlq.setErrorMessage(errorMessage);
        dlq.setAttempts(1);
        dlq.setCreatedAt(Instant.now());
        notificationDlqRepository.save(dlq);
    }

    private TargetMode mapTargetType(NotificationTargetType tt) {
        return switch (tt) {
            case CLASS, COURSE -> TargetMode.COURSE;
            case GLOBAL -> TargetMode.ALL;
            case USER -> TargetMode.USER_LIST;
        };
    }

    private String buildTargetPayload(NotificationTargetType tt, String targetId) {
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

    private String fallbackTitle(NotificationType type) {
        return switch (type) {
            case ASSIGNMENT_CREATED -> "Bài tập mới";
            case DEADLINE_REMINDER -> "Nhắc hạn nộp bài";
            case SUBMISSION_GRADED -> "Bài nộp đã được chấm";
            case DISCUSSION_REPLY -> "Có phản hồi thảo luận mới";
            case SYSTEM_MAINTENANCE -> "Thông báo bảo trì hệ thống";
            case SYSTEM_ANNOUNCEMENT -> "Thông báo hệ thống";
        };
    }

    private String normalizePayload(String payload) {
        if (!StringUtils.hasText(payload)) {
            return jsonCodec.toJsonString(Map.of());
        }
        try {
            return objectMapper.writeValueAsString(objectMapper.readTree(payload));
        } catch (Exception ex) {
            return jsonCodec.toJsonString(Map.of("rawPayload", payload));
        }
    }
}
