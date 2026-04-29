package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.AdminCreateNotificationRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationListItemResponse;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.NotificationOutboxEventType;
import com.hcmut.lms.notification.enums.ReadStatus;
import com.hcmut.lms.notification.enums.SendMode;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.exception.ResourceNotFoundException;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.AdminNotificationService;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final NotificationRepository notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final NotificationDispatchService notificationDispatchService;
    private final NotificationOutboxService notificationOutboxService;
    private final JsonCodec jsonCodec;

    @Override
    @Transactional
    public AdminNotificationCreateResponse create(
            CurrentUserInfo currentUser,
            AdminCreateNotificationRequest request,
            String idempotencyKey
    ) {
        RoleGuard.requireAdmin(currentUser);
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

        Map<String, Object> systemMetadata = new LinkedHashMap<>();
        systemMetadata.put("source", "admin");
        systemMetadata.put("createdBy", currentUser.getId().toString());
        systemMetadata.put("createdAt", Instant.now().toString());

        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle(request.getTitle().trim());
        entity.setContent(request.getContent().trim());
        entity.setType(request.getType());
        entity.setPriority(request.getPriority());
        entity.setCreatedBy(currentUser.getId());
        entity.setTargetMode(request.getTargetMode());
        entity.setTargetPayload(buildTargetPayload(request));
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
        RoleGuard.requireAdmin(currentUser);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        Specification<NotificationEntity> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
        RoleGuard.requireAdmin(currentUser);
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        return toDetail(entity);
    }

    @Override
    @Transactional
    public void sendNow(CurrentUserInfo currentUser, UUID notificationId) {
        RoleGuard.requireAdmin(currentUser);
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

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
        RoleGuard.requireAdmin(currentUser);
        NotificationEntity entity = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));

        if (entity.getStatus() != NotificationStatus.SCHEDULED) {
            throw new BadRequestException("Only SCHEDULED notifications can be cancelled");
        }

        entity.setStatus(NotificationStatus.CANCELLED);
        notificationRepository.save(entity);
        notificationOutboxService.enqueue(entity, NotificationOutboxEventType.ADMIN_CANCELLED);
    }

    private void validateCreateRequest(AdminCreateNotificationRequest request) {
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
        switch (request.getTargetMode()) {
            case USER_LIST -> {
                if (request.getUserIds() == null || request.getUserIds().isEmpty()) {
                    throw new BadRequestException("userIds is required for targetMode USER_LIST");
                }
            }
            case GROUP -> {
                if (request.getGroups() == null || request.getGroups().isEmpty()) {
                    throw new BadRequestException("groups is required for targetMode GROUP (e.g. ADMIN, TEACHER, STUDENT)");
                }
            }
            case COURSE -> {
                boolean hasClasses = request.getClassIds() != null && !request.getClassIds().isEmpty();
                if (!hasClasses) {
                    throw new BadRequestException("classIds is required for targetMode COURSE");
                }
            }
            case PROGRAM -> {
                if (request.getSpecializationIds() == null || request.getSpecializationIds().isEmpty()) {
                    throw new BadRequestException("specializationIds is required for targetMode PROGRAM");
                }
            }
            case ALL -> { /* no extra fields required */ }
        }
    }

    private String buildTargetPayload(AdminCreateNotificationRequest request) {
        return switch (request.getTargetMode()) {
            case USER_LIST -> jsonCodec.toJsonString(Map.of("userIds",
                    request.getUserIds().stream().map(UUID::toString).toList()));
            case GROUP -> jsonCodec.toJsonString(Map.of("groups", request.getGroups()));
            case COURSE -> {
                Map<String, Object> payload = new LinkedHashMap<>();
                if (request.getClassIds() != null && !request.getClassIds().isEmpty()) {
                    payload.put("classIds", request.getClassIds().stream().map(UUID::toString).toList());
                }
                yield jsonCodec.toJsonString(payload);
            }
            case PROGRAM -> jsonCodec.toJsonString(Map.of("specializationIds",
                    request.getSpecializationIds().stream().map(UUID::toString).toList()));
            case ALL -> jsonCodec.toJsonString(Map.of());
        };
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
                .targetMode(entity.getTargetMode())
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
