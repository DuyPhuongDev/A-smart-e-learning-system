package com.hcmut.lms.authentication.service;

import java.util.UUID;

/**
 * Service for managing outbox pattern events in Authentication Service
 * Events are stored in outbox table and will be processed by Debezium CDC
 */
public interface OutboxService {
    
    /**
     * Publish an event to the outbox table
     * 
     * @param aggregateType The type of aggregate (e.g., "user", "credential")
     * @param aggregateId The ID of the aggregate
     * @param eventType The type of event (e.g., "UserCredentialCreationFailed")
     * @param payload The event payload as JSON string
     */
    void publishEvent(String aggregateType, UUID aggregateId, String eventType, String payload);
}
