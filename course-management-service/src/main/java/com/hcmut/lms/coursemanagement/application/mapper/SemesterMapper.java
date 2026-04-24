package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.SemesterRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.util.SemesterUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, imports = {SemesterUtil.class})
public interface SemesterMapper {

  DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "academicYear", ignore = true)
  @Mapping(target = "classSections", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Semester toEntity(SemesterRequest request);

  @Mapping(target = "startDate", expression = "java(entity.getStartDate() != null ? entity.getStartDate().format" +
      "(FORMATTER) : null)")
  @Mapping(target = "endDate", expression = "java(entity.getEndDate() != null ? entity.getEndDate().format(FORMATTER)"
      + " : null)")
  @Mapping(target = "academicYearId", source = "academicYear.id")
  @Mapping(target = "academicYearCode", source = "academicYear.yearCode")
  @Mapping(target = "semKey", expression = "java(SemesterUtil.computeSemKey(entity))")
  @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() "
      + ": null)")
  @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() "
      + ": null)")
  SemesterResponse toResponse(Semester entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "academicYear", ignore = true)
  @Mapping(target = "classSections", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  void updateEntityFromRequest(SemesterRequest request, @MappingTarget Semester entity);


}
