package com.hcmut.lms.assessment.dto.request.teacher;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeacherEssayGradesRequest {

    @NotEmpty(message = "items must not be empty")
    @Valid
    private List<TeacherEssayGradeItemRequest> items;
}
