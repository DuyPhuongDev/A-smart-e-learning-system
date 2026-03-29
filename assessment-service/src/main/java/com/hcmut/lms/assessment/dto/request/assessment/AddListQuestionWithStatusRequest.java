package com.hcmut.lms.assessment.dto.request.assessment;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddListQuestionWithStatusRequest {
    private AssessmentStatus status;
    private List<AddQuestionRequest> questions;
}
