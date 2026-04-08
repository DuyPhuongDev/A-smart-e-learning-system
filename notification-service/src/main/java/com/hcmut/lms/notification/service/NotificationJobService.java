package com.hcmut.lms.notification.service;

public interface NotificationJobService {

    void processScheduledNotifications();

    void processEmailRetries();

    void processDailyDigest();

    void processRetention();

    void processOutbox();
}
