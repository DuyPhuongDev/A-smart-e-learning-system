package com.hcmut.lms.notification.dto.response;

import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class WebSocketNotificationPayload {
    private UUID userNotificationId;
    private UUID notificationId;
    private String title;
    private String content;
    private NotificationType type;
    private NotificationPriority priority;
    private Instant createdAt;
}
