package com.hcmut.lms.assessment.controller.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingAssessmentCountRequest {
    private List<UUID> classIds;
    private UUID studentId;
}
