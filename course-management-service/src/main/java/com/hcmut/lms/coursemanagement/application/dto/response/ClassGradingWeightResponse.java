package com.hcmut.lms.coursemanagement.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassGradingWeightResponse {
    private String gradingType;
    private Float weight;
}
