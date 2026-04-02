package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.NotificationDlqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.UUID;

public interface NotificationDlqRepository extends JpaRepository<NotificationDlqEntity, UUID> {
    long deleteByCreatedAtBefore(Instant threshold);
}
