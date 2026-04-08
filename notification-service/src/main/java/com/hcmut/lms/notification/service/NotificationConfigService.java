package com.hcmut.lms.notification.service;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.dto.request.RuleUpdateRequest;
import com.hcmut.lms.notification.dto.request.TemplateUpdateRequest;
import com.hcmut.lms.notification.dto.response.NotificationRuleResponse;
import com.hcmut.lms.notification.dto.response.NotificationTemplateResponse;

import java.util.List;

public interface NotificationConfigService {

    List<NotificationRuleResponse> getRules(CurrentUserInfo currentUser);

    NotificationRuleResponse updateRule(CurrentUserInfo currentUser, String eventType, RuleUpdateRequest request);

    List<NotificationTemplateResponse> getTemplates(CurrentUserInfo currentUser);

    NotificationTemplateResponse updateTemplate(CurrentUserInfo currentUser, String code, TemplateUpdateRequest request);
}
