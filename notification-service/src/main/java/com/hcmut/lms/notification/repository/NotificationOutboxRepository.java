package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.NotificationOutboxEntity;
import com.hcmut.lms.notification.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationOutboxRepository extends JpaRepository<NotificationOutboxEntity, UUID> {

    List<NotificationOutboxEntity> findTop100ByStatusAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
            OutboxStatus status,
            Instant now
    );

    long deleteByCreatedAtBefore(Instant threshold);
}
