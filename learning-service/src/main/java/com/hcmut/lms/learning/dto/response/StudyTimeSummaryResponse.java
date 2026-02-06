package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for study time summary/statistics
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudyTimeSummaryResponse {
    private UUID studentId;
    private UUID classId;
    private LocalDate date;
    private Integer totalSeconds;
    private Integer totalMinutes;
    private Integer totalHours;
    private Long sessionCount;
}
