package com.hcmut.lms.learning.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedStudyTimeResponse {
    private LocalDate date;
    private long totalSeconds;
    private long totalMinutes;
    private double totalHours;
    private long sessionCount;
}
