package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hcmut.lms.notification.client.LearningInternalClient;
import com.hcmut.lms.notification.client.UserManagementInternalClient;
import com.hcmut.lms.notification.client.dto.*;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationPreferenceEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.*;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.NotificationAsyncEmailService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.service.NotificationPreferenceService;
import com.hcmut.lms.notification.service.SseNotificationBroadcaster;
import com.hcmut.lms.notification.util.JsonCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationDispatchServiceImplTest {

    @Mock private UserNotificationRepository userNotificationRepository;
    @Mock private NotificationPreferenceService notificationPreferenceService;
    @Mock private UserManagementInternalClient userManagementInternalClient;
    @Mock private LearningInternalClient learningInternalClient;
    @Mock private SseNotificationBroadcaster sseNotificationBroadcaster;
    @Mock private NotificationAsyncEmailService notificationAsyncEmailService;
    @Mock private NotificationOutboxService notificationOutboxService;
    @Mock private JsonCodec jsonCodec;

    @InjectMocks
    private NotificationDispatchServiceImpl notificationDispatchService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void dispatch_shouldDeliverInAppToRecipients() {
        NotificationEntity notification = createNotification(TargetMode.ALL, "{}", "[\"IN_APP\"]");
        UUID userId = UUID.randomUUID();
        InternalUserSummaryResponse userSummary = new InternalUserSummaryResponse();
        userSummary.setId(userId);

        stubChannels(notification);
        when(jsonCodec.toJsonNode("{}")).thenReturn(mapper.createObjectNode());
        when(userManagementInternalClient.resolveUsers(any())).thenReturn(List.of(userSummary));

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(sseNotificationBroadcaster).broadcast(eq(userId), any());
    }

    @Test
    void dispatch_shouldDoNothing_whenNoRecipients() {
        NotificationEntity notification = createNotification(TargetMode.USER_LIST, "{\"userIds\":[]}", "[\"IN_APP\"]");
        ObjectNode payload = mapper.createObjectNode();
        payload.putArray("userIds");
        when(jsonCodec.toJsonNode("{\"userIds\":[]}")).thenReturn(payload);

        notificationDispatchService.dispatch(notification);

        verify(userNotificationRepository, never()).save(any());
    }

    @Test
    void dispatch_shouldSetEmailDigest_whenFrequencyIsDigestDaily() {
        NotificationEntity notification = createNotification(TargetMode.ALL, "{}", "[\"EMAIL\"]");
        UUID userId = UUID.randomUUID();
        InternalUserSummaryResponse userSummary = new InternalUserSummaryResponse();
        userSummary.setId(userId);

        stubChannels(notification);
        when(jsonCodec.toJsonNode("{}")).thenReturn(mapper.createObjectNode());
        when(userManagementInternalClient.resolveUsers(any())).thenReturn(List.of(userSummary));

        NotificationPreferenceEntity pref = createPreference(userId, NotificationType.SYSTEM_ANNOUNCEMENT);
        pref.setFrequency(NotificationFrequency.DIGEST_DAILY);
        Map<NotificationType, NotificationPreferenceEntity> prefMap = Map.of(NotificationType.SYSTEM_ANNOUNCEMENT, pref);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        ArgumentCaptor<UserNotificationEntity> captor = ArgumentCaptor.forClass(UserNotificationEntity.class);
        verify(userNotificationRepository, atLeastOnce()).save(captor.capture());
        UserNotificationEntity saved = captor.getAllValues().stream()
                .filter(u -> u.getChannel() == NotificationChannel.EMAIL)
                .findFirst().orElseThrow();
        assertThat(saved.getDeferReason()).isEqualTo(DeferReason.DIGEST);
    }

    @Test
    void dispatch_shouldScheduleEmailDelivery_whenImmediateFrequency() {
        NotificationEntity notification = createNotification(TargetMode.ALL, "{}", "[\"EMAIL\"]");
        UUID userId = UUID.randomUUID();
        InternalUserSummaryResponse userSummary = new InternalUserSummaryResponse();
        userSummary.setId(userId);

        stubChannels(notification);
        when(jsonCodec.toJsonNode("{}")).thenReturn(mapper.createObjectNode());
        when(userManagementInternalClient.resolveUsers(any())).thenReturn(List.of(userSummary));

        NotificationPreferenceEntity pref = createPreference(userId, NotificationType.SYSTEM_ANNOUNCEMENT);
        pref.setFrequency(NotificationFrequency.IMMEDIATE);
        Map<NotificationType, NotificationPreferenceEntity> prefMap = Map.of(NotificationType.SYSTEM_ANNOUNCEMENT, pref);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(notificationAsyncEmailService).deliverWhenReady(any());
    }

    @Test
    void dispatch_shouldSkipDisabledChannel() {
        NotificationEntity notification = createNotification(TargetMode.ALL, "{}", "[\"EMAIL\"]");
        UUID userId = UUID.randomUUID();
        InternalUserSummaryResponse userSummary = new InternalUserSummaryResponse();
        userSummary.setId(userId);

        stubChannels(notification);
        when(jsonCodec.toJsonNode("{}")).thenReturn(mapper.createObjectNode());
        when(userManagementInternalClient.resolveUsers(any())).thenReturn(List.of(userSummary));

        NotificationPreferenceEntity pref = createPreference(userId, NotificationType.SYSTEM_ANNOUNCEMENT);
        pref.setEmailEnabled(false);
        Map<NotificationType, NotificationPreferenceEntity> prefMap = Map.of(NotificationType.SYSTEM_ANNOUNCEMENT, pref);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);

        notificationDispatchService.dispatch(notification);

        verify(userNotificationRepository, never()).save(any());
    }

    @Test
    void dispatch_shouldHandlePushChannel_asPending() {
        NotificationEntity notification = createNotification(TargetMode.ALL, "{}", "[\"PUSH\"]");
        UUID userId = UUID.randomUUID();
        InternalUserSummaryResponse userSummary = new InternalUserSummaryResponse();
        userSummary.setId(userId);

        stubChannels(notification);
        when(jsonCodec.toJsonNode("{}")).thenReturn(mapper.createObjectNode());
        when(userManagementInternalClient.resolveUsers(any())).thenReturn(List.of(userSummary));

        NotificationPreferenceEntity pref = createPreference(userId, NotificationType.SYSTEM_ANNOUNCEMENT);
        pref.setPushEnabled(true);
        Map<NotificationType, NotificationPreferenceEntity> prefMap = Map.of(NotificationType.SYSTEM_ANNOUNCEMENT, pref);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(notificationAsyncEmailService, never()).deliverWhenReady(any());
        verify(sseNotificationBroadcaster, never()).broadcast(any(), any());
    }

    @Test
    void resolveRecipients_shouldResolveByCourseWithClassIds() {
        UUID classId = UUID.randomUUID();
        ObjectNode payload = mapper.createObjectNode();
        ArrayNode arr = payload.putArray("classIds");
        arr.add(classId.toString());

        NotificationEntity notification = createNotification(TargetMode.COURSE, payload.toString(), "[\"IN_APP\"]");
        UUID studentId = UUID.randomUUID();

        stubChannels(notification);
        when(jsonCodec.toJsonNode(payload.toString())).thenReturn(payload);

        ClassStudentIdsResponse classResponse = new ClassStudentIdsResponse();
        classResponse.setStudentIds(List.of(studentId));
        BatchClassStudentIdsResponse batchResponse = new BatchClassStudentIdsResponse();
        batchResponse.setItems(List.of(classResponse));
        when(learningInternalClient.resolveStudentsByClassBatch(any())).thenReturn(batchResponse);

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(studentId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(sseNotificationBroadcaster).broadcast(eq(studentId), any());
    }

    @Test
    void resolveRecipients_shouldResolveByProgram() {
        UUID specId = UUID.randomUUID();
        ObjectNode payload = mapper.createObjectNode();
        ArrayNode arr = payload.putArray("specializationIds");
        arr.add(specId.toString());

        NotificationEntity notification = createNotification(TargetMode.PROGRAM, payload.toString(), "[\"IN_APP\"]");
        UUID userId = UUID.randomUUID();

        stubChannels(notification);
        when(jsonCodec.toJsonNode(payload.toString())).thenReturn(payload);

        InternalUserSummaryResponse userSummary = new InternalUserSummaryResponse();
        userSummary.setId(userId);
        when(userManagementInternalClient.resolveUsers(any())).thenReturn(List.of(userSummary));

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(sseNotificationBroadcaster).broadcast(eq(userId), any());
    }

    @Test
    void resolveRecipients_shouldResolveByUserList() {
        UUID userId = UUID.randomUUID();
        ObjectNode payload = mapper.createObjectNode();
        ArrayNode arr = payload.putArray("userIds");
        arr.add(userId.toString());

        NotificationEntity notification = createNotification(TargetMode.USER_LIST, payload.toString(), "[\"IN_APP\"]");

        stubChannels(notification);
        when(jsonCodec.toJsonNode(payload.toString())).thenReturn(payload);

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(sseNotificationBroadcaster).broadcast(eq(userId), any());
    }

    @Test
    void resolveRecipients_shouldResolveByCourseWithSingleClassId() {
        UUID classId = UUID.randomUUID();
        ObjectNode payload = mapper.createObjectNode();
        payload.put("classId", classId.toString());

        NotificationEntity notification = createNotification(TargetMode.COURSE, payload.toString(), "[\"IN_APP\"]");
        UUID studentId = UUID.randomUUID();

        stubChannels(notification);
        when(jsonCodec.toJsonNode(payload.toString())).thenReturn(payload);
        when(learningInternalClient.resolveStudentsByClass(classId)).thenReturn(List.of(studentId));

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(studentId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(sseNotificationBroadcaster).broadcast(eq(studentId), any());
    }

    @Test
    void resolveRecipients_shouldResolveByCourseWithCourseId() {
        UUID courseId = UUID.randomUUID();
        ObjectNode payload = mapper.createObjectNode();
        payload.put("courseId", courseId.toString());

        NotificationEntity notification = createNotification(TargetMode.COURSE, payload.toString(), "[\"IN_APP\"]");
        UUID studentId = UUID.randomUUID();

        stubChannels(notification);
        when(jsonCodec.toJsonNode(payload.toString())).thenReturn(payload);
        when(learningInternalClient.resolveStudentsByCourse(courseId)).thenReturn(List.of(studentId));

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(studentId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(sseNotificationBroadcaster).broadcast(eq(studentId), any());
    }

    @Test
    void resolveRecipients_shouldResolveByUserListWithSingleUser() {
        UUID userId = UUID.randomUUID();
        ObjectNode payload = mapper.createObjectNode();
        payload.put("userId", userId.toString());

        NotificationEntity notification = createNotification(TargetMode.USER_LIST, payload.toString(), "[\"IN_APP\"]");

        stubChannels(notification);
        when(jsonCodec.toJsonNode(payload.toString())).thenReturn(payload);

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(sseNotificationBroadcaster).broadcast(eq(userId), any());
    }

    @Test
    void resolveRecipients_shouldResolveByGroup() {
        ObjectNode payload = mapper.createObjectNode();
        ArrayNode arr = payload.putArray("groups");
        arr.add("STUDENT");

        NotificationEntity notification = createNotification(TargetMode.GROUP, payload.toString(), "[\"IN_APP\"]");
        UUID userId = UUID.randomUUID();

        stubChannels(notification);
        when(jsonCodec.toJsonNode(payload.toString())).thenReturn(payload);

        InternalUserSummaryResponse userSummary = new InternalUserSummaryResponse();
        userSummary.setId(userId);
        when(userManagementInternalClient.resolveUsers(any())).thenReturn(List.of(userSummary));

        Map<NotificationType, NotificationPreferenceEntity> prefMap = new HashMap<>();
        prefMap.put(NotificationType.SYSTEM_ANNOUNCEMENT, null);
        when(notificationPreferenceService.getOrCreatePreferenceMap(userId)).thenReturn(prefMap);
        when(userNotificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationDispatchService.dispatch(notification);

        verify(userManagementInternalClient).resolveUsers(any());
    }

    private void stubChannels(NotificationEntity notification) {
        when(jsonCodec.toStringSet(notification.getChannels())).thenReturn(parseChannels(notification.getChannels()));
    }

    private Set<String> parseChannels(String channelsJson) {
        Set<String> channels = new LinkedHashSet<>();
        if (channelsJson.contains("IN_APP")) channels.add("IN_APP");
        if (channelsJson.contains("EMAIL")) channels.add("EMAIL");
        if (channelsJson.contains("PUSH")) channels.add("PUSH");
        return channels;
    }

    private NotificationEntity createNotification(TargetMode targetMode, String targetPayload, String channels) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(UUID.randomUUID());
        entity.setTitle("Test");
        entity.setContent("Content");
        entity.setType(NotificationType.SYSTEM_ANNOUNCEMENT);
        entity.setPriority(NotificationPriority.MEDIUM);
        entity.setTargetMode(targetMode);
        entity.setTargetPayload(targetPayload);
        entity.setChannels(channels);
        entity.setStatus(NotificationStatus.SENT);
        entity.setCreatedAt(java.time.Instant.now());
        return entity;
    }

    private NotificationPreferenceEntity createPreference(UUID userId, NotificationType type) {
        NotificationPreferenceEntity pref = new NotificationPreferenceEntity();
        pref.setId(UUID.randomUUID());
        pref.setUserId(userId);
        pref.setNotificationType(type);
        pref.setInAppEnabled(true);
        pref.setEmailEnabled(true);
        pref.setPushEnabled(false);
        pref.setFrequency(NotificationFrequency.IMMEDIATE);
        return pref;
    }
}
