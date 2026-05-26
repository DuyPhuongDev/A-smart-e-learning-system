package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.client.CourseManagementInternalClient;
import com.hcmut.lms.notification.dto.request.TeacherCreateNotificationRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.enums.*;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.exception.ForbiddenException;
import com.hcmut.lms.notification.exception.ResourceNotFoundException;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.util.JsonCodec;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class TeacherNotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private UserNotificationRepository userNotificationRepository;
    @Mock private NotificationDispatchService notificationDispatchService;
    @Mock private NotificationOutboxService notificationOutboxService;
    @Mock private CourseManagementInternalClient courseManagementInternalClient;
    @Mock private JsonCodec jsonCodec;

    @InjectMocks
    private TeacherNotificationServiceImpl teacherNotificationService;

    @Test
    void create_shouldReturnResponse_whenAllMyClasses() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE, TeacherNotificationScope.ALL_MY_CLASSES);
        List<UUID> classIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        when(courseManagementInternalClient.getClassIdsByTeacher(teacher.getId())).thenReturn(classIds);
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        AdminNotificationCreateResponse result = teacherNotificationService.create(teacher, request, null);

        assertThat(result.getNotificationId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(notificationDispatchService).dispatch(any());
    }

    @Test
    void create_shouldReturnResponse_whenSpecificClasses() {
        CurrentUserInfo teacher = createTeacher();
        UUID classId1 = UUID.randomUUID();
        UUID classId2 = UUID.randomUUID();
        TeacherCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE, TeacherNotificationScope.SPECIFIC_CLASSES);
        request.setClassIds(List.of(classId1, classId2));

        when(courseManagementInternalClient.getClassIdsByTeacher(teacher.getId()))
                .thenReturn(List.of(classId1, classId2, UUID.randomUUID()));
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        AdminNotificationCreateResponse result = teacherNotificationService.create(teacher, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void create_shouldThrow_whenNoClassesManaged() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE, TeacherNotificationScope.ALL_MY_CLASSES);

        when(courseManagementInternalClient.getClassIdsByTeacher(teacher.getId())).thenReturn(List.of());

        assertThatThrownBy(() -> teacherNotificationService.create(teacher, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not manage any classes");
    }

    @Test
    void create_shouldThrowForbidden_whenUnownedClassInSpecificScope() {
        CurrentUserInfo teacher = createTeacher();
        UUID ownedClass = UUID.randomUUID();
        UUID unownedClass = UUID.randomUUID();
        TeacherCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE, TeacherNotificationScope.SPECIFIC_CLASSES);
        request.setClassIds(List.of(ownedClass, unownedClass));

        when(courseManagementInternalClient.getClassIdsByTeacher(teacher.getId()))
                .thenReturn(List.of(ownedClass));

        assertThatThrownBy(() -> teacherNotificationService.create(teacher, request, null))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not manage");
    }

    @Test
    void create_shouldReturnExisting_whenIdempotencyKeyMatches() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE, TeacherNotificationScope.ALL_MY_CLASSES);
        UUID existingId = UUID.randomUUID();
        NotificationEntity existing = new NotificationEntity();
        existing.setId(existingId);
        existing.setStatus(NotificationStatus.SENT);

        when(notificationRepository.findByCreatedByAndIdempotencyKey(teacher.getId(), "key-1"))
                .thenReturn(Optional.of(existing));

        AdminNotificationCreateResponse result = teacherNotificationService.create(teacher, request, "key-1");

        assertThat(result.getNotificationId()).isEqualTo(existingId);
        verify(notificationRepository, never()).save(any());
        verify(courseManagementInternalClient, never()).getClassIdsByTeacher(any());
    }

    @Test
    void create_shouldNotDispatch_whenDraft() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.DRAFT, TeacherNotificationScope.ALL_MY_CLASSES);

        when(courseManagementInternalClient.getClassIdsByTeacher(teacher.getId())).thenReturn(List.of(UUID.randomUUID()));
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdminNotificationCreateResponse result = teacherNotificationService.create(teacher, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.DRAFT);
        verify(notificationDispatchService, never()).dispatch(any());
    }

    @Test
    void create_shouldThrow_whenSpecificClassesMissingClassIds() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.IMMEDIATE, TeacherNotificationScope.SPECIFIC_CLASSES);
        request.setClassIds(null);

        assertThatThrownBy(() -> teacherNotificationService.create(teacher, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("classIds");
    }

    @Test
    void list_shouldReturnPage() {
        CurrentUserInfo teacher = createTeacher();
        NotificationEntity entity = createNotification(teacher.getId());
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = teacherNotificationService.list(teacher, null, null, null, null, null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void detail_shouldReturnDetail_whenOwner() {
        CurrentUserInfo teacher = createTeacher();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification(teacher.getId());
        entity.setId(id);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));
        when(jsonCodec.toJsonNode(any())).thenReturn(null);

        AdminNotificationDetailResponse result = teacherNotificationService.detail(teacher, id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void detail_shouldThrowForbidden_whenNotOwner() {
        CurrentUserInfo teacher = createTeacher();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification(UUID.randomUUID()); // different owner
        entity.setId(id);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> teacherNotificationService.detail(teacher, id))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void sendNow_shouldDispatch() {
        CurrentUserInfo teacher = createTeacher();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification(teacher.getId());
        entity.setId(id);
        entity.setStatus(NotificationStatus.DRAFT);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        teacherNotificationService.sendNow(teacher, id);

        assertThat(entity.getStatus()).isEqualTo(NotificationStatus.SENT);
        verify(notificationDispatchService).dispatch(entity);
    }

    @Test
    void list_shouldFilterByStatus() {
        CurrentUserInfo teacher = createTeacher();
        NotificationEntity entity = createNotification(teacher.getId());
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = teacherNotificationService.list(teacher, "SENT", null, null, null, null, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void list_shouldFilterByTypeAndKeyword() {
        CurrentUserInfo teacher = createTeacher();
        NotificationEntity entity = createNotification(teacher.getId());
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = teacherNotificationService.list(teacher, null, "ASSIGNMENT_CREATED", null,
                null, null, "keyword", 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void list_shouldFilterByDateAndChannel() {
        CurrentUserInfo teacher = createTeacher();
        NotificationEntity entity = createNotification(teacher.getId());
        when(notificationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(userNotificationRepository.countByNotification_Id(any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndDeliveryStatus(any(), any())).thenReturn(0L);
        when(userNotificationRepository.countByNotification_IdAndReadStatus(any(), any())).thenReturn(0L);
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));

        var result = teacherNotificationService.list(teacher, null, null, "IN_APP",
                "2025-01-01T00:00:00Z", "2025-12-31T23:59:59Z", null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void parseInstantNullable_shouldThrowOnInvalidFormat() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(teacherNotificationService, "parseInstantNullable", "bad-date"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid timestamp");
    }

    @Test
    void parseInstantNullable_shouldReturnNull_whenEmptyOrNull() {
        assertThat((Object) ReflectionTestUtils.invokeMethod(teacherNotificationService, "parseInstantNullable", "")).isNull();
        assertThat((Object) ReflectionTestUtils.invokeMethod(teacherNotificationService, "parseInstantNullable", (Object) null)).isNull();
    }

    @Test
    void cancel_shouldCancel_whenScheduled() {
        CurrentUserInfo teacher = createTeacher();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification(teacher.getId());
        entity.setId(id);
        entity.setStatus(NotificationStatus.SCHEDULED);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        teacherNotificationService.cancel(teacher, id);

        assertThat(entity.getStatus()).isEqualTo(NotificationStatus.CANCELLED);
    }

    @Test
    void validateCreateRequest_shouldThrow_whenScheduledMissingScheduledAt() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.SCHEDULED, TeacherNotificationScope.ALL_MY_CLASSES);
        request.setScheduledAt(null);

        assertThatThrownBy(() -> teacherNotificationService.create(teacher, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("scheduledAt");
    }

    @Test
    void create_shouldReturnScheduledStatus_whenScheduled() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.SCHEDULED, TeacherNotificationScope.ALL_MY_CLASSES);

        when(courseManagementInternalClient.getClassIdsByTeacher(teacher.getId())).thenReturn(List.of(UUID.randomUUID()));
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdminNotificationCreateResponse result = teacherNotificationService.create(teacher, request, null);

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SCHEDULED);
        verify(notificationDispatchService, never()).dispatch(any());
    }

    @Test
    void parseInstantNullable_shouldParseOffsetDateTime() {
        Object result = ReflectionTestUtils.invokeMethod(teacherNotificationService, "parseInstantNullable",
                "2025-06-15T10:30+07:00");
        assertThat(result).isNotNull();
        assertThat(result instanceof java.time.Instant).isTrue();
    }

    @Test
    void validateCreateRequest_shouldThrow_whenScheduledAtInPast() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.SCHEDULED, TeacherNotificationScope.ALL_MY_CLASSES);
        request.setScheduledAt(OffsetDateTime.now().minusDays(1));

        assertThatThrownBy(() -> teacherNotificationService.create(teacher, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("future");
    }

    @Test
    void sendNow_shouldThrow_whenAlreadySent() {
        CurrentUserInfo teacher = createTeacher();
        UUID id = UUID.randomUUID();
        NotificationEntity entity = createNotification(teacher.getId());
        entity.setId(id);
        entity.setStatus(NotificationStatus.SENT);
        when(notificationRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> teacherNotificationService.sendNow(teacher, id))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("DRAFT or SCHEDULED");
    }

    @Test
    void create_shouldThrow_whenExpiresAtBeforeScheduledAt() {
        CurrentUserInfo teacher = createTeacher();
        TeacherCreateNotificationRequest request = createRequest(SendMode.SCHEDULED, TeacherNotificationScope.ALL_MY_CLASSES);
        OffsetDateTime scheduledAt = OffsetDateTime.now().plusDays(2);
        request.setScheduledAt(scheduledAt);
        request.setExpiresAt(scheduledAt.minusDays(1));

        assertThatThrownBy(() -> teacherNotificationService.create(teacher, request, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expiresAt");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    void list_shouldExecuteSpecificationWithAllFilters() {
        CurrentUserInfo teacher = createTeacher();
        NotificationEntity entity = createNotification(teacher.getId());

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

        var result = teacherNotificationService.list(teacher, "SENT", "ASSIGNMENT_CREATED", "IN_APP",
                "2025-01-01T00:00:00Z", "2025-12-31T23:59:59Z", "keyword", 0, 10);

        assertThat(result.getContent()).hasSize(1);
        verify(cb).and(any(Predicate[].class));
    }

    private CurrentUserInfo createTeacher() {
        return CurrentUserInfo.builder()
                .id(UUID.randomUUID())
                .email("teacher@hcmut.edu.vn")
                .role("TEACHER")
                .build();
    }

    private TeacherCreateNotificationRequest createRequest(SendMode sendMode, TeacherNotificationScope scope) {
        TeacherCreateNotificationRequest req = new TeacherCreateNotificationRequest();
        req.setTitle("Test");
        req.setContent("Content");
        req.setType(NotificationType.ASSIGNMENT_CREATED);
        req.setPriority(NotificationPriority.MEDIUM);
        req.setScope(scope);
        req.setChannels(Set.of(NotificationChannel.IN_APP));
        req.setSendMode(sendMode);
        if (sendMode == SendMode.SCHEDULED) {
            req.setScheduledAt(OffsetDateTime.now().plusDays(1));
        }
        return req;
    }

    private NotificationEntity createNotification(UUID createdBy) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Test");
        entity.setContent("Content");
        entity.setType(NotificationType.ASSIGNMENT_CREATED);
        entity.setPriority(NotificationPriority.MEDIUM);
        entity.setStatus(NotificationStatus.SENT);
        entity.setCreatedBy(createdBy);
        entity.setTargetMode(TargetMode.COURSE);
        entity.setTargetPayload("{}");
        entity.setChannels("[\"IN_APP\"]");
        entity.setMetadata("{}");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        return entity;
    }
}
