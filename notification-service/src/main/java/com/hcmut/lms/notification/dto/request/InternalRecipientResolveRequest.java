package com.hcmut.lms.notification.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InternalRecipientResolveRequest {
    private List<String> roleNames;
    private List<UUID> specializationIds;
    private List<UUID> userIds;
}
