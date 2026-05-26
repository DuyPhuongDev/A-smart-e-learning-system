package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.AdminCreateNotificationRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationListItemResponse;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.enums.*;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.exception.ResourceNotFoundException;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.util.JsonCodec;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminNotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserNotificationRepository userNotificationRepository;
    @Mock private NotificationDispatchService notificationDispatchService;
    @Mock private NotificationOutboxService notificationOutboxService;
    @Mock private JsonCodec jsonCodec;

    @InjectMocks
    private AdminNotificationServiceImpl adminNotificationService;

    @Test
    void create_shouldReturnResponse_whenSendNow() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);

        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, null);

        assertThat(result.getNotificationId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(notificationDispatchService).dispatch(any());
    }

    @Test
    void create_shouldReturnScheduledStatus_whenScheduled() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createScheduledRequest();

        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SCHEDULED);
        verify(notificationDispatchService, never()).dispatch(any());
    }

    @Test
    void create_shouldReturnDraftStatus_whenDraft() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.DRAFT);

        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.DRAFT);
        verify(notificationDispatchService, never()).dispatch(any());
    }

    @Test
    void create_shouldReturnExisting_whenIdempotencyKeyMatches() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        UUID existingId = UUID.randomUUID();
        NotificationEntity existing = new NotificationEntity();
        existing.setId(existingId);
        existing.setStatus(NotificationStatus.SENT);

        when(notificationRepository.findByCreatedByAndIdempotencyKey(admin.getId(), "key-1"))
                .thenReturn(Optional.of(existing));

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, "key-1");

        assertThat(result.getNotificationId()).isEqualTo(existingId);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void create_shouldThrow_whenScheduledMissingScheduledAt() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.SCHEDULED);
        request.setScheduledAt(null);

        assertThatThrownBy(() -> adminNotificationService.create(admin, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("scheduledAt");
    }

    @Test
    void create_shouldThrow_whenScheduledAtInPast() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.SCHEDULED);
        request.setScheduledAt(OffsetDateTime.now().minusDays(1));

        assertThatThrownBy(() -> adminNotificationService.create(admin, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("future");
    }

    @Test
    void create_shouldThrow_whenExpiresAtBeforeScheduledAt() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.SCHEDULED);
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusDays(2);
        request.setScheduledAt(scheduledAt);
        request.setExpiresAt(scheduledAt.minusDays(1));

        assertThatThrownBy(() -> adminNotificationService.create(admin, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expiresAt");
    }

    @Test
    void create_shouldThrow_whenUserListMissingUserIds() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.USER_LIST);
        request.setUserIds(null);

        assertThatThrownBy(() -> adminNotificationService.create(admin, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("userIds");
    }

    @Test
    void create_shouldThrow_whenGroupMissingGroups() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.GROUP);
        request.setGroups(null);

        assertThatThrownBy(() -> adminNotificationService.create(admin, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("groups");
    }

    @Test
    void create_shouldThrow_whenCourseMissingClassIds() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.COURSE);
        request.setClassIds(null);

        assertThatThrownBy(() -> adminNotificationService.create(admin, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("classIds");
    }

    @Test
    void create_shouldThrow_whenProgramMissingSpecializationIds() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.PROGRAM);
        request.setSpecializationIds(null);

        assertThatThrownBy(() -> adminNotificationService.create(admin, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("specializationIds");
    }

    @Test
    void list_shouldReturnFilteredPage() {
        CurrentUserInfo admin = createAdmin();
        NotificationEntity entity = createNotification();
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = adminNotificationService.list(admin, "SENT", "SYSTEM_ANNOUNCEMENT", "IN_APP",
                null, null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void list_shouldThrowForbidden_whenNotAdmin() {
        CurrentUserInfo student = createStudent();

        assertThatThrownBy(() -> adminNotificationService.list(student, null, null, null,
                null, null, null, 0, 10))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Admin");
    }

    @Test
    void detail_shouldReturnDetail() {
        CurrentUserInfo admin = createAdmin();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification();
        entity.setId(id);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));
        when(jsonCodec.toJsonNode(any())).thenReturn(null);

        AdminNotificationDetailResponse result = adminNotificationService.detail(admin, id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void detail_shouldThrow_whenNotFound() {
        CurrentUserInfo admin = createAdmin();
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminNotificationService.detail(admin, id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void sendNow_shouldDispatch_whenDraft() {
        CurrentUserInfo admin = createAdmin();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification();
        entity.setId(id);
        entity.setStatus(NotificationStatus.DRAFT);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        adminNotificationService.sendNow(admin, id);

        assertThat(entity.getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(notificationDispatchService).dispatch(entity);
    }

    @Test
    void sendNow_shouldThrow_whenAlreadySent() {
        CurrentUserInfo admin = createAdmin();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification();
        entity.setId(id);
        entity.setStatus(NotificationStatus.SENT);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> adminNotificationService.sendNow(admin, id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("DRAFT or SCHEDULED");
    }

    @Test
    void cancel_shouldCancel_whenScheduled() {
        CurrentUserInfo admin = createAdmin();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification();
        entity.setId(id);
        entity.setStatus(NotificationStatus.SCHEDULED);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        adminNotificationService.cancel(admin, id);

        assertThat(entity.getStatus()).isEqualTo(NotificationStatus.CANCELLED);
    }

    @Test
    void list_shouldFilterByDateRange() {
        CurrentUserInfo admin = createAdmin();
        NotificationEntity entity = createNotification();
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = adminNotificationService.list(admin, null, null, null,
                "2025-01-01T00:00:00Z", "2025-12-31T23:59:59Z", null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void list_shouldFilterByKeyword() {
        CurrentUserInfo admin = createAdmin();
        NotificationEntity entity = createNotification();
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = adminNotificationService.list(admin, null, null, null,
                null, null, "urgent", 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void list_shouldFilterByChannel() {
        CurrentUserInfo admin = createAdmin();
        NotificationEntity entity = createNotification();
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("EMAIL"));

        var result = adminNotificationService.list(admin, null, null, "EMAIL",
                null, null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void parseInstantNullable_shouldThrowOnInvalidFormat() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(adminNotificationService, "parseInstantNullable", "not-a-date"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid timestamp");
    }

    @Test
    void parseInstantNullable_shouldReturnNull_whenEmpty() {
        Object result = ReflectionTestUtils.invokeMethod(adminNotificationService, "parseInstantNullable", "");
        assertThat((Object) result).isNull();
    }

    @Test
    void parseInstantNullable_shouldReturnNull_whenNull() {
        Object result = ReflectionTestUtils.invokeMethod(adminNotificationService, "parseInstantNullable", (Object) null);
        assertThat((Object) result).isNull();
    }

    @Test
    void parseInstantNullable_shouldParseOffsetDateTime() {
        Object result = ReflectionTestUtils.invokeMethod(adminNotificationService, "parseInstantNullable",
                "2025-06-15T10:30+07:00");
        assertThat(result).isNotNull();
        assertThat(result instanceof Instant).isTrue();
    }

    @Test
    void create_shouldBuildTargetPayloadForUserList() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.USER_LIST);
        request.setUserIds(Set.of(UUID.randomUUID()));

        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void create_shouldBuildTargetPayloadForGroup() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.GROUP);
        request.setGroups(Set.of("STUDENT"));

        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void create_shouldBuildTargetPayloadForCourse() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.COURSE);
        request.setClassIds(List.of(UUID.randomUUID()));

        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void create_shouldBuildTargetPayloadForProgram() {
        CurrentUserInfo admin = createAdmin();
        AdminCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE);
        request.setTargetMode(TargetMode.PROGRAM);
        request.setSpecializationIds(List.of(UUID.randomUUID()));

        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        AdminNotificationCreateResponse result = adminNotificationService.create(admin, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void cancel_shouldThrow_whenNotScheduled() {
        CurrentUserInfo admin = createAdmin();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification();
        entity.setId(id);
        entity.setStatus(NotificationStatus.SENT);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> adminNotificationService.cancel(admin, id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("SCHEDULED");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void list_shouldExecuteSpecificationWithAllFilters() {
        CurrentUserInfo admin = createAdmin();
        NotificationEntity entity = createNotification();

        Root<NotificationEntity> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Predicate pred = mock(Predicate.class);
        Path<Object> path = mock(Path.class);
        Expression<String> expr = mock(Expression.class);

        lenient().when(root.get(anyString())).thenReturn((Path) path);
        lenient().when(cb.equal(any(), any())).thenReturn(pred);
        lenient().when(cb.like(any(), anyString())).thenReturn(pred);
        lenient().when(cb.lower(any())).thenReturn((Expression) expr);
        lenient().when(cb.or(any(Predicate[].class))).thenReturn(pred);
        lenient().when(cb.and(any(Predicate[].class))).thenReturn(pred);
        lenient().when(cb.greaterThanOrEqualTo(any(Expression.class), any(Instant.class))).thenReturn(pred);
        lenient().when(cb.lessThanOrEqualTo(any(Expression.class), any(Instant.class))).thenReturn(pred);

        doAnswer(inv -> {
            Specification<NotificationEntity> spec = inv.getArgument(0);
            spec.toPredicate(root, query, cb);
            return new PageImpl<>(List.of(entity));
        }).when(notificationRepository).findAll(any(Specification.class), any(Pageable.class));

        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = adminNotificationService.list(admin, "SENT", "SYSTEM_ANNOUNCEMENT", "IN_APP",
                "2025-01-01T00:00:00Z", "2025-12-31T23:59:59Z", "keyword", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(cb).and(any(Predicate[].class));
    }

    private CurrentUserInfo createAdmin() {
        return CurrentUserInfo.builder()
                .id(UUID.randomUUID())
                .email("admin@hcmut.edu.vn")
                .role("ADMIN")
                .build();
    }

    private CurrentUserInfo createStudent() {
        return CurrentUserInfo.builder()
                .id(UUID.randomUUID())
                .email("student@hcmut.edu.vn")
                .role("STUDENT")
                .build();
    }

    private AdminCreateNotificationRequest createRequest(SendMode sendMode) {
        AdminCreateNotificationRequest req = new AdminCreateNotificationRequest();
        req.setTitle("Test Notification");
        req.setContent("Test Content");
        req.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
        req.setPriority(NotificationPriority.MEDIUM);
        req.setTargetMode(TargetMode.ALL);
        req.setChannels(Set.of(NotificationChannel.IN_APP));
        req.setSendMode(sendMode);
        return req;
    }

    private AdminCreateNotificationRequest createScheduledRequest() {
        AdminCreateNotificationRequest req = createRequest(SendMode.SCHEDULED);
        req.setScheduledAt(OffsetDateTime.now().plusDays(1));
        return req;
    }

    private NotificationEntity createNotification() {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Test");
        entity.setContent("Content");
        entity.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
        entity.setPriority(NotificationPriority.MEDIUM);
        entity.setStatus(NotificationStatus.SENT);
        entity.setTargetMode(TargetMode.ALL);
        entity.setTargetPayload("{}");
        entity.setChannels("[\"IN_APP\"]");
        entity.setMetadata("{}");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}
