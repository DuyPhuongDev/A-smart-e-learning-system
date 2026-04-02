package com.hcmut.lms.notification.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public final class TimePolicy {

    private TimePolicy() {
    }

    public static boolean isInQuietHours(Instant now, ZoneId zoneId, LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return false;
        }

        LocalTime localNow = now.atZone(zoneId).toLocalTime();

        if (start.equals(end)) {
            return false;
        }

        if (start.isBefore(end)) {
            return !localNow.isBefore(start) && localNow.isBefore(end);
        }

        // cross midnight window (22:00 -> 07:00)
        return !localNow.isBefore(start) || localNow.isBefore(end);
    }
}
