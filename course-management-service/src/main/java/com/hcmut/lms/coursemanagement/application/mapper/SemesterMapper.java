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

    @Mapping(target = "startDate", expression = "java(entity.getStartDate() != null ? entity.getStartDate().format(FORMATTER) : null)")
    @Mapping(target = "endDate", expression = "java(entity.getEndDate() != null ? entity.getEndDate().format(FORMATTER) : null)")
    @Mapping(target = "academicYearId", source = "academicYear.id")
    @Mapping(target = "academicYearCode", source = "academicYear.yearCode")
    @Mapping(target = "semKey", expression = "java(computeSemKey(entity))")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    SemesterResponse toResponse(Semester entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "classSections", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(SemesterRequest request, @MappingTarget Semester entity);

    /**
     * Compute semKey from semester: startYear * 10 + semesterNumber
     * Example: HK1 2023-2024 → 20231
     */
    default Integer computeSemKey(Semester entity) {
        if (entity == null || entity.getAcademicYear() == null) return null;
        String semesterCode = entity.getSemesterCode();
        String yearCode = entity.getAcademicYear().getYearCode();
        if (semesterCode == null || yearCode == null) return null;

        String digits = semesterCode.replaceAll("\\D", "");
        if (digits.isEmpty()) return null;

        try {
            int semNum = Integer.parseInt(digits);
            int startYear = Integer.parseInt(yearCode.split("-")[0].trim());
            return startYear * 10 + semNum;
        } catch (Exception e) {
            return null;
        }
    }
}
