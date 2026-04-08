package com.hcmut.lms.personalization.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathGraphResponse {

  private List<PrerequisiteGraphNodeResponse> nodes;
  private List<PrerequisiteGraphEdgeResponse> edges;
}

