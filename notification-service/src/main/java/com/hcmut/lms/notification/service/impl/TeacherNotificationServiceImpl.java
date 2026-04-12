package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.client.CourseManagementInternalClient;
import com.hcmut.lms.notification.dto.request.TeacherCreateNotificationRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationListItemResponse;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationOutboxEventType;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.ReadStatus;
import com.hcmut.lms.notification.enums.SendMode;
import com.hcmut.lms.notification.enums.TargetMode;
import com.hcmut.lms.notification.enums.TeacherNotificationScope;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.exception.ForbiddenException;
import com.hcmut.lms.notification.exception.ResourceNotFoundException;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.service.TeacherNotificationService;
import com.hcmut.lms.notification.util.JsonCodec;
import com.hcmut.lms.notification.util.RoleGuard;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherNotificationServiceImpl implements TeacherNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationDispatchService notificationDispatchService;
    private final NotificationOutboxService notificationOutboxService;
    private final CourseManagementInternalClient courseManagementInternalClient;
    private final JsonCodec jsonCodec;

    @Override
    @Transactional
    public AdminNotificationCreateResponse create(
            CurrentUserInfo currentUser,
            TeacherCreateNotificationRequest request,
            String idempotencyKey
    ) {
        RoleGuard.requireTeacher(currentUser);
        validateCreateRequest(request);

        if (StringUtils.hasText(idempotencyKey)) {
            NotificationEntity existing = notificationRepository
                    .findByCreatedByAndIdempotencyKey(currentUser.getId(), idempotencyKey.trim())
                    .orElse(null);
            if (existing != null) {
                return AdminNotificationCreateResponse.builder()
                        .notificationId(existing.getId())
                        .status(existing.getStatus())
                        .build();
            }
        }

        List<UUID> resolvedClassIds = resolveClassIds(currentUser, request);

        Map<String, Object> systemMetadata = new LinkedHashMap<>();
        systemMetadata.put("source", "teacher");
        systemMetadata.put("createdBy", currentUser.getId().toString());
        systemMetadata.put("createdAt", Instant.now().toString());
        systemMetadata.put("scope", request.getScope().name());

        String targetPayload = jsonCodec.toJsonString(
                Map.of("classIds", resolvedClassIds.stream().map(UUID::toString).toList())
        );

        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle(request.getTitle().trim());
        entity.setContent(request.getContent().trim());
        entity.setType(request.getType());
        entity.setPriority(request.getPriority());
        entity.setCreatedBy(currentUser.getId());
        entity.setTargetMode(TargetMode.COURSE);
        entity.setTargetPayload(targetPayload);
        entity.setChannels(jsonCodec.toJsonString(
                request.getChannels().stream()
                        .map(NotificationChannel::name)
                        .collect(Collectors.toCollection(LinkedHashSet::new))));
        entity.setMetadata(jsonCodec.toJsonString(systemMetadata));
        entity.setScheduledAt(toInstant(request.getScheduledAt()));
        entity.setExpiresAt(toInstant(request.getExpiresAt()));
        entity.setStatus(resolveInitialStatus(request.getSendMode()));
        entity.setSourceService("notification-service");
        entity.setSourceEventId(null);
        entity.setIdempotencyKey(StringUtils.hasText(idempotencyKey) ? idempotencyKey.trim() : null);

        notificationRepository.save(entity);
        notificationOutboxService.enqueue(entity, NotificationOutboxEventType.ADMIN_CREATED);

        if (request.getSendMode() == SendMode.IMMEDIATE) {
            notificationDispatchService.dispatch(entity);
        }

        return AdminNotificationCreateResponse.builder()
                .notificationId(entity.getId())
                .status(entity.getStatus())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminNotificationListItemResponse> list(
            CurrentUserInfo currentUser,
            String status,
            String type,
            String channel,
            String from,
            String to,
            String keyword,
            int page,
            int size
    ) {
        RoleGuard.requireTeacher(currentUser);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        Specification<NotificationEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("createdBy"), currentUser.getId()));

            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), NotificationStatus.valueOf(status.trim().toUpperCase())));
            }
            if (StringUtils.hasText(type)) {
                predicates.add(cb.equal(root.get("type"), NotificationType.valueOf(type.trim().toUpperCase())));
            }
            if (StringUtils.hasText(keyword)) {
                String normalized = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), normalized),
                        cb.like(cb.lower(root.get("content")), normalized)
                ));
            }
            if (StringUtils.hasText(channel)) {
                String normalized = "%\"" + channel.trim().toUpperCase() + "\"%";
                predicates.add(cb.like(root.get("channels"), normalized));
            }

            Instant fromInstant = parseInstantNullable(from);
            if (fromInstant != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromInstant));
            }
            Instant toInstant = parseInstantNullable(to);
            if (toInstant != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toInstant));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Page<NotificationEntity> result = notificationRepository.findAll(specification, pageable);
        List<AdminNotificationListItemResponse> content = result.getContent().stream()
                .map(this::toListItem)
                .toList();

        return PageResponse.<AdminNotificationListItemResponse>builder()
                .content(content)
                .pageNumber(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .empty(result.isEmpty())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminNotificationDetailResponse detail(CurrentUserInfo currentUser, UUID notificationId) {
        RoleGuard.requireTeacher(currentUser);
        NotificationEntity entity = findOwnedNotification(currentUser, notificationId);
        return toDetail(entity);
    }

    @Override
    @Transactional
    public void sendNow(CurrentUserInfo currentUser, UUID notificationId) {
        RoleGuard.requireTeacher(currentUser);
        NotificationEntity entity = findOwnedNotification(currentUser, notificationId);

        if (entity.getStatus() != NotificationStatus.DRAFT && entity.getStatus() != NotificationStatus.SCHEDULED) {
            throw new BadRequestException("Only DRAFT or SCHEDULED notifications can be sent now");
        }

        entity.setStatus(NotificationStatus.SENT);
        entity.setScheduledAt(null);
        notificationRepository.save(entity);
        notificationOutboxService.enqueue(entity, NotificationOutboxEventType.ADMIN_SEND_NOW);
        notificationDispatchService.dispatch(entity);
    }

    @Override
    @Transactional
    public void cancel(CurrentUserInfo currentUser, UUID notificationId) {
        RoleGuard.requireTeacher(currentUser);
        NotificationEntity entity = findOwnedNotification(currentUser, notificationId);

        if (entity.getStatus() != NotificationStatus.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED notifications can be cancelled");
        }

        entity.setStatus(NotificationStatus.CANCELLED);
        notificationRepository.save(entity);
        notificationOutboxService.enqueue(entity, NotificationOutboxEventType.ADMIN_CANCELLED);
    }

    private NotificationEntity findOwnedNotification(CurrentUserInfo currentUser, UUID notificationId) {
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        if (!currentUser.getId().equals(entity.getCreatedBy())) {
            throw new ForbiddenException("You do not have permission to access this notification");
        }
        return entity;
    }

    private List<UUID> resolveClassIds(CurrentUserInfo currentUser, TeacherCreateNotificationRequest request) {
        List<UUID> teacherClassIds = courseManagementInternalClient.getClassIdsByTeacher(currentUser.getId());

        if (teacherClassIds == null || teacherClassIds.isEmpty()) {
            throw new BadRequestException("You do not manage any classes");
        }

        if (request.getScope() == TeacherNotificationScope.ALL_MY_CLASSES) {
            return teacherClassIds;
        }

        // SPECIFIC_CLASSES: validate ownership
        Set<UUID> teacherClassIdSet = new LinkedHashSet<>(teacherClassIds);
        List<UUID> invalidIds = request.getClassIds().stream()
                .filter(id -> !teacherClassIdSet.contains(id))
                .toList();

        if (!invalidIds.isEmpty()) {
            throw new ForbiddenException("You do not manage the following class(es): " + invalidIds);
        }

        return request.getClassIds();
    }

    private void validateCreateRequest(TeacherCreateNotificationRequest request) {
        if (request.getSendMode() == SendMode.SCHEDULED && request.getScheduledAt() == null) {
            throw new BadRequestException("scheduledAt is required for SCHEDULED notification");
        }
        if (request.getScheduledAt() != null && request.getScheduledAt().isBefore(OffsetDateTime.now())) {
            throw new BadRequestException("scheduledAt must be in the future");
        }
        if (request.getExpiresAt() != null
                && request.getScheduledAt() != null
                && request.getExpiresAt().isBefore(request.getScheduledAt())) {
            throw new BadRequestException("expiresAt must be later than scheduledAt");
        }
        if (request.getScope() == TeacherNotificationScope.SPECIFIC_CLASSES) {
            if (request.getClassIds() == null || request.getClassIds().isEmpty()) {
                throw new BadRequestException("classIds is required when scope is SPECIFIC_CLASSES");
            }
        }
    }

    private NotificationStatus resolveInitialStatus(SendMode sendMode) {
        return switch (sendMode) {
            case DRAFT -> NotificationStatus.DRAFT;
            case IMMEDIATE -> NotificationStatus.SENT;
            case SCHEDULED -> NotificationStatus.SCHEDULED;
        };
    }

    private AdminNotificationListItemResponse toListItem(NotificationEntity entity) {
        long totalRecipients = userNotificationRepository.countByNotification_Id(entity.getId());
        long deliveredCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.DELIVERED);
        long failedCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.FAILED);
        long readCount = userNotificationRepository.countByNotification_IdAndReadStatus(entity.getId(), ReadStatus.READ);

        return AdminNotificationListItemResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .type(entity.getType())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .channels(jsonCodec.toStringSet(entity.getChannels()))
                .createdAt(entity.getCreatedAt())
                .scheduledAt(entity.getScheduledAt())
                .expiresAt(entity.getExpiresAt())
                .totalRecipients(totalRecipients)
                .deliveredCount(deliveredCount)
                .failedCount(failedCount)
                .readCount(readCount)
                .build();
    }

    private AdminNotificationDetailResponse toDetail(NotificationEntity entity) {
        long totalRecipients = userNotificationRepository.countByNotification_Id(entity.getId());
        long deliveredCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.DELIVERED);
        long failedCount = userNotificationRepository.countByNotification_IdAndDeliveryStatus(entity.getId(), DeliveryStatus.FAILED);
        long readCount = userNotificationRepository.countByNotification_IdAndReadStatus(entity.getId(), ReadStatus.READ);

        return AdminNotificationDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .type(entity.getType())
                .priority(entity.getPriority())
                .status(entity.getStatus())
                .targetMode(entity.getTargetMode())
                .targetPayload(jsonCodec.toJsonNode(entity.getTargetPayload()))
                .channels(jsonCodec.toStringSet(entity.getChannels()))
                .metadata(jsonCodec.toJsonNode(entity.getMetadata()))
                .scheduledAt(entity.getScheduledAt())
                .expiresAt(entity.getExpiresAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .totalRecipients(totalRecipients)
                .deliveredCount(deliveredCount)
                .failedCount(failedCount)
                .readCount(readCount)
                .build();
    }

    private Instant parseInstantNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String normalized = value.trim();
        try {
            return Instant.parse(normalized);
        } catch (Exception ignored) {
        }
        try {
            return OffsetDateTime.parse(normalized).toInstant();
        } catch (Exception ex) {
            throw new BadRequestException("Invalid timestamp format: " + value);
        }
    }

    private Instant toInstant(OffsetDateTime value) {
        return value == null ? null : value.toInstant();
    }
}
