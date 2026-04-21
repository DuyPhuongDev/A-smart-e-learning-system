package com.hcmut.lms.notification.dto.request;

import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.SendMode;
import com.hcmut.lms.notification.enums.TeacherNotificationScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class TeacherCreateNotificationRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationPriority priority;

    @NotNull
    private TeacherNotificationScope scope;

    // Required when scope = SPECIFIC_CLASSES
    private List<UUID> classIds;

    @NotEmpty
    private Set<NotificationChannel> channels;

    @NotNull
    private SendMode sendMode;

    private OffsetDateTime scheduledAt;

    private OffsetDateTime expiresAt;
}
