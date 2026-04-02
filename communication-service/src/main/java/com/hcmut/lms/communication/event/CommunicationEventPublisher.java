package com.hcmut.lms.communication.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.communication.dto.request.DiscussionReplyEventRequest;
import com.hcmut.lms.communication.dto.request.MaintenanceScheduleEventRequest;
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

    @Value("${communication.events.topic-prefix:lms.events}")
    private String topicPrefix;

    @Value("${spring.application.name:communication-service}")
    private String sourceService;

    public void publishDiscussionReplyCreated(String postId, DiscussionReplyEventRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("postId", postId);
        String eventKey = postId + ":" + Instant.now().toEpochMilli();

        if (request != null) {
            if (StringUtils.hasText(request.getEventId())) {
                eventKey = request.getEventId().trim();
            }
            putIfNotBlank(data, "classId", request.getClassId());
            putIfNotBlank(data, "courseId", request.getCourseId());
            putIfNotBlank(data, "userId", request.getUserId());
            if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
                data.put("userIds", request.getUserIds());
            }
            putIfNotBlank(data, "replyByUserId", request.getReplyByUserId());
            putIfNotBlank(data, "replyContent", request.getReplyContent());
        }

        publish(
                "discussion.reply.created",
                "communication.discussion.reply.created",
                eventKey,
                data
        );
    }

    public void publishSystemMaintenanceScheduled(MaintenanceScheduleEventRequest request) {
        Map<String, Object> data = new LinkedHashMap<>();
        String eventKey = "system-maintenance:" + Instant.now().toEpochMilli();
        if (request != null) {
            if (StringUtils.hasText(request.getEventId())) {
                eventKey = request.getEventId().trim();
            }
            putIfNotBlank(data, "title", request.getTitle());
            putIfNotBlank(data, "message", request.getMessage());
            putIfNotBlank(data, "scheduledAt", request.getScheduledAt());
            putIfNotBlank(data, "expiresAt", request.getExpiresAt());
        }

        publish(
                "system.maintenance.scheduled",
                "system.maintenance.scheduled",
                eventKey,
                data
        );
    }

    private void publish(String topicSuffix, String eventType, String key, Map<String, Object> data) {
        try {
            LmsEventEnvelope envelope = LmsEventEnvelope.builder()
                    .eventId(key)
                    .eventType(eventType)
                    .occurredAt(Instant.now())
                    .sourceService(sourceService)
                    .version("1.0")
                    .data(data)
                    .build();

            String topic = topicPrefix + "." + topicSuffix;
            String payload = objectMapper.writeValueAsString(envelope);
            kafkaTemplate.send(topic, key, payload).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed publishing {} with key={}: {}", eventType, key, throwable.getMessage(), throwable);
                } else {
                    log.debug("Published {} with key={} to {}", eventType, key, topic);
                }
            });
        } catch (Exception ex) {
            log.error("Cannot publish {} with key={}: {}", eventType, key, ex.getMessage(), ex);
        }
    }

    private void putIfNotBlank(Map<String, Object> data, String key, String value) {
        if (value != null && !value.isBlank()) {
            data.put(key, value);
        }
    }
}
