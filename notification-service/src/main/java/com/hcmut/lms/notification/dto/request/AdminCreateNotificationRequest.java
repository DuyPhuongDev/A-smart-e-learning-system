package com.hcmut.lms.notification.dto.request;

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
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    // Required when targetMode = USER_LIST
    private Set<UUID> userIds;

    // Required when targetMode = GROUP (values: ADMIN, TEACHER, ALL_STUDENT, K22_STUDENT,...)
    private Set<String> groups;

    // Required when targetMode = COURSE (provide courseId or classIds)
    private List<UUID> classIds;

    // Required when targetMode = PROGRAM
    private List<UUID> specializationIds;

    // targetMode = ALL requires no extra fields

    @NotEmpty
    private Set<NotificationChannel> channels;

    @NotNull
    private SendMode sendMode;

    private OffsetDateTime scheduledAt;

    private OffsetDateTime expiresAt;
}
