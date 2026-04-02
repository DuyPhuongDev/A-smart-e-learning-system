package com.hcmut.lms.notification.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.AdminCreateNotificationRequest;
import com.hcmut.lms.notification.dto.request.PreferencesUpsertRequest;
import com.hcmut.lms.notification.dto.request.RuleUpdateRequest;
import com.hcmut.lms.notification.dto.request.TemplateUpdateRequest;
import com.hcmut.lms.notification.dto.response.AdminNotificationCreateResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationDetailResponse;
import com.hcmut.lms.notification.dto.response.AdminNotificationListItemResponse;
import com.hcmut.lms.notification.dto.response.InboxNotificationResponse;
import com.hcmut.lms.notification.dto.response.NotificationPreferenceResponse;
import com.hcmut.lms.notification.dto.response.NotificationRuleResponse;
import com.hcmut.lms.notification.dto.response.NotificationTemplateResponse;
import com.hcmut.lms.notification.dto.response.UnreadCountResponse;
import com.hcmut.lms.notification.service.NotificationApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationApplicationService notificationApplicationService;

    @PostMapping("/admin")
    public ResponseEntity<AdminNotificationCreateResponse> createAdminNotification(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @RequestBody @Valid AdminCreateNotificationRequest request
    ) {
        return ResponseEntity.ok(notificationApplicationService.createAdminNotification(currentUser, request, idempotencyKey));
    }

    @GetMapping("/admin")
    public ResponseEntity<PageResponse<AdminNotificationListItemResponse>> getAdminNotifications(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(notificationApplicationService.getAdminNotifications(
                currentUser,
                status,
                type,
                channel,
                from,
                to,
                keyword,
                page,
                size
        ));
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<AdminNotificationDetailResponse> getAdminNotificationDetail(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(notificationApplicationService.getAdminNotificationDetail(currentUser, id));
    }

    @PostMapping("/admin/{id}/send-now")
    public ResponseEntity<Void> sendNow(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable("id") UUID id
    ) {
        notificationApplicationService.sendNow(currentUser, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable("id") UUID id
    ) {
        notificationApplicationService.cancel(currentUser, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<PageResponse<InboxNotificationResponse>> getMyInbox(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(notificationApplicationService.getMyInbox(currentUser, status, page, size));
    }

    @GetMapping("/me/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(@CurrentUser CurrentUserInfo currentUser) {
        return ResponseEntity.ok(new UnreadCountResponse(notificationApplicationService.getUnreadCount(currentUser)));
    }

    @PostMapping("/me/{userNotificationId}/read")
    public ResponseEntity<Void> markRead(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable UUID userNotificationId
    ) {
        notificationApplicationService.markRead(currentUser, userNotificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/me/read-all")
    public ResponseEntity<Void> markReadAll(@CurrentUser CurrentUserInfo currentUser) {
        notificationApplicationService.markReadAll(currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me/preferences")
    public ResponseEntity<List<NotificationPreferenceResponse>> getPreferences(@CurrentUser CurrentUserInfo currentUser) {
        return ResponseEntity.ok(notificationApplicationService.getPreferences(currentUser));
    }

    @PutMapping("/me/preferences")
    public ResponseEntity<List<NotificationPreferenceResponse>> upsertPreferences(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestBody @Valid PreferencesUpsertRequest request
    ) {
        return ResponseEntity.ok(notificationApplicationService.upsertPreferences(currentUser, request));
    }

    @PostMapping("/me/preferences/reset")
    public ResponseEntity<List<NotificationPreferenceResponse>> resetPreferences(@CurrentUser CurrentUserInfo currentUser) {
        return ResponseEntity.ok(notificationApplicationService.resetPreferences(currentUser));
    }

    @GetMapping("/admin/rules")
    public ResponseEntity<List<NotificationRuleResponse>> getRules(@CurrentUser CurrentUserInfo currentUser) {
        return ResponseEntity.ok(notificationApplicationService.getRules(currentUser));
    }

    @PutMapping("/admin/rules/{eventType}")
    public ResponseEntity<NotificationRuleResponse> updateRule(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable String eventType,
            @RequestBody @Valid RuleUpdateRequest request
    ) {
        return ResponseEntity.ok(notificationApplicationService.updateRule(currentUser, eventType, request));
    }

    @GetMapping("/admin/templates")
    public ResponseEntity<List<NotificationTemplateResponse>> getTemplates(@CurrentUser CurrentUserInfo currentUser) {
        return ResponseEntity.ok(notificationApplicationService.getTemplates(currentUser));
    }

    @PutMapping("/admin/templates/{code}")
    public ResponseEntity<NotificationTemplateResponse> updateTemplate(
            @CurrentUser CurrentUserInfo currentUser,
            @PathVariable String code,
            @RequestBody @Valid TemplateUpdateRequest request
    ) {
        return ResponseEntity.ok(notificationApplicationService.updateTemplate(currentUser, code, request));
    }
}
