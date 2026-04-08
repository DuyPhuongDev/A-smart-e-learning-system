package com.hcmut.lms.notification.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.TargetMode;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class NotificationRuleResponse {
    private String eventType;
    private boolean enabled;
    private NotificationType notificationType;
    private Set<String> channels;
    private NotificationPriority priority;
    private NotificationFrequency frequency;
    private String templateCode;
    private TargetMode targetMode;
    private JsonNode targetPayload;
}
