package com.hcmut.lms.notification.dto.response;

import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class NotificationPreferenceResponse {
    private NotificationType notificationType;
    private boolean inAppEnabled;
    private boolean emailEnabled;
    private boolean pushEnabled;
    private NotificationFrequency frequency;
    private LocalTime quietHoursStart;
    private LocalTime quietHoursEnd;
}
