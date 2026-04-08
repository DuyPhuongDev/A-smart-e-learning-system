package com.hcmut.lms.notification.entity;

import com.hcmut.lms.notification.enums.NotificationChannel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_delivery_logs", schema = "notification")
public class NotificationDeliveryLogEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_notification_id")
    private UserNotificationEntity userNotification;

    @Column(name = "provider", nullable = false, length = 64)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private NotificationChannel channel;

    @Column(name = "request_payload", columnDefinition = "text")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "text")
    private String responsePayload;

    @Column(name = "response_code", length = 32)
    private String responseCode;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
