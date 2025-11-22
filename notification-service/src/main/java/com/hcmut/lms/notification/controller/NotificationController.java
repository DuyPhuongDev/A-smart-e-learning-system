package com.hcmut.lms.notification.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping("/send")
    public String sendNotification() {
        // TODO: Implement send notification logic
        return "Send notification - To be implemented";
    }

    @PostMapping("/announcement")
    public String createAnnouncement() {
        // TODO: Implement create announcement logic
        return "Create announcement - To be implemented";
    }

    @GetMapping("/{userId}")
    public String getUserNotifications(@PathVariable String userId) {
        // TODO: Implement get user notifications logic
        return "Get user notifications - To be implemented";
    }

    @PutMapping("/{id}/read")
    public String markAsRead(@PathVariable String id) {
        // TODO: Implement mark as read logic
        return "Mark as read - To be implemented";
    }

    @PutMapping("/preferences/{userId}")
    public String updateNotificationPreferences(@PathVariable String userId) {
        // TODO: Implement update notification preferences logic
        return "Update notification preferences - To be implemented";
    }

    @GetMapping("/preferences/{userId}")
    public String getNotificationPreferences(@PathVariable String userId) {
        // TODO: Implement get notification preferences logic
        return "Get notification preferences - To be implemented";
    }
}
