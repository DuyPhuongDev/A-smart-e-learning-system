package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class CourseInfoRequest {
    @NotNull(message = "Course name is mandatory")
    private String courseName;

    private String description;

    private String level;

    private Integer durationHours;

    private String language;

    private MultipartFile introVideo;

    @NotNull(message = "Class section ID is required")
    private UUID classSectionId;

    private MultipartFile thumbnail;

}
