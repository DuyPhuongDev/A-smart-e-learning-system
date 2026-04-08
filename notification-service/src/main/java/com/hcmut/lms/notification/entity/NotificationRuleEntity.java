package com.hcmut.lms.notification.entity;

import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.TargetMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Getter
@Setter
@Entity
@Table(name = "notification_rules", schema = "notification")
public class NotificationRuleEntity extends AuditableEntity {

    @Id
    @Column(name = "event_type", length = 128)
    private String eventType;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 64)
    private NotificationType notificationType;

    @Column(name = "channels", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String channels;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 32)
    private NotificationPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 32)
    private NotificationFrequency frequency;

    @Column(name = "template_code", length = 128)
    private String templateCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_mode", nullable = false, length = 32)
    private TargetMode targetMode;

    @Column(name = "target_payload", nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String targetPayload;
}
