package com.hcmut.lms.communication.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.communication.dto.request.DiscussionReplyEventRequest;
import com.hcmut.lms.communication.dto.request.MaintenanceScheduleEventRequest;
import com.hcmut.lms.common.event.NotificationTargetType;
import com.hcmut.lms.common.event.SimpleNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommunicationEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${notification.events.topic:lms.events.notification}")
    private String notificationTopic;

    @Value("${spring.application.name:communication-service}")
    private String sourceService;

    public void publishDiscussionReplyCreated(String postId, DiscussionReplyEventRequest request) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("postId", postId);
        String messageId = postId + ":" + Instant.now().toEpochMilli();

        if (request != null) {
            if (StringUtils.hasText(request.getEventId())) {
                messageId = request.getEventId().trim();
            }
            putIfNotBlank(metadata, "classId", request.getClassId());
            putIfNotBlank(metadata, "courseId", request.getCourseId());
            putIfNotBlank(metadata, "userId", request.getUserId());
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                metadata.put("userIds", request.getUserIds());
            }
            putIfNotBlank(metadata, "replyByUserId", request.getReplyByUserId());
            putIfNotBlank(metadata, "replyContent", request.getReplyContent());
        }

        NotificationTargetType targetType;
        String targetId = null;
        if (request != null && StringUtils.hasText(request.getClassId())) {
            targetType = NotificationTargetType.CLASS;
            targetId = request.getClassId().trim();
        } else if (request != null && StringUtils.hasText(request.getCourseId())) {
            targetType = NotificationTargetType.COURSE;
            targetId = request.getCourseId().trim();
        } else {
            log.warn("Discussion reply event missing classId/courseId; skipping notification postId={}", postId);
            return;
        }

        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .messageId(messageId)
                .sourceService(sourceService)
                .semanticType("DISCUSSION_REPLY")
                .targetType(targetType)
                .targetId(targetId)
                .title("Có phản hồi thảo luận mới")
                .content("Một phản hồi mới vừa được gửi trong thảo luận.")
                .metadata(metadata)
                .build();

        publish(event);
    }

    public void publishSystemMaintenanceScheduled(MaintenanceScheduleEventRequest request) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        String messageId = "system-maintenance:" + Instant.now().toEpochMilli();
        if (request != null) {
            if (StringUtils.hasText(request.getEventId())) {
                messageId = request.getEventId().trim();
            }
            putIfNotBlank(metadata, "title", request.getTitle());
            putIfNotBlank(metadata, "message", request.getMessage());
            putIfNotBlank(metadata, "scheduledAt", request.getScheduledAt());
            putIfNotBlank(metadata, "expiresAt", request.getExpiresAt());
        }

        String title = request != null && StringUtils.hasText(request.getTitle())
                ? request.getTitle().trim()
                : "Thông báo bảo trì hệ thống";
        String content = request != null && StringUtils.hasText(request.getMessage())
                ? request.getMessage().trim()
                : "Hệ thống LMS sẽ bảo trì theo lịch đã thông báo.";

        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .messageId(messageId)
                .sourceService(sourceService)
                .semanticType("SYSTEM_MAINTENANCE")
                .targetType(NotificationTargetType.GLOBAL)
                .title(title)
                .content(content)
                .metadata(metadata)
                .build();

        publish(event);
    }

    private void publish(SimpleNotificationEvent event) {
        try {
            String key = event.getMessageId();
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(notificationTopic, key, payload).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed publishing semanticType={} key={}: {}",
                            event.resolvedSemanticType(), key, throwable.getMessage(), throwable);
                } else {
                    log.debug("Published semanticType={} key={} to {}", event.resolvedSemanticType(), key, notificationTopic);
                }
            });
        } catch (Exception ex) {
            log.error("Cannot publish key={}: {}", event.getMessageId(), ex.getMessage(), ex);
        }
    }

    private void putIfNotBlank(Map<String, Object> data, String key, String value) {
        if (value != null && !value.isBlank()) {
            data.put(key, value);
        }
    }
}
