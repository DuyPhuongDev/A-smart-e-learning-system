package com.hcmut.lms.communication.event;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class LmsEventEnvelope {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private String sourceService;
    private String version;
    private Object data;
}
