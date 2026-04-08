package com.hcmut.lms.notification.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.TargetMode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class RuleUpdateRequest {

    @NotNull
    private Boolean enabled;

    @NotNull
    private NotificationType notificationType;

    @NotEmpty
    private Set<NotificationChannel> channels;

    @NotNull
    private NotificationPriority priority;

    @NotNull
    private NotificationFrequency frequency;

    private String templateCode;

    @NotNull
    private TargetMode targetMode;

    private JsonNode targetPayload;
}
