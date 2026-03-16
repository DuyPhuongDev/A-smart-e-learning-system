package com.hcmut.lms.personalization.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectLearningOutcomeResponse {
    private UUID id;
    private String code;
    private String description;
    private String descriptionEn;
    private Integer displayOrder;
}
