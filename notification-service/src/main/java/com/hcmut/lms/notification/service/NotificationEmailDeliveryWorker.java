package com.hcmut.lms.notification.service;

import com.hcmut.lms.notification.client.UserManagementInternalClient;
import com.hcmut.lms.notification.client.dto.InternalResolveUsersRequest;
import com.hcmut.lms.notification.client.dto.InternalUserSummaryResponse;
import com.hcmut.lms.notification.config.NotificationProperties;
import com.hcmut.lms.notification.entity.NotificationEntity;
import com.hcmut.lms.notification.entity.NotificationDlqEntity;
import com.hcmut.lms.notification.entity.NotificationDeliveryLogEntity;
import com.hcmut.lms.notification.entity.UserNotificationEntity;
import com.hcmut.lms.notification.enums.DeliveryStatus;
import com.hcmut.lms.notification.enums.DeferReason;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.repository.NotificationDeliveryLogRepository;
import com.hcmut.lms.notification.repository.NotificationDlqRepository;
import com.hcmut.lms.notification.repository.UserNotificationRepository;
import com.hcmut.lms.notification.util.JsonCodec;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEmailDeliveryWorker {

    private final UserNotificationRepository userNotificationRepository;
    private final NotificationDeliveryLogRepository notificationDeliveryLogRepository;
    private final NotificationDlqRepository notificationDlqRepository;
    private final NotificationMetricsService notificationMetricsService;
    private final NotificationProperties notificationProperties;
    private final UserManagementInternalClient userManagementInternalClient;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final JsonCodec jsonCodec;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeDelivery(UUID userNotificationId) {
        UserNotificationEntity userNotification = userNotificationRepository.findById(userNotificationId).orElse(null);
        if (userNotification == null) {
            return;
        }
        if (userNotification.getChannel() != NotificationChannel.EMAIL) {
            return;
        }

        NotificationEntity notification = userNotification.getNotification();
        String subject = notification.getTitle();
        String title = notification.getTitle();
        String textContent = notification.getContent();

        long startedAt = System.currentTimeMillis();
        try {
            sendTemplatedNotificationEmail(userNotification.getUserId(), subject, title, textContent);

            userNotification.setDeliveryStatus(DeliveryStatus.DELIVERED);
            userNotification.setDeliveredAt(Instant.now());
            userNotification.setDeliveryAttempts(userNotification.getDeliveryAttempts() + 1);
            userNotification.setNextAttemptAt(null);
            userNotification.setLastError(null);
            userNotification.setDeferReason(null);
            userNotificationRepository.save(userNotification);

            logDelivery(userNotification, "smtp-gmail", "sent", "250", System.currentTimeMillis() - startedAt, true);
            notificationMetricsService.incrementDelivery(NotificationChannel.EMAIL, DeliveryStatus.DELIVERED);
            notificationMetricsService.recordDeliveryLatency(NotificationChannel.EMAIL, System.currentTimeMillis() - startedAt);
        } catch (Exception ex) {
            int attempts = userNotification.getDeliveryAttempts() + 1;
            userNotification.setDeliveryAttempts(attempts);
            userNotification.setDeliveryStatus(DeliveryStatus.FAILED);
            userNotification.setLastError(ex.getMessage());
            userNotification.setDeferReason(DeferReason.RETRY);
            userNotification.setNextAttemptAt(nextRetryTime(attempts));
            userNotificationRepository.save(userNotification);

            if (nextRetryTime(attempts) == null) {
                moveUserNotificationToDlq(userNotification, ex.getMessage());
            }

            logDelivery(userNotification, "smtp-gmail", "failed", "500", System.currentTimeMillis() - startedAt, false);
            notificationMetricsService.incrementDelivery(NotificationChannel.EMAIL, DeliveryStatus.FAILED);
            notificationMetricsService.recordDeliveryLatency(NotificationChannel.EMAIL, System.currentTimeMillis() - startedAt);
            log.warn("Email delivery failed userNotificationId={}: {}", userNotificationId, ex.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendDigest(UUID userId, String subject, String plainBody) {
        long startedAt = System.currentTimeMillis();
        try {
            String to = resolveUserEmail(userId);
            if (!StringUtils.hasText(to)) {
                throw new IllegalStateException("Cannot resolve email for user " + userId);
            }
            Context ctx = new Context(Locale.forLanguageTag("vi"));
            ctx.setVariable("subject", subject);
            ctx.setVariable("body", plainBody);
            String html = templateEngine.process("mail/digest", ctx);
            MimeMessage mime = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(notificationProperties.getEmail().getFrom());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            javaMailSender.send(mime);
            log.debug("Digest email sent userId={} latencyMs={}", userId, System.currentTimeMillis() - startedAt);
        } catch (Exception ex) {
            log.error("Digest email failed userId={}: {}", userId, ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }

    private void sendTemplatedNotificationEmail(UUID userId, String subject, String title, String textContent) throws Exception {
        String to = resolveUserEmail(userId);
        if (!StringUtils.hasText(to)) {
            throw new IllegalStateException("Cannot resolve email for user " + userId);
        }
        Context ctx = new Context(Locale.forLanguageTag("vi"));
        ctx.setVariable("subject", subject);
        ctx.setVariable("title", title);
        ctx.setVariable("content", textContent);
        String html = templateEngine.process("mail/notification", ctx);
        MimeMessage mime = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
        helper.setFrom(notificationProperties.getEmail().getFrom());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        javaMailSender.send(mime);
    }

    private String resolveUserEmail(UUID userId) {
        List<InternalUserSummaryResponse> users = userManagementInternalClient.resolveUsers(
                InternalResolveUsersRequest.builder()
                        .userIds(List.of(userId))
                        .build()
        );
        return users.stream()
                .filter(user -> userId.equals(user.getId()))
                .map(InternalUserSummaryResponse::getEmail)
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private void moveUserNotificationToDlq(UserNotificationEntity userNotification, String errorMessage) {
        NotificationDlqEntity dlq = new NotificationDlqEntity();
        dlq.setId(UUID.randomUUID());
        dlq.setOutboxId(null);
        dlq.setEventType("delivery.email");
        dlq.setEventKey(userNotification.getId().toString());
        dlq.setPayload(jsonCodec.toJsonString(Map.of(
                "userNotificationId", userNotification.getId(),
                "notificationId", userNotification.getNotification().getId(),
                "userId", userNotification.getUserId()
        )));
        dlq.setErrorMessage(errorMessage);
        dlq.setAttempts(userNotification.getDeliveryAttempts());
        dlq.setCreatedAt(Instant.now());
        notificationDlqRepository.save(dlq);
        notificationMetricsService.incrementDlq();
    }

    private void logDelivery(
            UserNotificationEntity userNotification,
            String provider,
            String responsePayload,
            String responseCode,
            long latencyMs,
            boolean success
    ) {
        NotificationDeliveryLogEntity logEntity = new NotificationDeliveryLogEntity();
        logEntity.setId(UUID.randomUUID());
        logEntity.setUserNotification(userNotification);
        logEntity.setProvider(provider);
        logEntity.setChannel(userNotification.getChannel());
        logEntity.setRequestPayload(userNotification.getNotification().getContent());
        logEntity.setResponsePayload(responsePayload);
        logEntity.setResponseCode(responseCode);
        logEntity.setLatencyMs(Math.max(latencyMs, 0));
        logEntity.setSuccess(success);
        logEntity.setCreatedAt(Instant.now());
        notificationDeliveryLogRepository.save(logEntity);
    }

    private Instant nextRetryTime(int attempts) {
        List<Integer> retryDelays = parseRetryDelays();
        if (attempts <= 0 || attempts > retryDelays.size()) {
            return null;
        }
        return Instant.now().plus(Duration.ofMinutes(retryDelays.get(attempts - 1)));
    }

    private List<Integer> parseRetryDelays() {
        return Arrays.stream(notificationProperties.getEmail().getRetryDelaysMinutes().split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(Integer::parseInt)
                .toList();
    }
}
