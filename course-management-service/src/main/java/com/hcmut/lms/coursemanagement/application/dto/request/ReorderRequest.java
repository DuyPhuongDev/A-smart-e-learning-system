package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderRequest {
    @NotNull(message = "Item ID is required")
    private UUID id;
    
    @NotNull(message = "New order index is required")
    private Integer newOrderIndex;
}

