package com.hcmut.lms.notification.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.time.Instant;

@Data
public class LmsEventEnvelope {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private String sourceService;
    private String version;
    private JsonNode data;
}
