package com.hcmut.lms.common.event;

/**
 * Single inbound topic for standardized notification events (producers + consumer).
 */
public final class NotificationKafkaTopics {

    public static final String INBOUND_NOTIFICATION = "lms.events.notification";

    private NotificationKafkaTopics() {
    }
}
