package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClassSectionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "classSectionGradings", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teacherId", ignore = true)
    ClassSection toEntity(ClassSectionRequest dto);
    
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterCode", source = "semester.semesterCode")
    ClassSectionResponse toResponseDTO(ClassSection entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "classSectionGradings", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teacherId", ignore = true)
    void updateEntityFromDTO(ClassSectionRequest dto, @MappingTarget ClassSection entity);
}

