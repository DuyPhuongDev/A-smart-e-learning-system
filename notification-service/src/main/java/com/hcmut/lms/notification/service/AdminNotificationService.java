package com.hcmut.lms.notification.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.AdminCreateNotificationRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationListItemResponse;

import java.util.UUID;

public interface AdminNotificationService {

    AdminNotificationCreateResponse create(
            CurrentUserInfo currentUser,
            AdminCreateNotificationRequest request,
            String idempotencyKey
    );

    PageResponse<AdminNotificationListItemResponse> list(
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

    AdminNotificationDetailResponse detail(CurrentUserInfo currentUser, UUID notificationId);

    void sendNow(CurrentUserInfo currentUser, UUID notificationId);

    void cancel(CurrentUserInfo currentUser, UUID notificationId);
}
