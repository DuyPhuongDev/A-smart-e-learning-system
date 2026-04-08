package com.hcmut.lms.personalization.application.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreatePreferredSummerSemesterRequest {

  @NotNull(message = "semesterId is required")
  private UUID semesterId;

  @NotBlank(message = "learnIntensity is required")
  private String learnIntensity;
}

