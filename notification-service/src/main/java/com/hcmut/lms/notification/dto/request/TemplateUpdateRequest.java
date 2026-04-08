package com.hcmut.lms.notification.dto.request;

import com.hcmut.lms.notification.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TemplateUpdateRequest {

    @NotBlank
    private String titleTemplate;

    @NotBlank
    private String contentTemplate;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private Boolean active;
}
