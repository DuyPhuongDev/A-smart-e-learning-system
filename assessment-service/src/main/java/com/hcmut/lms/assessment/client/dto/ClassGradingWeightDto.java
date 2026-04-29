package com.hcmut.lms.assessment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassGradingWeightDto {
    private String gradingType;
    private Float weight;
}
