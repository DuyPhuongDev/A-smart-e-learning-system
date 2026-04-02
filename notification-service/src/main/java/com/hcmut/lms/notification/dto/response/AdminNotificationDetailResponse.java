package com.hcmut.lms.notification.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.TargetMode;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class AdminNotificationDetailResponse {
    private UUID id;
    private String title;
    private String content;
    private NotificationType type;
    private NotificationPriority priority;
    private NotificationStatus status;
    private TargetMode targetMode;
    private JsonNode targetPayload;
    private Set<String> channels;
    private JsonNode metadata;
    private Instant scheduledAt;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;
    private long totalRecipients;
    private long deliveredCount;
    private long failedCount;
    private long readCount;
}
