package com.hcmut.lms.personalization.application.mapper;

import com.hcmut.lms.personalization.application.dto.response.LearningPathResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningPathSectionResponse;
import com.hcmut.lms.personalization.application.dto.response.LearningPathSubjectResponse;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LearningPathMapper {

  @Mapping(target = "sections", ignore = true)
  @Mapping(target = "subjects", ignore = true)
  @Mapping(target = "graph", ignore = true)
  @Mapping(target = "validationConflicts", ignore = true)
  LearningPathResponse toPathResponse(LearningPath entity);

  @Mapping(target = "academicYear", ignore = true)
  @Mapping(target = "semester", ignore = true)
  LearningPathSectionResponse toSectionResponse(LearningPathSection entity);

  @Mapping(target = "prerequisitesGraph", ignore = true)
  LearningPathSubjectResponse toSubjectResponse(LearningPathSubject entity);
}
