package com.hcmut.lms.notification.entity;

import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationStatus;
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

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications", schema = "notification")
public class NotificationEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 64)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 32)
    private NotificationPriority priority;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "source_service", length = 128)
    private String sourceService;

    @Column(name = "source_event_id", length = 255)
    private String sourceEventId;

    @Column(name = "idempotency_key", length = 128)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_mode", nullable = false, length = 32)
    private TargetMode targetMode;

    @Column(name = "target_payload", nullable = false, columnDefinition = "jsonb")
    private String targetPayload;

    @Column(name = "channels", nullable = false, columnDefinition = "jsonb")
    private String channels;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private NotificationStatus status;

    @Column(name = "metadata", nullable = false, columnDefinition = "jsonb")
    private String metadata;
}
