package com.hcmut.lms.assessment.dto.request.question;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class ReorderRequest {
    @NotNull(message = "New order index is required")
    private Integer newOrderIndex;
}
