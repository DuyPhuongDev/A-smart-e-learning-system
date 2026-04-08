package com.hcmut.lms.learning.dto.internal;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InternalClassStudentIdsResponse {
    private UUID classId;
    private List<UUID> studentIds;
}
