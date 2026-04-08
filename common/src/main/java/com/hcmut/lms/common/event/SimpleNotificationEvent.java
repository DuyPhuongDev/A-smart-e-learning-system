package com.hcmut.lms.common.event;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Standard Kafka payload for {@link NotificationKafkaTopics#INBOUND_NOTIFICATION}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleNotificationEvent {

    /** Idempotency key; must be unique per logical notification. */
    private String messageId;

    private String sourceService;

    /**
     * Stable key matching {@code notification_rules.event_type} (e.g. ASSIGNMENT_CREATED).
     */
    private String semanticType;

    @JsonAlias("type")
    private String typeAlias;

    private NotificationTargetType targetType;

    /** Class UUID, user UUID, or null for GLOBAL. */
    private String targetId;

    private String title;

    private String content;

    @Builder.Default
    private Map<String, Object> metadata = new LinkedHashMap<>();

    public String resolvedSemanticType() {
        if (semanticType != null && !semanticType.isBlank()) {
            return semanticType.trim();
        }
        if (typeAlias != null && !typeAlias.isBlank()) {
            return typeAlias.trim();
        }
        return null;
    }
}
