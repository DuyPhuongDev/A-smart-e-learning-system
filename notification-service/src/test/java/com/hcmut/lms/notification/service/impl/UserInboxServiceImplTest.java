package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.response.InboxNotificationResponse;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.ReadStatus;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.exception.ForbiddenException;
import com.hcmut.lms.notification.exception.ResourceNotFoundException;
import com.hcmut.lms.notification.enums.NotificationStatus;
import com.hcmut.lms.notification.enums.TargetMode;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserInboxServiceImplTest {

    @Mock private UserNotificationRepository userNotificationRepository;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private UserInboxServiceImpl userInboxService;

    @Test
    void getMyInbox_shouldReturnPage() throws JsonProcessingException {
        CurrentUserInfo user = createUser();
        UserNotificationEntity entity = createUserNotification();
        when(userNotificationRepository.findInboxByUserId(eq(user.getId()), isNull(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(entity), Pageable.unpaged(), 1));
        when(objectMapper.readTree(anyString())).thenReturn(null);

        var result = userInboxService.getMyInbox(user, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getMyInbox_shouldReturnEmptyPage() {
        CurrentUserInfo user = createUser();
        when(userNotificationRepository.findInboxByUserId(eq(user.getId()), isNull(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), Pageable.unpaged(), 0));

        var result = userInboxService.getMyInbox(user, null, 0, 10);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void getMyInbox_shouldWrapJsonException_asRuntime() throws JsonProcessingException {
        CurrentUserInfo user = createUser();
        UserNotificationEntity entity = createUserNotification();
        when(userNotificationRepository.findInboxByUserId(eq(user.getId()), isNull(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(entity), Pageable.unpaged(), 1));
        when(objectMapper.readTree(anyString())).thenThrow(new JsonProcessingException("bad json") {});

        assertThatThrownBy(() -> userInboxService.getMyInbox(user, null, 0, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to parse");
    }

    @Test
    void getMyInbox_shouldFilterByReadStatus() {
        CurrentUserInfo user = createUser();
        when(userNotificationRepository.findInboxByUserId(eq(user.getId()), eq(ReadStatus.UNREAD), any(), any()))
                .thenReturn(new PageImpl<>(List.of(), Pageable.unpaged(), 0));

        userInboxService.getMyInbox(user, "unread", 0, 10);

        verify(userNotificationRepository).findInboxByUserId(eq(user.getId()), eq(ReadStatus.UNREAD), any(), any());
    }

    @Test
    void getMyInbox_shouldThrowBadRequest_whenInvalidStatus() {
        CurrentUserInfo user = createUser();

        assertThatThrownBy(() -> userInboxService.getMyInbox(user, "invalid", 0, 10))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("all|unread|read");
    }

    @Test
    void getUnreadCount_shouldReturnCount() {
        CurrentUserInfo user = createUser();
        when(userNotificationRepository.countUnread(eq(user.getId()), any())).thenReturn(5L);

        long count = userInboxService.getUnreadCount(user);

        assertThat(count).isEqualTo(5L);
    }

    @Test
    void markRead_shouldUpdateStatus_whenUnread() {
        CurrentUserInfo user = createUser();
        UUID notiId = UUID.randomUUID();
        UserNotificationEntity entity = createUserNotification();
        entity.setReadStatus(ReadStatus.UNREAD);
        when(userNotificationRepository.findByIdAndUserId(notiId, user.getId()))
                .thenReturn(Optional.of(entity));

        userInboxService.markRead(user, notiId);

        assertThat(entity.getReadStatus()).isEqualTo(ReadStatus.READ);
        assertThat(entity.getReadAt()).isNotNull();
        verify(userNotificationRepository).save(entity);
    }

    @Test
    void markRead_shouldNotUpdate_whenAlreadyRead() {
        CurrentUserInfo user = createUser();
        UUID notiId = UUID.randomUUID();
        UserNotificationEntity entity = createUserNotification();
        entity.setReadStatus(ReadStatus.READ);
        when(userNotificationRepository.findByIdAndUserId(notiId, user.getId()))
                .thenReturn(Optional.of(entity));

        userInboxService.markRead(user, notiId);

        verify(userNotificationRepository, never()).save(any());
    }

    @Test
    void markRead_shouldThrow_whenNotFound() {
        CurrentUserInfo user = createUser();
        UUID notiId = UUID.randomUUID();
        when(userNotificationRepository.findByIdAndUserId(notiId, user.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userInboxService.markRead(user, notiId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void markReadAll_shouldCallRepository() {
        CurrentUserInfo user = createUser();

        userInboxService.markReadAll(user);

        verify(userNotificationRepository).markAllRead(eq(user.getId()), any());
    }

    private CurrentUserInfo createUser() {
        return CurrentUserInfo.builder()
                .id(UUID.randomUUID())
                .email("student@hcmut.edu.vn")
                .role("STUDENT")
                .build();
    }

    private UserNotificationEntity createUserNotification() {
        NotificationEntity notification = new NotificationEntity();
        notification.setId(UUID.randomUUID());
        notification.setTitle("Test Notification");
        notification.setContent("Test Content");
        notification.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
        notification.setPriority(NotificationPriority.MEDIUM);
        notification.setMetadata("{}");
        notification.setCreatedAt(Instant.now());

        UserNotificationEntity entity = new UserNotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setNotification(notification);
        entity.setUserId(UUID.randomUUID());
        entity.setChannel(NotificationChannel.IN_APP);
        entity.setReadStatus(ReadStatus.UNREAD);
        entity.setDeliveredAt(Instant.now());
        entity.setCreatedAt(Instant.now());
        return entity;
    }
}
