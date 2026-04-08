package com.hcmut.lms.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationAsyncEmailService {

    private final NotificationEmailDeliveryWorker notificationEmailDeliveryWorker;

    @Async("notificationEmailExecutor")
    public void deliverWhenReady(UUID userNotificationId) {
        notificationEmailDeliveryWorker.executeDelivery(userNotificationId);
    }

}
