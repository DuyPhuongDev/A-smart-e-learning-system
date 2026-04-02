package com.hcmut.lms.notification.dto.response;

import com.hcmut.lms.notification.enums.NotificationChannel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationTemplateResponse {
    private String code;
    private String titleTemplate;
    private String contentTemplate;
    private NotificationChannel channel;
    private boolean active;
    private int version;
}
