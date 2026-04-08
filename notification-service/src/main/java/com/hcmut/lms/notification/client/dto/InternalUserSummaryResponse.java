package com.hcmut.lms.notification.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class InternalUserSummaryResponse {
    private UUID id;
    private String email;
    private String roleName;
    private UUID specializationId;
}
