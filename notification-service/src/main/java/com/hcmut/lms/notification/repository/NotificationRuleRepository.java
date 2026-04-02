package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.NotificationRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRuleRepository extends JpaRepository<NotificationRuleEntity, String> {
}
