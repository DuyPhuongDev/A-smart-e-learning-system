package com.hcmut.lms.authentication.service.impl;

import com.hcmut.lms.authentication.model.entity.Outbox;
import com.hcmut.lms.authentication.repository.OutboxRepository;
import com.hcmut.lms.authentication.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void publishEvent(String aggregateType, UUID aggregateId, String eventType, String payload) {
        log.info("Publishing event to outbox: aggregateType={}, aggregateId={}, eventType={}", 
                aggregateType, aggregateId, eventType);
        
        Outbox outboxEvent = Outbox.builder()
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .type(eventType)
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build();
        
        outboxRepository.save(outboxEvent);
        
        log.info("Event published to outbox successfully: id={}", outboxEvent.getId());
    }
}
