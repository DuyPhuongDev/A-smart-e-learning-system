package com.hcmut.lms.notification.entity;

import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.DeferReason;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.ReadStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_notifications", schema = "notification")
public class UserNotificationEntity extends AuditableEntity {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private NotificationEntity notification;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 16)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 32)
    private DeliveryStatus deliveryStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "read_status", nullable = false, length = 16)
    private ReadStatus readStatus;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(name = "delivery_attempts", nullable = false)
    private int deliveryAttempts;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Column(name = "last_error", columnDefinition = "text")
    private String lastError;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 32)
    private NotificationFrequency frequency;

    @Enumerated(EnumType.STRING)
    @Column(name = "defer_reason", length = 32)
    private DeferReason deferReason;

    @Column(name = "digest_bucket_date")
    private LocalDate digestBucketDate;
}
