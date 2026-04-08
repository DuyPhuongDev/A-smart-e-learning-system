package com.hcmut.lms.notification.dto.request;

import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PreferenceItemRequest {

    @NotNull
    private NotificationType notificationType;

    @NotNull
    private Boolean inAppEnabled;

    @NotNull
    private Boolean emailEnabled;

    @NotNull
    private Boolean pushEnabled;

    @NotNull
    private NotificationFrequency frequency;
}
