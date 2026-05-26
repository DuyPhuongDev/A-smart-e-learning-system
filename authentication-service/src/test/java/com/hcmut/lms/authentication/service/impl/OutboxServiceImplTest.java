package com.hcmut.lms.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.authentication.model.entity.Outbox;
import com.hcmut.lms.authentication.repository.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class OutboxServiceImplTest {

    @Mock private OutboxRepository outboxRepository;

    @InjectMocks
    private OutboxServiceImpl outboxService;

    @Test
    void publishEvent_shouldSaveOutboxEvent_whenValidInput() {
        UUID aggregateId = UUID.randomUUID();
        String aggregateType = "UserCredentials";
        String eventType = "USER_CREATED";
        String payload = "{\"userId\":\"" + aggregateId + "\"}";

        Outbox savedOutbox = Outbox.builder()
                .id(UUID.randomUUID())
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .type(eventType)
                .payload(payload)
                .build();
        when(outboxRepository.save(any())).thenReturn(savedOutbox);

        assertDoesNotThrow(() -> outboxService.publishEvent(aggregateType, aggregateId, eventType, payload));

        ArgumentCaptor<Outbox> captor = ArgumentCaptor.forClass(Outbox.class);
        verify(outboxRepository).save(captor.capture());
        Outbox captured = captor.getValue();
        assertEquals(aggregateType, captured.getAggregateType());
        assertEquals(aggregateId, captured.getAggregateId());
        assertEquals(eventType, captured.getType());
        assertEquals(payload, captured.getPayload());
    }
}
