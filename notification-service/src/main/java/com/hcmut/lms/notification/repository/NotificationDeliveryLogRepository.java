package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.NotificationDeliveryLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface NotificationDeliveryLogRepository extends JpaRepository<NotificationDeliveryLogEntity, UUID> {
    long deleteByCreatedAtBefore(Instant threshold);
}
