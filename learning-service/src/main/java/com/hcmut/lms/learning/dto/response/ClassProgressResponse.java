package com.hcmut.lms.learning.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for class-level progress summary
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassProgressResponse {
    private UUID studentId;
    private UUID classId;
    private BigDecimal overallProgressPercentage;
    private Integer totalLectures;
    private Integer completedLectures;
    private Integer totalStudyTimeSeconds;
    private List<LectureProgressSummary> lectureProgresses;
    
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LectureProgressSummary {
        private UUID lectureId;
        private String lectureTitle;
        private BigDecimal progressPercentage;
        private Integer totalTimeSpent;
        private Boolean isCompleted;
        private Instant completedAt;
    }
}
