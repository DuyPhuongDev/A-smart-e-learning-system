package com.hcmut.lms.notification.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.SendMode;
import com.hcmut.lms.notification.enums.TargetMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class AdminCreateNotificationRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationPriority priority;

    @NotNull
    private TargetMode targetMode;

    private JsonNode targetPayload;

    @NotEmpty
    private Set<NotificationChannel> channels;

    @NotNull
    private SendMode sendMode;

    private OffsetDateTime scheduledAt;

    private OffsetDateTime expiresAt;

    private JsonNode metadata;
}
