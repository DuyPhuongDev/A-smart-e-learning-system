package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.SemesterRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
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
  @Mapping(target = "semKey", expression = "java(computeSemKey(entity))")
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

  /**
   * Compute semKey from a semester code string.
   * <p>
   * Expected format: {@code HK(YY)(S)} where YY is 2-digit year and S is semester number.
   * Examples: HK231 → 20231, HK242 → 20242, HK253 → 20253
   *
   * @param semester the semester entity (e.g., "HK231")
   * @return semKey integer, or null if the code is null or malformed
   */
  default Integer computeSemKey(Semester semester) {
    String semesterCode = semester.getSemesterCode();
    if (semesterCode == null) {
      return null;
    }

    // Expected format HK(YY)(S) requires at least 5 characters (e.g., HK231)
    if (semesterCode.length() < 5 || !semesterCode.startsWith("HK")) {
      return null;
    }

    try {
      // Extract YY: indices 2 and 3 (e.g., "23")
      String yearPart = semesterCode.substring(2, 4);
      // Extract S: index 4 onwards (e.g., "1")
      String semPart = semesterCode.substring(4);

      int yy = Integer.parseInt(yearPart);
      int semNum = Integer.parseInt(semPart);

      // Standardize to 4-digit year assuming 21st century (2000s)
      int startYear = 2000 + yy;

      return (startYear * 10) + semNum;
    } catch (NumberFormatException | IndexOutOfBoundsException e) {
      return null;
    }
  }
}
