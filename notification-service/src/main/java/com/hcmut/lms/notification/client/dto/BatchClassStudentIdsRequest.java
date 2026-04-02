package com.hcmut.lms.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BatchClassStudentIdsRequest {
    private List<UUID> classIds;
}
