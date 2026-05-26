package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.PreferenceItemRequest;
import com.hcmut.lms.notification.dto.request.PreferencesUpsertRequest;
import com.hcmut.lms.notification.dto.response.NotificationPreferenceResponse;
import com.hcmut.lms.notification.entity.NotificationPreferenceEntity;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.repository.NotificationPreferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationPreferenceServiceImplTest {

    @Mock
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @InjectMocks
    private NotificationPreferenceServiceImpl notificationPreferenceService;

    @Test
    void getPreferences_shouldReturnExistingPreferences() {
        CurrentUserInfo user = createUser();
        NotificationPreferenceEntity pref = createPreference(user.getId(), NotificationType.SYSTEM_ANNOUNCEMENT);
        when(notificationPreferenceRepository.findByUserId(user.getId())).thenReturn(List.of(pref));

        List<NotificationPreferenceResponse> result = notificationPreferenceService.getPreferences(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNotificationType()).isEqualTo(NotificationType.SYSTEM_ANNOUNCEMENT);
    }

    @Test
    void getPreferences_shouldCreateDefaults_whenNoneExist() {
        CurrentUserInfo user = createUser();
        when(notificationPreferenceRepository.findByUserId(user.getId())).thenReturn(List.of());
        when(notificationPreferenceRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<NotificationPreferenceResponse> result = notificationPreferenceService.getPreferences(user);

        assertThat(result).hasSize(NotificationType.values().length);
    }

    @Test
    void upsertPreferences_shouldUpdateExisting() {
        CurrentUserInfo user = createUser();
        PreferenceItemRequest item = new PreferenceItemRequest();
        item.setNotificationType(NotificationType.SYSTEM_ANNOUNCEMENT);
        item.setInAppEnabled(false);
        item.setEmailEnabled(true);
        item.setPushEnabled(false);
        item.setFrequency(NotificationFrequency.DIGEST_DAILY);
        PreferencesUpsertRequest request = new PreferencesUpsertRequest();
        request.setPreferences(List.of(item));

        NotificationPreferenceEntity existing = createPreference(user.getId(), NotificationType.SYSTEM_ANNOUNCEMENT);
        when(notificationPreferenceRepository.findByUserIdAndNotificationType(user.getId(), NotificationType.SYSTEM_ANNOUNCEMENT))
                .thenReturn(Optional.of(existing));
        when(notificationPreferenceRepository.save(any())).thenReturn(existing);
        when(notificationPreferenceRepository.findByUserId(user.getId())).thenReturn(List.of(existing));

        List<NotificationPreferenceResponse> result = notificationPreferenceService.upsertPreferences(user, request);

        assertThat(existing.isInAppEnabled()).isFalse();
        assertThat(existing.getFrequency()).isEqualTo(NotificationFrequency.DIGEST_DAILY);
        assertThat(result).hasSize(1);
    }

    @Test
    void upsertPreferences_shouldCreateNew_whenNotFound() {
        CurrentUserInfo user = createUser();
        PreferenceItemRequest item = new PreferenceItemRequest();
        item.setNotificationType(NotificationType.DEADLINE_REMINDER);
        item.setInAppEnabled(true);
        item.setEmailEnabled(true);
        item.setPushEnabled(false);
        item.setFrequency(NotificationFrequency.IMMEDIATE);
        PreferencesUpsertRequest request = new PreferencesUpsertRequest();
        request.setPreferences(List.of(item));

        when(notificationPreferenceRepository.findByUserIdAndNotificationType(user.getId(), NotificationType.DEADLINE_REMINDER))
                .thenReturn(Optional.empty());
        when(notificationPreferenceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(notificationPreferenceRepository.findByUserId(user.getId())).thenReturn(List.of());

        notificationPreferenceService.upsertPreferences(user, request);

        ArgumentCaptor<NotificationPreferenceEntity> captor = ArgumentCaptor.forClass(NotificationPreferenceEntity.class);
        verify(notificationPreferenceRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(user.getId());
        assertThat(captor.getValue().getNotificationType()).isEqualTo(NotificationType.DEADLINE_REMINDER);
    }

    @Test
    void resetPreferences_shouldDeleteAndRecreateDefaults() {
        CurrentUserInfo user = createUser();
        when(notificationPreferenceRepository.findByUserId(user.getId())).thenReturn(List.of());
        when(notificationPreferenceRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        notificationPreferenceService.resetPreferences(user);

        verify(notificationPreferenceRepository).deleteByUserId(user.getId());
    }

    @Test
    void getOrCreatePreferenceMap_shouldReturnExistingMap() {
        UUID userId = UUID.randomUUID();
        NotificationPreferenceEntity pref = createPreference(userId, NotificationType.SYSTEM_ANNOUNCEMENT);
        when(notificationPreferenceRepository.findByUserId(userId)).thenReturn(List.of(pref));

        Map<NotificationType, NotificationPreferenceEntity> result = notificationPreferenceService.getOrCreatePreferenceMap(userId);

        assertThat(result).hasSize(1);
        assertThat(result).containsKey(NotificationType.SYSTEM_ANNOUNCEMENT);
    }

    @Test
    void getOrCreatePreferenceMap_shouldCreateDefaults_whenNone() {
        UUID userId = UUID.randomUUID();
        when(notificationPreferenceRepository.findByUserId(userId)).thenReturn(List.of());
        when(notificationPreferenceRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<NotificationType, NotificationPreferenceEntity> result = notificationPreferenceService.getOrCreatePreferenceMap(userId);

        assertThat(result).hasSize(NotificationType.values().length);
    }

    private CurrentUserInfo createUser() {
        return CurrentUserInfo.builder()
                .id(UUID.randomUUID())
                .email("test@hcmut.edu.vn")
                .role("STUDENT")
                .build();
    }

    private NotificationPreferenceEntity createPreference(UUID userId, NotificationType type) {
        NotificationPreferenceEntity pref = new NotificationPreferenceEntity();
        pref.setId(UUID.randomUUID());
        pref.setUserId(userId);
        pref.setNotificationType(type);
        pref.setInAppEnabled(true);
        pref.setEmailEnabled(false);
        pref.setPushEnabled(false);
        pref.setFrequency(NotificationFrequency.IMMEDIATE);
        return pref;
    }
}
