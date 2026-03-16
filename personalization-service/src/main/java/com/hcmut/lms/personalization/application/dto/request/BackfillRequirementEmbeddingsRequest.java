package com.hcmut.lms.personalization.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackfillRequirementEmbeddingsRequest {
    // Optional; empty means scan all occupation codes.
    private List<String> occupationCodes;

    // "yes" -> generate/update V4 init migration file from current embedding table.
    private String optimal;
}

