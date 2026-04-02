package com.hcmut.lms.learning.dto.internal;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class InternalBatchClassStudentIdsRequest {
    private List<UUID> classIds;
}
