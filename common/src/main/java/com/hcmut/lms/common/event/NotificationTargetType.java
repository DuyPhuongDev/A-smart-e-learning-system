package com.hcmut.lms.common.event;

/**
 * Who receives the notification (maps to fan-out strategy in notification-service).
 */
public enum NotificationTargetType {
    /** Single class — targetId = class UUID */
    CLASS,
    /** Subject — targetId = course UUID (all students in subject) */
    COURSE,
    /** All active users in the system */
    GLOBAL,
    /** Single user — targetId = user UUID */
    USER
}
