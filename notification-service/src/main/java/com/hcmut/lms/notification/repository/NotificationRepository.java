package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID>, JpaSpecificationExecutor<NotificationEntity> {

    Optional<NotificationEntity> findBySourceServiceAndSourceEventIdAndType(
            String sourceService,
            String sourceEventId,
            NotificationType type
    );

    Optional<NotificationEntity> findByCreatedByAndIdempotencyKey(UUID createdBy, String idempotencyKey);

    List<NotificationEntity> findByStatusAndScheduledAtLessThanEqual(NotificationStatus status, Instant scheduledAt);

    long deleteByCreatedAtBefore(Instant threshold);
}
