package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectGradingWeightResponse {
    private UUID gradingId;
    private String gradingType;
    private Float weight;
    private String name;
    private String description;
}
