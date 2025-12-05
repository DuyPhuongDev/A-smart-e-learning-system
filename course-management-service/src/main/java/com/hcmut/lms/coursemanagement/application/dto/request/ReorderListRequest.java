package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReorderListRequest {
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<ReorderRequest> items;
}

