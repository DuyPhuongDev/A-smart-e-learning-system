package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.NotificationPreferenceEntity;
import com.hcmut.lms.notification.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreferenceEntity, UUID> {

    List<NotificationPreferenceEntity> findByUserId(UUID userId);

    Optional<NotificationPreferenceEntity> findByUserIdAndNotificationType(UUID userId, NotificationType notificationType);

    void deleteByUserId(UUID userId);
}
