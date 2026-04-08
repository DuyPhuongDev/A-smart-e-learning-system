package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.response.InboxNotificationResponse;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.ReadStatus;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.exception.ResourceNotFoundException;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.UserInboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInboxServiceImpl implements UserInboxService {

    private final UserNotificationRepository userNotificationRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InboxNotificationResponse> getMyInbox(CurrentUserInfo currentUser, String status, int page, int size) {
        ReadStatus readStatus = parseReadStatus(status);
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<UserNotificationEntity> result = userNotificationRepository
                .findInboxByUserId(currentUser.getId(), readStatus, Instant.now(), pageable);

        List<InboxNotificationResponse> content = result.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<InboxNotificationResponse>builder()
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
    public long getUnreadCount(CurrentUserInfo currentUser) {
        return userNotificationRepository.countUnread(currentUser.getId(), Instant.now());
    }

    @Override
    @Transactional
    public void markRead(CurrentUserInfo currentUser, UUID userNotificationId) {
        UserNotificationEntity entity = userNotificationRepository
                .findByIdAndUserId(userNotificationId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User notification not found: " + userNotificationId));

        if (entity.getReadStatus() == ReadStatus.UNREAD) {
            entity.setReadStatus(ReadStatus.READ);
            entity.setReadAt(Instant.now());
            userNotificationRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void markReadAll(CurrentUserInfo currentUser) {
        userNotificationRepository.markAllRead(currentUser.getId(), Instant.now());
    }

    private InboxNotificationResponse toResponse(UserNotificationEntity entity) {
        NotificationEntity notification = entity.getNotification();
        return InboxNotificationResponse.builder()
                .userNotificationId(entity.getId())
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .priority(notification.getPriority())
                .channel(entity.getChannel())
                .read(entity.getReadStatus() == ReadStatus.READ)
                .readAt(entity.getReadAt())
                .deliveredAt(entity.getDeliveredAt())
                .createdAt(notification.getCreatedAt())
                .expiresAt(notification.getExpiresAt())
                .build();
    }

    private ReadStatus parseReadStatus(String status) {
        if (!StringUtils.hasText(status) || "all".equalsIgnoreCase(status)) {
            return null;
        }
        if ("unread".equalsIgnoreCase(status)) {
            return ReadStatus.UNREAD;
        }
        if ("read".equalsIgnoreCase(status)) {
            return ReadStatus.READ;
        }
        throw new BadRequestException("status must be all|unread|read");
    }
}
