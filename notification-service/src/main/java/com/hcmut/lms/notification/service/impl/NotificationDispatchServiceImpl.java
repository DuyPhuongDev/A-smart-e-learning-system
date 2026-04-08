package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.hcmut.lms.notification.client.LearningInternalClient;
import com.hcmut.lms.notification.client.UserManagementInternalClient;
import com.hcmut.lms.notification.client.dto.*;
import com.hcmut.lms.notification.dto.response.RealtimeNotificationPayload;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationPreferenceEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.DeferReason;
import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.ReadStatus;
import com.hcmut.lms.notification.enums.TargetMode;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.service.NotificationAsyncEmailService;
import com.hcmut.lms.notification.service.NotificationDispatchService;
import com.hcmut.lms.notification.service.NotificationOutboxService;
import com.hcmut.lms.notification.service.NotificationPreferenceService;
import com.hcmut.lms.notification.service.SseNotificationBroadcaster;
import com.hcmut.lms.notification.util.JsonCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDispatchServiceImpl implements NotificationDispatchService {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Saigon");

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationPreferenceService notificationPreferenceService;
    private final UserManagementInternalClient userManagementInternalClient;
    private final LearningInternalClient learningInternalClient;
    private final SseNotificationBroadcaster sseNotificationBroadcaster;
    private final NotificationAsyncEmailService notificationAsyncEmailService;
    private final NotificationOutboxService notificationOutboxService;
    private final JsonCodec jsonCodec;

    @Override
    @Transactional
    public void dispatch(NotificationEntity notification) {
        // get list userIds
        Set<UUID> recipients = resolveRecipients(
                notification.getTargetMode(),
                jsonCodec.toJsonNode(notification.getTargetPayload())
        );

        if (recipients.isEmpty()) {
            log.info("Notification {} has no resolved recipients", notification.getId());
            return;
        }

        Set<NotificationChannel> channels = parseChannelSet(notification.getChannels());

        for (UUID recipientId : recipients) {
            Map<NotificationType, NotificationPreferenceEntity> preferenceMap =
                    notificationPreferenceService.getOrCreatePreferenceMap(recipientId);
            NotificationPreferenceEntity preference = preferenceMap.get(notification.getType());

            for (NotificationChannel channel : channels) {
                if (!isChannelEnabled(preference, notification.getType(), channel)) {
                    continue;
                }

                NotificationFrequency frequency = preference == null
                        ? NotificationFrequency.IMMEDIATE
                        : preference.getFrequency();

                UserNotificationEntity userNotification =
                        createBaseUserNotification(notification, recipientId, channel, frequency);
                routeDelivery(userNotification, preference);
            }
        }
    }

    private void routeDelivery(UserNotificationEntity userNotification, NotificationPreferenceEntity preference) {
        if (userNotification.getChannel() == NotificationChannel.IN_APP) {
            userNotification.setDeliveryStatus(DeliveryStatus.DELIVERED);
            userNotification.setDeliveredAt(Instant.now());
            userNotificationRepository.save(userNotification);
            pushRealtime(userNotification);
            return;
        }

        if (userNotification.getChannel() != NotificationChannel.EMAIL) {
            userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
            userNotificationRepository.save(userNotification);
            return;
        }

        if (userNotification.getFrequency() == NotificationFrequency.DIGEST_DAILY) {
            userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
            userNotification.setDeferReason(DeferReason.DIGEST);
            userNotification.setDigestBucketDate(LocalDate.now(DEFAULT_ZONE));
            userNotification.setNextAttemptAt(null);
            userNotificationRepository.save(userNotification);
            return;
        }

        userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
        userNotificationRepository.save(userNotification);
        scheduleEmailDelivery(userNotification);
    }

    private UserNotificationEntity createBaseUserNotification(
            NotificationEntity notification,
            UUID userId,
            NotificationChannel channel,
            NotificationFrequency frequency
    ) {
        UserNotificationEntity userNotification = new UserNotificationEntity();
        userNotification.setId(UUID.randomUUID());
        userNotification.setNotification(notification);
        userNotification.setUserId(userId);
        userNotification.setChannel(channel);
        userNotification.setDeliveryStatus(DeliveryStatus.PENDING);
        userNotification.setReadStatus(ReadStatus.UNREAD);
        userNotification.setReadAt(null);
        userNotification.setDeliveredAt(null);
        userNotification.setDeliveryAttempts(0);
        userNotification.setLastError(null);
        userNotification.setFrequency(frequency);
        userNotification.setNextAttemptAt(Instant.now());
        return userNotificationRepository.save(userNotification);
    }

    private void scheduleEmailDelivery(UserNotificationEntity userNotification) {
        UUID id = userNotification.getId();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    notificationAsyncEmailService.deliverWhenReady(id);
                }
            });
        } else {
            notificationAsyncEmailService.deliverWhenReady(id);
        }
    }

    private void pushRealtime(UserNotificationEntity userNotification) {
        NotificationEntity notification = userNotification.getNotification();
        RealtimeNotificationPayload payload = RealtimeNotificationPayload.builder()
                .userNotificationId(userNotification.getId())
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .priority(notification.getPriority())
                .createdAt(notification.getCreatedAt())
                .build();

        sseNotificationBroadcaster.broadcast(userNotification.getUserId(), payload);
    }

    private Set<UUID> resolveRecipients(TargetMode targetMode, JsonNode targetPayload) {
        return switch (targetMode) {
            case ALL -> resolveAllUsers();
            case GROUP -> resolveByGroups(targetPayload);
            case COURSE -> resolveByCourse(targetPayload);
            case PROGRAM -> resolveByProgram(targetPayload);
            case USER_LIST -> resolveByUserList(targetPayload);
        };
    }

    private Set<UUID> resolveAllUsers() {
        InternalResolveUsersRequest request = InternalResolveUsersRequest.builder()
                .roleNames(List.of("ADMIN", "TEACHER", "STUDENT"))
                .build();
        return userManagementInternalClient.resolveUsers(request).stream()
                .map(InternalUserSummaryResponse::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<UUID> resolveByGroups(JsonNode payload) {
        List<String> groups = jsonArrayText(payload, "groups");
        if (groups.isEmpty()) {
            return Collections.emptySet();
        }
        List<String> roleNames = groups.stream()
                .map(this::groupToRole)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (roleNames.isEmpty()) {
            return Collections.emptySet();
        }
        InternalResolveUsersRequest request = InternalResolveUsersRequest.builder()
                .roleNames(roleNames)
                .build();
        return userManagementInternalClient.resolveUsers(request).stream()
                .map(it -> it.getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<UUID> resolveByCourse(JsonNode payload) {
        Set<UUID> recipients = new LinkedHashSet<>();

        UUID classId = jsonUuid(payload, "classId");
        if (classId != null) {
            recipients.addAll(safeList(learningInternalClient.resolveStudentsByClass(classId)));
        }

        List<UUID> classIds = jsonUuidList(payload, "classIds");
        if (!classIds.isEmpty()) {
            BatchClassStudentIdsResponse response = learningInternalClient.resolveStudentsByClassBatch(
                    BatchClassStudentIdsRequest.builder().classIds(classIds).build()
            );
            if (response != null && response.getItems() != null) {
                for (ClassStudentIdsResponse item : response.getItems()) {
                    recipients.addAll(safeList(item.getStudentIds()));
                }
            }
        }

        UUID courseId = jsonUuid(payload, "courseId");
        if (courseId != null) {
            recipients.addAll(safeList(learningInternalClient.resolveStudentsByCourse(courseId)));
        }

        return recipients;
    }

    private Set<UUID> resolveByProgram(JsonNode payload) {
        List<UUID> specializationIds = jsonUuidList(payload, "specializationIds");
        if (specializationIds.isEmpty()) {
            return Collections.emptySet();
        }
        InternalResolveUsersRequest request = InternalResolveUsersRequest.builder()
                .specializationIds(specializationIds)
                .build();
        return userManagementInternalClient.resolveUsers(request).stream()
                .map(it -> it.getId())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<UUID> resolveByUserList(JsonNode payload) {
        List<UUID> userIds = jsonUuidList(payload, "userIds");
        if (!userIds.isEmpty()) {
            return new LinkedHashSet<>(userIds);
        }
        UUID userId = jsonUuid(payload, "userId");
        UUID studentId = jsonUuid(payload, "studentId");
        Set<UUID> resolved = new LinkedHashSet<>();
        if (userId != null) resolved.add(userId);
        if (studentId != null) resolved.add(studentId);
        return resolved;
    }

    private boolean isChannelEnabled(NotificationPreferenceEntity preference, NotificationType type, NotificationChannel channel) {
        if (preference == null) {
            return switch (channel) {
                case IN_APP -> true;
                case EMAIL -> defaultEmailEnabled(type);
                case PUSH -> false;
            };
        }
        return switch (channel) {
            case IN_APP -> preference.isInAppEnabled();
            case EMAIL -> preference.isEmailEnabled();
            case PUSH -> preference.isPushEnabled();
        };
    }

    private boolean defaultEmailEnabled(NotificationType type) {
        return EnumSet.of(
                NotificationType.DEADLINE_REMINDER,
                NotificationType.SUBMISSION_GRADED,
                NotificationType.SYSTEM_MAINTENANCE,
                NotificationType.SYSTEM_ANNOUNCEMENT
        ).contains(type);
    }

    private Set<NotificationChannel> parseChannelSet(String channelsJson) {
        Set<NotificationChannel> channels = new LinkedHashSet<>();
        for (String value : jsonCodec.toStringSet(channelsJson)) {
            channels.add(NotificationChannel.valueOf(value));
        }
        return channels;
    }

    private String groupToRole(String group) {
        return switch (group.toUpperCase()) {
            case "ALL_STUDENTS", "STUDENT" -> "STUDENT";
            case "ALL_TEACHERS", "ALL_INSTRUCTORS", "TEACHER", "INSTRUCTOR" -> "TEACHER";
            case "ALL_ADMINS", "ADMIN" -> "ADMIN";
            default -> null;
        };
    }

    private List<UUID> jsonUuidList(JsonNode node, String field) {
        if (node == null || node.get(field) == null || !node.get(field).isArray()) {
            return List.of();
        }
        List<UUID> result = new ArrayList<>();
        for (JsonNode item : node.get(field)) {
            try {
                result.add(UUID.fromString(item.asText()));
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    private UUID jsonUuid(JsonNode node, String field) {
        if (node == null || node.get(field) == null || node.get(field).isNull()) {
            return null;
        }
        try {
            return UUID.fromString(node.get(field).asText());
        } catch (Exception ex) {
            return null;
        }
    }

    private List<String> jsonArrayText(JsonNode node, String field) {
        if (node == null || node.get(field) == null || !node.get(field).isArray()) {
            return List.of();
        }
        List<String> result = new ArrayList<>();
        for (JsonNode item : node.get(field)) {
            if (!item.isNull() && StringUtils.hasText(item.asText())) {
                result.add(item.asText());
            }
        }
        return result;
    }

    private <T> List<T> safeList(Collection<T> values) {
        return values == null ? List.of() : new ArrayList<>(values);
    }
}
