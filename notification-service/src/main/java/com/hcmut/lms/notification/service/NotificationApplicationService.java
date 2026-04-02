package com.hcmut.lms.notification.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.AdminCreateNotificationRequest;
import com.hcmut.lms.notification.dto.request.PreferencesUpsertRequest;
import com.hcmut.lms.notification.dto.request.RuleUpdateRequest;
import com.hcmut.lms.notification.dto.request.TemplateUpdateRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationListItemResponse;
import com.hcmut.lms.notification.dto.response.InboxNotificationResponse;
import com.hcmut.lms.notification.dto.response.NotificationPreferenceResponse;
import com.hcmut.lms.notification.dto.response.NotificationRuleResponse;
import com.hcmut.lms.notification.dto.response.NotificationTemplateResponse;

import java.util.List;
import java.util.UUID;

public interface NotificationApplicationService {

    AdminNotificationCreateResponse createAdminNotification(
            CurrentUserInfo currentUser,
            AdminCreateNotificationRequest request,
            String idempotencyKey
    );

    PageResponse<AdminNotificationListItemResponse> getAdminNotifications(
            CurrentUserInfo currentUser,
            String status,
            String type,
            String channel,
            String from,
            String to,
            String keyword,
            int page,
            int size
    );

    AdminNotificationDetailResponse getAdminNotificationDetail(CurrentUserInfo currentUser, UUID notificationId);

    void sendNow(CurrentUserInfo currentUser, UUID notificationId);

    void cancel(CurrentUserInfo currentUser, UUID notificationId);

    PageResponse<InboxNotificationResponse> getMyInbox(CurrentUserInfo currentUser, String status, int page, int size);

    long getUnreadCount(CurrentUserInfo currentUser);

    void markRead(CurrentUserInfo currentUser, UUID userNotificationId);

    void markReadAll(CurrentUserInfo currentUser);

    List<NotificationPreferenceResponse> getPreferences(CurrentUserInfo currentUser);

    List<NotificationPreferenceResponse> upsertPreferences(CurrentUserInfo currentUser, PreferencesUpsertRequest request);

    List<NotificationPreferenceResponse> resetPreferences(CurrentUserInfo currentUser);

    List<NotificationRuleResponse> getRules(CurrentUserInfo currentUser);

    NotificationRuleResponse updateRule(CurrentUserInfo currentUser, String eventType, RuleUpdateRequest request);

    List<NotificationTemplateResponse> getTemplates(CurrentUserInfo currentUser);

    NotificationTemplateResponse updateTemplate(CurrentUserInfo currentUser, String code, TemplateUpdateRequest request);

    void processScheduledNotifications();

    void processEmailRetries();

    void processDailyDigest();

    void processRetention();

    void processOutbox();

    void refreshUnreadMetrics();

    void handleInboundEvent(String payload);

    void recordInboundFailure(String topic, String key, String payload, String errorMessage);
}
