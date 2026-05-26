package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcmut.lms.common.event.NotificationTargetType;
import com.hcmut.lms.common.event.SimpleNotificationEvent;
import com.hcmut.lms.notification.entity.NotificationDlqEntity;
import com.hcmut.lms.notification.entity.NotificationRuleEntity;
import com.hcmut.lms.notification.enums.*;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.repository.NotificationDlqRepository;
import com.hcmut.lms.notification.repository.NotificationRepository;
import com.hcmut.lms.notification.repository.NotificationRuleRepository;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.util.JsonCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationInboundServiceImplTest {

    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationRuleRepository notificationRuleRepository;
    @Mock private NotificationDlqRepository notificationDlqRepository;
    @Mock private NotificationDispatchService notificationDispatchService;
    @Mock private NotificationOutboxService notificationOutboxService;
    @Mock private JsonCodec jsonCodec;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationInboundServiceImpl notificationInboundService;

    private final ObjectMapper realMapper = new ObjectMapper();

    @Test
    void handleInboundEvent_shouldCreateAndDispatch_whenValid() throws Exception {
        String eventJson = realMapper.writeValueAsString(createValidEvent());
        SimpleNotificationEvent event = createValidEvent();
        when(objectMapper.readValue(eventJson, SimpleNotificationEvent.class)).thenReturn(event);

        NotificationRuleEntity rule = createRule();
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(rule));
        when(notificationRepository.findBySourceServiceAndSourceEventIdAndType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        notificationInboundService.handleInboundEvent(eventJson);

        verify(notificationDispatchService).dispatch(any());
    }

    @Test
    void handleInboundEvent_shouldThrowBadRequest_whenInvalidJson() throws Exception {
        when(objectMapper.readValue("bad json", SimpleNotificationEvent.class))
                .thenThrow(new RuntimeException("parse error"));

        assertThatThrownBy(() -> notificationInboundService.handleInboundEvent("bad json"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid notification event");
    }

    @Test
    void handleInboundEvent_shouldThrowBadRequest_whenNoSemanticType() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .messageId("msg-1")
                .sourceService("course-management")
                .targetType(NotificationTargetType.GLOBAL)
                .build();
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);

        assertThatThrownBy(() -> notificationInboundService.handleInboundEvent(json))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("semanticType");
    }

    @Test
    void handleInboundEvent_shouldThrowBadRequest_whenNoMessageId() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .semanticType("ASSIGNMENT_CREATED")
                .sourceService("course-management")
                .targetType(NotificationTargetType.GLOBAL)
                .build();
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);

        assertThatThrownBy(() -> notificationInboundService.handleInboundEvent(json))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("messageId");
    }

    @Test
    void handleInboundEvent_shouldSkip_whenSelfOriginated() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .semanticType("ASSIGNMENT_CREATED")
                .messageId("msg-1")
                .sourceService("notification-service")
                .targetType(NotificationTargetType.GLOBAL)
                .build();
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);

        notificationInboundService.handleInboundEvent(json);

        verify(notificationRuleRepository, never()).findById(any());
    }

    @Test
    void handleInboundEvent_shouldSkip_whenNoRuleFound() throws Exception {
        String eventJson = realMapper.writeValueAsString(createValidEvent());
        SimpleNotificationEvent event = createValidEvent();
        when(objectMapper.readValue(eventJson, SimpleNotificationEvent.class)).thenReturn(event);
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.empty());

        notificationInboundService.handleInboundEvent(eventJson);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void handleInboundEvent_shouldSkip_whenRuleDisabled() throws Exception {
        String eventJson = realMapper.writeValueAsString(createValidEvent());
        SimpleNotificationEvent event = createValidEvent();
        when(objectMapper.readValue(eventJson, SimpleNotificationEvent.class)).thenReturn(event);

        NotificationRuleEntity rule = createRule();
        rule.setEnabled(false);
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(rule));

        notificationInboundService.handleInboundEvent(eventJson);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void handleInboundEvent_shouldSkip_whenDuplicate() throws Exception {
        String eventJson = realMapper.writeValueAsString(createValidEvent());
        SimpleNotificationEvent event = createValidEvent();
        when(objectMapper.readValue(eventJson, SimpleNotificationEvent.class)).thenReturn(event);

        NotificationRuleEntity rule = createRule();
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(rule));
        when(notificationRepository.findBySourceServiceAndSourceEventIdAndType(
                eq("course-management"), eq("msg-1"), eq(NotificationType.ASSIGNMENT_CREATED)))
                .thenReturn(Optional.of(new com.hcmut.lms.notification.entity.NotificationEntity()));

        notificationInboundService.handleInboundEvent(eventJson);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void handleInboundEvent_shouldThrow_whenTargetTypeNull() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .semanticType("ASSIGNMENT_CREATED")
                .messageId("msg-1")
                .sourceService("course-management")
                .build();
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);

        NotificationRuleEntity rule = createRule();
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(rule));
        when(notificationRepository.findBySourceServiceAndSourceEventIdAndType(any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationInboundService.handleInboundEvent(json))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("targetType");
    }

    @Test
    void handleInboundEvent_shouldThrow_whenTargetIdMissing_forUserTarget() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .semanticType("ASSIGNMENT_CREATED")
                .messageId("msg-1")
                .sourceService("course-management")
                .targetType(NotificationTargetType.USER)
                .build(); // no targetId
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);

        NotificationRuleEntity rule = createRule();
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(rule));
        when(notificationRepository.findBySourceServiceAndSourceEventIdAndType(any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationInboundService.handleInboundEvent(json))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("targetId");
    }

    @Test
    void handleInboundEvent_shouldUseFallbackTitleAndContent() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .semanticType("ASSIGNMENT_CREATED")
                .messageId("msg-1")
                .sourceService("course-management")
                .targetType(NotificationTargetType.GLOBAL)
                .build(); // no title/content
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);

        NotificationRuleEntity rule = createRule();
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(rule));
        when(notificationRepository.findBySourceServiceAndSourceEventIdAndType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> {
            com.hcmut.lms.notification.entity.NotificationEntity e = inv.getArgument(0);
            assertThat(e.getTitle()).isEqualTo("Bài tập mới");
            return e;
        });
        doNothing().when(notificationDispatchService).dispatch(any());

        notificationInboundService.handleInboundEvent(json);
    }

    @Test
    void handleInboundEvent_shouldUseSemanticTypeAsEventType_whenTypeAliasProvided() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .typeAlias("ASSIGNMENT_CREATED")
                .messageId("msg-2")
                .sourceService("course-management")
                .targetType(NotificationTargetType.GLOBAL)
                .build();
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);

        NotificationRuleEntity rule = createRule();
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(rule));
        when(notificationRepository.findBySourceServiceAndSourceEventIdAndType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        notificationInboundService.handleInboundEvent(json);

        verify(notificationDispatchService).dispatch(any());
    }

    @Test
    void handleInboundEvent_shouldMapCourseTargetType_toCourseMode() throws Exception {
        SimpleNotificationEvent event = SimpleNotificationEvent.builder()
                .semanticType("ASSIGNMENT_CREATED")
                .messageId("msg-1")
                .sourceService("course-management")
                .targetType(NotificationTargetType.COURSE)
                .targetId(UUID.randomUUID().toString())
                .build();
        String json = realMapper.writeValueAsString(event);
        when(objectMapper.readValue(json, SimpleNotificationEvent.class)).thenReturn(event);
        when(notificationRuleRepository.findById("ASSIGNMENT_CREATED")).thenReturn(Optional.of(createRule()));
        when(notificationRepository.findBySourceServiceAndSourceEventIdAndType(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(notificationDispatchService).dispatch(any());

        notificationInboundService.handleInboundEvent(json);

        ArgumentCaptor<com.hcmut.lms.notification.entity.NotificationEntity> captor =
                ArgumentCaptor.forClass(com.hcmut.lms.notification.entity.NotificationEntity.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getTargetMode()).isEqualTo(TargetMode.COURSE);
    }

    @Test
    void recordInboundFailure_shouldCreateDlqEntity() {
        notificationInboundService.recordInboundFailure("test-topic", "key-1", "{}", "serialization error");

        ArgumentCaptor<NotificationDlqEntity> captor = ArgumentCaptor.forClass(NotificationDlqEntity.class);
        verify(notificationDlqRepository).save(captor.capture());
        assertThat(captor.getValue().getErrorMessage()).isEqualTo("serialization error");
        assertThat(captor.getValue().getEventKey()).isEqualTo("key-1");
    }

    @Test
    void recordInboundFailure_shouldNormalizePayload_whenInvalidJson() throws Exception {
        when(objectMapper.readTree("not-json")).thenThrow(new RuntimeException("bad json"));
        when(jsonCodec.toJsonString(any())).thenReturn("{\"rawPayload\":\"not-json\"}");

        notificationInboundService.recordInboundFailure("topic", "k", "not-json", "error");

        ArgumentCaptor<NotificationDlqEntity> captor = ArgumentCaptor.forClass(NotificationDlqEntity.class);
        verify(notificationDlqRepository).save(captor.capture());
        assertThat(captor.getValue().getPayload()).contains("rawPayload");
    }

    @Test
    void recordInboundFailure_shouldUseEmptyPayload_whenNull() {
        when(jsonCodec.toJsonString(any())).thenReturn("{}");
        notificationInboundService.recordInboundFailure("topic", "k", null, "error");

        ArgumentCaptor<NotificationDlqEntity> captor = ArgumentCaptor.forClass(NotificationDlqEntity.class);
        verify(notificationDlqRepository).save(captor.capture());
        assertThat(captor.getValue().getPayload()).isEqualTo("{}");
    }

    @Test
    void recordInboundFailure_shouldUseGeneratedKey_whenKeyEmpty() {
        notificationInboundService.recordInboundFailure("topic", null, "{}", "error");

        ArgumentCaptor<NotificationDlqEntity> captor = ArgumentCaptor.forClass(NotificationDlqEntity.class);
        verify(notificationDlqRepository).save(captor.capture());
        assertThat(captor.getValue().getEventKey()).isNotNull();
    }

    private SimpleNotificationEvent createValidEvent() {
        return SimpleNotificationEvent.builder()
                .semanticType("ASSIGNMENT_CREATED")
                .messageId("msg-1")
                .sourceService("course-management")
                .targetType(NotificationTargetType.GLOBAL)
                .title("New Assignment")
                .content("A new assignment has been posted")
                .build();
    }

    private NotificationRuleEntity createRule() {
        NotificationRuleEntity rule = new NotificationRuleEntity();
        rule.setEventType("ASSIGNMENT_CREATED");
        rule.setEnabled(true);
        rule.setNotificationType(NotificationType.ASSIGNMENT_CREATED);
        rule.setChannels("[\"IN_APP\"]");
        rule.setPriority(NotificationPriority.MEDIUM);
        rule.setFrequency(NotificationFrequency.IMMEDIATE);
        rule.setTargetMode(TargetMode.ALL);
        rule.setTargetPayload("{}");
        return rule;
    }
}
