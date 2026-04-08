package com.hcmut.lms.notification.enums;

public enum NotificationOutboxEventType {
    ADMIN_CREATED("notification.admin.created"),
    ADMIN_SEND_NOW("notification.admin.send_now"),
    ADMIN_CANCELLED("notification.admin.cancelled"),
    SCHEDULER_SENT("notification.scheduler.sent"),
    EVENT_INGESTED("notification.event.ingested");

    private final String value;

    NotificationOutboxEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

