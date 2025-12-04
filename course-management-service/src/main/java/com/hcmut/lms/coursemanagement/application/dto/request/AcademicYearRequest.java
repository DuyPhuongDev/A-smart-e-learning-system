package com.hcmut.lms.coursemanagement.application.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AcademicYearRequest {
    private String yearCode;
    private LocalDate startDate;
    private LocalDate endDate;
}
