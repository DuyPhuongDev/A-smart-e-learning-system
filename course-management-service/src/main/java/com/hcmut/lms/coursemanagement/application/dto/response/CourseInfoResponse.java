package com.hcmut.lms.coursemanagement.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfoId;
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
public class CourseInfoResponse {
    private CourseInfoId courseInfoId;
    private String description;
    private String language;
    private String level;
    private Integer durationHours;
    private String thumbnail;
    private String introVideo;
    private UUID classSectionId;
    private String createdAt;
    private String updatedAt;
}
