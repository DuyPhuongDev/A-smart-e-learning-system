package com.hcmut.lms.notification.repository;

import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.DeferReason;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.ReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserNotificationRepository extends JpaRepository<UserNotificationEntity, UUID> {

    @Query("""
            select un from UserNotificationEntity un
            join un.notification n
            where un.userId = :userId
            and (:readStatus is null or un.readStatus = :readStatus)
            and (n.expiresAt is null or n.expiresAt > :now)
            order by n.createdAt desc
            """)
    Page<UserNotificationEntity> findInboxByUserId(
            @Param("userId") UUID userId,
            @Param("readStatus") ReadStatus readStatus,
            @Param("now") Instant now,
            Pageable pageable
    );

    @Query("""
            select count(un) from UserNotificationEntity un
            join un.notification n
            where un.userId = :userId
            and un.readStatus = 'UNREAD'
            and (n.expiresAt is null or n.expiresAt > :now)
            """)
    long countUnread(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("""
            select count(un) from UserNotificationEntity un
            join un.notification n
            where un.userId in :userIds
            and un.readStatus = 'UNREAD'
            and (n.expiresAt is null or n.expiresAt > :now)
            """)
    long countUnreadByUserIds(@Param("userIds") List<UUID> userIds, @Param("now") Instant now);

    Optional<UserNotificationEntity> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("""
            update UserNotificationEntity un
            set un.readStatus = 'READ',
                un.readAt = :now,
                un.updatedAt = :now
            where un.userId = :userId
            and un.readStatus = 'UNREAD'
            """)
    int markAllRead(@Param("userId") UUID userId, @Param("now") Instant now);

    @Query("""
            select un from UserNotificationEntity un
            where un.channel = :channel
            and un.deliveryStatus in :statuses
            and un.nextAttemptAt is not null
            and un.nextAttemptAt <= :now
            order by un.createdAt asc
            """)
    List<UserNotificationEntity> findRetryCandidates(
            @Param("channel") NotificationChannel channel,
            @Param("statuses") List<DeliveryStatus> statuses,
            @Param("now") Instant now
    );

    @Query("""
            select un from UserNotificationEntity un
            where un.channel = 'EMAIL'
            and un.deferReason = :deferReason
            and un.digestBucketDate <= :digestDate
            and un.deliveryStatus = 'PENDING'
            order by un.userId asc, un.createdAt asc
            """)
    List<UserNotificationEntity> findDigestCandidates(
            @Param("deferReason") DeferReason deferReason,
            @Param("digestDate") LocalDate digestDate
    );

    long countByNotification_Id(UUID notificationId);

    long countByNotification_IdAndDeliveryStatus(UUID notificationId, DeliveryStatus deliveryStatus);

    long countByNotification_IdAndReadStatus(UUID notificationId, ReadStatus readStatus);

    long deleteByCreatedAtBefore(Instant threshold);
}
