package com.hcmut.lms.learning.client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureResponse {
    private UUID id;
    private Boolean isMandatory;
    private LectureType lectureType;
    private Integer estimate_time_spent;
}
