package com.hcmut.lms.notification.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping("/send")
    public ResponseDto<String> sendNotification() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Send notification - To be implemented")
                .build();
    }

    @PostMapping("/announcement")
    public ResponseDto<String> createAnnouncement() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Create announcement - To be implemented")
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseDto<String> getUserNotifications(@PathVariable String userId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get user notifications - To be implemented")
                .build();
    }

    @PutMapping("/{id}/read")
    public ResponseDto<String> markAsRead(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Mark as read - To be implemented")
                .build();
    }

    @PutMapping("/preferences/{userId}")
    public ResponseDto<String> updateNotificationPreferences(@PathVariable String userId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Update notification preferences - To be implemented")
                .build();
    }

    @GetMapping("/preferences/{userId}")
    public ResponseDto<String> getNotificationPreferences(@PathVariable String userId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get notification preferences - To be implemented")
                .build();
    }
}

