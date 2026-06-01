package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurriculumSectionProgressResponse {

  private UUID sectionId;
  private String sectionName;
  private Integer displayOrder;
  private Integer requiredCredits;
  private Integer learningPathCredits;
  private Integer completedCredits;
  private List<CurriculumSectionSubjectResponse> subjects;
}
