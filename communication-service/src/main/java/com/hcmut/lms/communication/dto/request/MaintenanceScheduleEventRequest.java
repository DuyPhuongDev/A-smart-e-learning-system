package com.hcmut.lms.communication.dto.request;

import lombok.Data;

@Data
public class MaintenanceScheduleEventRequest {
    private String eventId;
    private String title;
    private String message;
    private String scheduledAt;
    private String expiresAt;
}
