package com.hcmut.lms.notification.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class TimeHelper {

    public static String formatReminderTime(int totalMinutes) {
        if (totalMinutes <= 0) return "0 phút";

        int days = totalMinutes / 1440;
        int hours = (totalMinutes % 1440) / 60;
        int minutes = totalMinutes % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) result.append(days).append(" ngày ");
        if (hours > 0) result.append(hours).append(" giờ ");
        if (minutes > 0) result.append(minutes).append(" phút");

        return result.toString().trim();
    }

}