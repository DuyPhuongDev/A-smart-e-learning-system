package com.hcmut.lms.usermanagement.model.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class InternalResolveUsersRequest {
    private List<String> roleNames;
    private List<UUID> specializationIds;
    private List<UUID> userIds;
}
