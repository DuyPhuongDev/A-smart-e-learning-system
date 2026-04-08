package com.hcmut.lms.personalization.application.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlattenOccupationRequest {
  private List<String> occupationCodes;
}
