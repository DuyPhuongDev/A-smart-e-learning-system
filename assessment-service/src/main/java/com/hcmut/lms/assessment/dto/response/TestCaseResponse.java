package com.hcmut.lms.assessment.dto.response;

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
}
