package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.AcademicYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.AcademicYearResponse;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AcademicYearMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "semesters", ignore = true)
    AcademicYear toEntity(AcademicYearRequest request);

    @Mapping(target = "startDate", expression = "java(entity.getStartDate() != null ? entity.getStartDate().format(FORMATTER) : null)")
    @Mapping(target = "endDate", expression = "java(entity.getEndDate() != null ? entity.getEndDate().format(FORMATTER) : null)")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    AcademicYearResponse toResponse(AcademicYear entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "semesters", ignore = true)
    void updateEntityFromRequest(AcademicYearRequest request, @MappingTarget AcademicYear entity);
}
