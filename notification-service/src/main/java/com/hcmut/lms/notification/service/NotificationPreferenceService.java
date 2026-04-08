package com.hcmut.lms.notification.service;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.PreferencesUpsertRequest;
import com.hcmut.lms.notification.dto.response.NotificationPreferenceResponse;
import com.hcmut.lms.notification.entity.NotificationPreferenceEntity;
import com.hcmut.lms.notification.enums.NotificationType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationPreferenceService {

    List<NotificationPreferenceResponse> getPreferences(CurrentUserInfo currentUser);

    List<NotificationPreferenceResponse> upsertPreferences(CurrentUserInfo currentUser, PreferencesUpsertRequest request);

    List<NotificationPreferenceResponse> resetPreferences(CurrentUserInfo currentUser);

    /**
     * Returns a preference map per notification type for a user,
     * creating defaults if the user has no preferences yet.
     * Used internally by the dispatch pipeline.
     */
    Map<NotificationType, NotificationPreferenceEntity> getOrCreatePreferenceMap(UUID userId);
}
