package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OccupationDetailResponse {
    private String onetsocCode;
    private String title;
    private String description;
    private Long skillsCount;
    private Long tasksCount;
    private Long dwaCount;
}
