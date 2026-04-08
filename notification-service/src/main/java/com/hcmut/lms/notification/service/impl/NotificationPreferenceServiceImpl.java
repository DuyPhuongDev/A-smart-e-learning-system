package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.PreferenceItemRequest;
import com.hcmut.lms.notification.dto.request.PreferencesUpsertRequest;
import com.hcmut.lms.notification.dto.response.NotificationPreferenceResponse;
import com.hcmut.lms.notification.entity.NotificationPreferenceEntity;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.repository.NotificationPreferenceRepository;
import com.hcmut.lms.notification.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceServiceImpl implements NotificationPreferenceService {

    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationPreferenceResponse> getPreferences(CurrentUserInfo currentUser) {
        return getOrCreateDefaultPreferences(currentUser.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<NotificationPreferenceResponse> upsertPreferences(CurrentUserInfo currentUser, PreferencesUpsertRequest request) {
        UUID userId = currentUser.getId();
        for (PreferenceItemRequest item : request.getPreferences()) {
            NotificationPreferenceEntity entity = notificationPreferenceRepository
                    .findByUserIdAndNotificationType(userId, item.getNotificationType())
                    .orElseGet(() -> {
                        NotificationPreferenceEntity created = new NotificationPreferenceEntity();
                        created.setId(UUID.randomUUID());
                        created.setUserId(userId);
                        created.setNotificationType(item.getNotificationType());
                        return created;
                    });

            entity.setInAppEnabled(Boolean.TRUE.equals(item.getInAppEnabled()));
            entity.setEmailEnabled(Boolean.TRUE.equals(item.getEmailEnabled()));
            entity.setPushEnabled(Boolean.TRUE.equals(item.getPushEnabled()));
            entity.setFrequency(item.getFrequency());
            notificationPreferenceRepository.save(entity);
        }
        return getPreferences(currentUser);
    }

    @Override
    @Transactional
    public List<NotificationPreferenceResponse> resetPreferences(CurrentUserInfo currentUser) {
        notificationPreferenceRepository.deleteByUserId(currentUser.getId());
        return getPreferences(currentUser);
    }

    @Override
    @Transactional
    public Map<NotificationType, NotificationPreferenceEntity> getOrCreatePreferenceMap(UUID userId) {
        return getOrCreateDefaultPreferences(userId).stream()
                .collect(Collectors.toMap(NotificationPreferenceEntity::getNotificationType, Function.identity()));
    }

    private List<NotificationPreferenceEntity> getOrCreateDefaultPreferences(UUID userId) {
        List<NotificationPreferenceEntity> existing = notificationPreferenceRepository.findByUserId(userId);
        if (!existing.isEmpty()) {
            return existing;
        }

        List<NotificationPreferenceEntity> defaults = Arrays.stream(NotificationType.values())
                .map(type -> {
                    NotificationPreferenceEntity pref = new NotificationPreferenceEntity();
                    pref.setId(UUID.randomUUID());
                    pref.setUserId(userId);
                    pref.setNotificationType(type);
                    pref.setInAppEnabled(true);
                    pref.setEmailEnabled(defaultEmailEnabled(type));
                    pref.setPushEnabled(false);
                    pref.setFrequency(NotificationFrequency.IMMEDIATE);
                    return pref;
                })
                .toList();

        return notificationPreferenceRepository.saveAll(defaults);
    }

    private boolean defaultEmailEnabled(NotificationType type) {
        return EnumSet.of(
                NotificationType.DEADLINE_REMINDER,
                NotificationType.SUBMISSION_GRADED,
                NotificationType.SYSTEM_MAINTENANCE,
                NotificationType.SYSTEM_ANNOUNCEMENT
        ).contains(type);
    }

    private NotificationPreferenceResponse toResponse(NotificationPreferenceEntity entity) {
        return NotificationPreferenceResponse.builder()
                .notificationType(entity.getNotificationType())
                .inAppEnabled(entity.isInAppEnabled())
                .emailEnabled(entity.isEmailEnabled())
                .pushEnabled(entity.isPushEnabled())
                .frequency(entity.getFrequency())
                .build();
    }
}
