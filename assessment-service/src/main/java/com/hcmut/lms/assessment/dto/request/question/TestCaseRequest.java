package com.hcmut.lms.assessment.dto.request.question;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseRequest {
    private String input;
    private String expected;
    private boolean hidden;
}
