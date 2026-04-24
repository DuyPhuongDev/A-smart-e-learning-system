package com.hcmut.lms.personalization.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectChangeDto {

    @NotBlank(message = "Hành động không được trống")
    private String action; // ADD, REMOVE, MOVE

    @NotNull(message = "Mã học phần không được trống")
    private UUID subjectId;

    private UUID targetSectionId; // for ADD and MOVE

    private UUID fromSectionId; // for MOVE and REMOVE

    private UUID curriculumSubjectId; // for ADD

    private String subjectCode; // for ADD

    private String subjectName; // for ADD

    private Integer credits; // for ADD
}