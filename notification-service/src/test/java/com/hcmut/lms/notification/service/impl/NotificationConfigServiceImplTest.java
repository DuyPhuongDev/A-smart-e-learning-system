package com.hcmut.lms.notification.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.RuleUpdateRequest;
import com.hcmut.lms.notification.dto.request.TemplateUpdateRequest;
import com.hcmut.lms.notification.dto.response.NotificationRuleResponse;
import com.hcmut.lms.notification.dto.response.NotificationTemplateResponse;
import com.hcmut.lms.notification.entity.NotificationRuleEntity;
import com.hcmut.lms.notification.entity.NotificationTemplateEntity;
import com.hcmut.lms.notification.enums.NotificationChannel;
import com.hcmut.lms.notification.enums.NotificationFrequency;
import com.hcmut.lms.notification.enums.NotificationPriority;
import com.hcmut.lms.notification.enums.NotificationType;
import com.hcmut.lms.notification.enums.TargetMode;
import com.hcmut.lms.notification.exception.BadRequestException;
import com.hcmut.lms.notification.repository.NotificationRuleRepository;
import com.hcmut.lms.notification.repository.NotificationTemplateRepository;
import com.hcmut.lms.notification.util.JsonCodec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConfigServiceImplTest {

    @Mock private NotificationRuleRepository notificationRuleRepository;
    @Mock private NotificationTemplateRepository notificationTemplateRepository;
    @Mock private JsonCodec jsonCodec;

    @InjectMocks
    private NotificationConfigServiceImpl notificationConfigService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void getRules_shouldReturnAll() {
        CurrentUserInfo user = createAdmin();
        NotificationRuleEntity rule = createRule();
        when(notificationRuleRepository.findAll()).thenReturn(List.of(rule));
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));
        when(jsonCodec.toJsonNode(any())).thenReturn(mapper.createObjectNode());

        List<NotificationRuleResponse> result = notificationConfigService.getRules(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEventType()).isEqualTo("test.event");
    }

    @Test
    void getRules_shouldThrowForbidden_whenNotAdmin() {
        CurrentUserInfo user = createStudent();

        assertThatThrownBy(() -> notificationConfigService.getRules(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Admin");
    }

    @Test
    void updateRule_shouldUpdateExisting() {
        CurrentUserInfo user = createAdmin();
        String eventType = "test.event";
        NotificationRuleEntity existing = createRule();
        RuleUpdateRequest request = createRuleUpdateRequest();

        when(notificationRuleRepository.findById("test.event")).thenReturn(Optional.of(existing));
        when(jsonCodec.toJsonString(any())).thenReturn("[\"IN_APP\"]");
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));
        when(jsonCodec.toJsonNode(any())).thenReturn(mapper.createObjectNode());

        NotificationRuleResponse result = notificationConfigService.updateRule(user, eventType, request);

        verify(notificationRuleRepository).save(existing);
        assertThat(result.getEventType()).isEqualTo("test.event");
    }

    @Test
    void updateRule_shouldCreateNew_whenNotFound() {
        CurrentUserInfo user = createAdmin();
        String eventType = "new.event";
        RuleUpdateRequest request = createRuleUpdateRequest();

        when(notificationRuleRepository.findById("new.event")).thenReturn(Optional.empty());
        when(jsonCodec.toJsonString(any())).thenReturn("[\"IN_APP\"]");
        when(jsonCodec.toStringSet(any())).thenReturn(Set.of("IN_APP"));
        when(jsonCodec.toJsonNode(any())).thenReturn(mapper.createObjectNode());

        NotificationRuleResponse result = notificationConfigService.updateRule(user, eventType, request);

        verify(notificationRuleRepository).save(any(NotificationRuleEntity.class));
        assertThat(result.getEventType()).isEqualTo("new.event");
    }

    @Test
    void updateRule_shouldThrow_whenEventTypeEmpty() {
        CurrentUserInfo user = createAdmin();
        RuleUpdateRequest request = createRuleUpdateRequest();

        assertThatThrownBy(() -> notificationConfigService.updateRule(user, "", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("eventType");
    }

    @Test
    void updateRule_shouldThrow_whenEventTypeHasNotificationPrefix() {
        CurrentUserInfo user = createAdmin();
        RuleUpdateRequest request = createRuleUpdateRequest();

        assertThatThrownBy(() -> notificationConfigService.updateRule(user, "notification.test", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("notification.");
    }

    @Test
    void getTemplates_shouldReturnAll() {
        CurrentUserInfo user = createAdmin();
        NotificationTemplateEntity template = createTemplate();
        when(notificationTemplateRepository.findAll()).thenReturn(List.of(template));

        List<NotificationTemplateResponse> result = notificationConfigService.getTemplates(user);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getCode()).isEqualTo("welcome");
    }

    @Test
    void updateTemplate_shouldUpdateExisting() {
        CurrentUserInfo user = createAdmin();
        String code = "welcome";
        NotificationTemplateEntity existing = createTemplate();
        TemplateUpdateRequest request = createTemplateUpdateRequest();

        when(notificationTemplateRepository.findById(code)).thenReturn(Optional.of(existing));

        NotificationTemplateResponse result = notificationConfigService.updateTemplate(user, code, request);

        verify(notificationTemplateRepository).save(existing);
        assertThat(result.getCode()).isEqualTo("welcome");
        assertThat(existing.getVersion()).isEqualTo(2); // was 1, incremented
    }

    @Test
    void updateTemplate_shouldCreateNew_whenNotFound() {
        CurrentUserInfo user = createAdmin();
        String code = "new-template";
        TemplateUpdateRequest request = createTemplateUpdateRequest();

        when(notificationTemplateRepository.findById(code)).thenReturn(Optional.empty());

        NotificationTemplateResponse result = notificationConfigService.updateTemplate(user, code, request);

        verify(notificationTemplateRepository).save(any(NotificationTemplateEntity.class));
        assertThat(result.getCode()).isEqualTo("new-template");
        assertThat(result.getVersion()).isEqualTo(1); // 0 + 1
    }

    private CurrentUserInfo createAdmin() {
        return CurrentUserInfo.builder()
                .id(UUID.randomUUID())
                .email("admin@hcmut.edu.vn")
                .role("ADMIN")
                .build();
    }

    private CurrentUserInfo createStudent() {
        return CurrentUserInfo.builder()
                .id(UUID.randomUUID())
                .email("student@hcmut.edu.vn")
                .role("STUDENT")
                .build();
    }

    private NotificationRuleEntity createRule() {
        NotificationRuleEntity rule = new NotificationRuleEntity();
        rule.setEventType("test.event");
        rule.setEnabled(true);
        rule.setNotificationType(NotificationType.SYSTEM_ANNOUNCEMENT);
        rule.setChannels("[\"IN_APP\"]");
        rule.setPriority(NotificationPriority.MEDIUM);
        rule.setFrequency(NotificationFrequency.IMMEDIATE);
        rule.setTargetMode(TargetMode.ALL);
        rule.setTargetPayload("{}");
        return rule;
    }

    private RuleUpdateRequest createRuleUpdateRequest() {
        RuleUpdateRequest req = new RuleUpdateRequest();
        req.setEnabled(true);
        req.setNotificationType(NotificationType.SYSTEM_ANNOUNCEMENT);
        req.setChannels(Set.of(NotificationChannel.IN_APP));
        req.setPriority(NotificationPriority.MEDIUM);
        req.setFrequency(NotificationFrequency.IMMEDIATE);
        req.setTargetMode(TargetMode.ALL);
        return req;
    }

    private NotificationTemplateEntity createTemplate() {
        NotificationTemplateEntity template = new NotificationTemplateEntity();
        template.setCode("welcome");
        template.setTitleTemplate("Welcome {{name}}");
        template.setContentTemplate("Hello {{name}}");
        template.setChannel(NotificationChannel.IN_APP);
        template.setActive(true);
        template.setVersion(1);
        return template;
    }

    private TemplateUpdateRequest createTemplateUpdateRequest() {
        TemplateUpdateRequest req = new TemplateUpdateRequest();
        req.setTitleTemplate("Updated Title");
        req.setContentTemplate("Updated Content");
        req.setChannel(NotificationChannel.EMAIL);
        req.setActive(true);
        return req;
    }
}
