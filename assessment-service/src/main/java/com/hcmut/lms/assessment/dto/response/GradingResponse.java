package com.hcmut.lms.assessment.dto.response;

import com.hcmut.lms.assessment.handler.dto.FeedbackDto;
import com.hcmut.lms.assessment.handler.dto.GradingResult;
import com.hcmut.lms.assessment.handler.dto.GradingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradingResponse {
    private UUID questionId;
    private BigDecimal earnedPoints;
    private BigDecimal maxPoints;
    private GradingStatus status;
    private String detail;
    private FeedbackDto feedback;

    public static GradingResponse from(GradingResult result, FeedbackDto feedback) {
        return GradingResponse.builder()
                .questionId(result.getQuestionId())
                .earnedPoints(result.getEarnedPoints())
                .maxPoints(result.getMaxPoints())
                .status(result.getStatus())
                .detail(result.getDetail())
                .feedback(feedback)
                .build();
    }
}
