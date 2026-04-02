package com.hcmut.lms.notification.dto.response;

import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class AdminNotificationListItemResponse {
    private UUID id;
    private String title;
    private NotificationType type;
    private NotificationPriority priority;
    private NotificationStatus status;
    private Set<String> channels;
    private Instant createdAt;
    private Instant scheduledAt;
    private Instant expiresAt;
    private long totalRecipients;
    private long deliveredCount;
    private long failedCount;
    private long readCount;
}
