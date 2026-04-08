package com.hcmut.lms.notification.service.impl;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.RuleUpdateRequest;
import com.hcmut.lms.notification.dto.request.TemplateUpdateRequest;
import com.hcmut.lms.notification.dto.response.NotificationRuleResponse;
import com.hcmut.lms.notification.dto.response.NotificationTemplateResponse;
import com.hcmut.lms.notification.entity.NotificationRuleEntity;
import com.hcmut.lms.notification.entity.NotificationTemplateEntity;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.repository.NotificationRuleRepository;
import com.hcmut.lms.notification.repository.NotificationTemplateRepository;
import com.hcmut.lms.notification.service.NotificationConfigService;
import com.hcmut.lms.notification.util.JsonCodec;
import com.hcmut.lms.notification.util.RoleGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationConfigServiceImpl implements NotificationConfigService {

    private final NotificationRuleRepository notificationRuleRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final JsonCodec jsonCodec;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationRuleResponse> getRules(CurrentUserInfo currentUser) {
        RoleGuard.requireAdmin(currentUser);
        return notificationRuleRepository.findAll().stream()
                .map(this::toRuleResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationRuleResponse updateRule(CurrentUserInfo currentUser, String eventType, RuleUpdateRequest request) {
        RoleGuard.requireAdmin(currentUser);
        validateEventType(eventType);
        String normalizedEventType = eventType.trim().toLowerCase();

        NotificationRuleEntity entity = notificationRuleRepository.findById(normalizedEventType)
                .orElseGet(() -> {
                    NotificationRuleEntity created = new NotificationRuleEntity();
                    created.setEventType(normalizedEventType);
                    return created;
                });

        entity.setEnabled(Boolean.TRUE.equals(request.getEnabled()));
        entity.setNotificationType(request.getNotificationType());
        entity.setChannels(jsonCodec.toJsonString(
                request.getChannels().stream().map(Enum::name).toList()));
        entity.setPriority(request.getPriority());
        entity.setFrequency(request.getFrequency());
        entity.setTemplateCode(request.getTemplateCode());
        entity.setTargetMode(request.getTargetMode());
        entity.setTargetPayload(jsonCodec.toJsonString(
                request.getTargetPayload() == null ? Map.of() : request.getTargetPayload()));

        notificationRuleRepository.save(entity);
        return toRuleResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplateResponse> getTemplates(CurrentUserInfo currentUser) {
        RoleGuard.requireAdmin(currentUser);
        return notificationTemplateRepository.findAll().stream()
                .map(this::toTemplateResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationTemplateResponse updateTemplate(CurrentUserInfo currentUser, String code, TemplateUpdateRequest request) {
        RoleGuard.requireAdmin(currentUser);

        NotificationTemplateEntity entity = notificationTemplateRepository.findById(code)
                .orElseGet(() -> {
                    NotificationTemplateEntity created = new NotificationTemplateEntity();
                    created.setCode(code);
                    created.setVersion(0);
                    return created;
                });

        entity.setTitleTemplate(request.getTitleTemplate());
        entity.setContentTemplate(request.getContentTemplate());
        entity.setChannel(request.getChannel());
        entity.setActive(Boolean.TRUE.equals(request.getActive()));
        entity.setVersion(entity.getVersion() + 1);
        entity.setUpdatedBy(currentUser.getId());

        notificationTemplateRepository.save(entity);
        return toTemplateResponse(entity);
    }

    private void validateEventType(String eventType) {
        if (!StringUtils.hasText(eventType)) {
            throw new BadRequestException("eventType is required");
        }
        if (eventType.trim().toLowerCase().startsWith("notification.")) {
            throw new BadRequestException("eventType with 'notification.' prefix is not allowed");
        }
    }

    private NotificationRuleResponse toRuleResponse(NotificationRuleEntity entity) {
        return NotificationRuleResponse.builder()
                .eventType(entity.getEventType())
                .enabled(entity.isEnabled())
                .notificationType(entity.getNotificationType())
                .channels(jsonCodec.toStringSet(entity.getChannels()))
                .priority(entity.getPriority())
                .frequency(entity.getFrequency())
                .templateCode(entity.getTemplateCode())
                .targetMode(entity.getTargetMode())
                .targetPayload(jsonCodec.toJsonNode(entity.getTargetPayload()))
                .build();
    }

    private NotificationTemplateResponse toTemplateResponse(NotificationTemplateEntity entity) {
        return NotificationTemplateResponse.builder()
                .code(entity.getCode())
                .titleTemplate(entity.getTitleTemplate())
                .contentTemplate(entity.getContentTemplate())
                .channel(entity.getChannel())
                .active(entity.isActive())
                .version(entity.getVersion())
                .build();
    }
}
