package com.hcmut.lms.assessment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingAssessmentCountResponse {
    private UUID classId;
    private Integer submittedCount;
    private Integer count;
}
