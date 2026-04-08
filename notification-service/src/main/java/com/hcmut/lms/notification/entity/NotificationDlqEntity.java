package com.hcmut.lms.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_dlq", schema = "notification")
public class NotificationDlqEntity {

    @Id
    private UUID id;

    @Column(name = "outbox_id")
    private UUID outboxId;

    @Column(name = "event_type", nullable = false, length = 128)
    private String eventType;

    @Column(name = "event_key", length = 255)
    private String eventKey;

    @Column(name = "payload", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String payload;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
