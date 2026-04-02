package com.hcmut.lms.notification.dto.response;

import com.hcmut.lms.notification.enums.NotificationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AdminNotificationCreateResponse {
    private UUID notificationId;
    private NotificationStatus status;
}
