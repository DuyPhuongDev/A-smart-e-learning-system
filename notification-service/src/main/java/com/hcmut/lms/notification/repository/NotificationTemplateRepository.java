package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.NotificationTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplateEntity, String> {
}
