package com.hcmut.lms.notification.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.response.InboxNotificationResponse;

import java.util.UUID;

public interface UserInboxService {

    PageResponse<InboxNotificationResponse> getMyInbox(CurrentUserInfo currentUser, String status, int page, int size);

    long getUnreadCount(CurrentUserInfo currentUser);

    void markRead(CurrentUserInfo currentUser, UUID userNotificationId);

    void markReadAll(CurrentUserInfo currentUser);
}
