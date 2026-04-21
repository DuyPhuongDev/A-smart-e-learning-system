package com.hcmut.lms.assessment.dto.response;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResponse {
    private UUID id;
    private String input;
    private String expected;
    private boolean hidden;

    public static TestCaseResponse toResponse(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expected(testCase.getExpected())
                .hidden(testCase.isHidden())
                .build();
    }
}
