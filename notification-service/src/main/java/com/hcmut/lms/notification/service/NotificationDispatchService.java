package com.hcmut.lms.notification.service;

import com.hcmut.lms.notification.entity.NotificationEntity;

public interface NotificationDispatchService {

    /**
     * Resolves recipients for the notification, creates per-user delivery records,
     * and routes each record to the appropriate channel (in-app, email, push).
     */
    void dispatch(NotificationEntity notification);
}
