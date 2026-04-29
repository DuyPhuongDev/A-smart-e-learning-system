package com.hcmut.lms.notification.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InboxNotificationResponse {
    private UUID userNotificationId;
    private UUID notificationId;
    private String title;
    private String content;
    private NotificationType type;
    private NotificationPriority priority;
    private NotificationChannel channel;
    private boolean read;
    private JsonNode metadata;
    private Instant readAt;
    private Instant deliveredAt;
    private Instant createdAt;
    private Instant expiresAt;
}
