package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CourseMenuLectureDTO {
    @JsonProperty("lecture_id")
    private UUID lectureId;
    private String title;
    private Integer order;
    private String type;
}
