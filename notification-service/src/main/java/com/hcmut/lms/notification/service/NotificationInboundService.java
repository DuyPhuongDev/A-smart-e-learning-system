package com.hcmut.lms.notification.service;

public interface NotificationInboundService {

    void handleInboundEvent(String payload);

    void recordInboundFailure(String topic, String key, String payload, String errorMessage);
}
