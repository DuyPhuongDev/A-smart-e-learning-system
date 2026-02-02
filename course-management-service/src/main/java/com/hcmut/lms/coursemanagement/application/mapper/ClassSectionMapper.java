package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassStatus;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.CourseLanguage;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.CourseLevel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClassSectionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "classSectionGradings", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "language", source = "language", qualifiedByName = "stringToCourseLanguage")
    @Mapping(target = "level", source = "level", qualifiedByName = "stringToCourseLevel")
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "introVideo", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "code",  ignore = true)
    @Mapping(target = "maxStudents", ignore = true)
    ClassSection toEntity(ClassSectionRequest dto);
    
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.name")
    @Mapping(target = "semesterId", source = "semester.id")
    @Mapping(target = "semesterCode", source = "semester.semesterCode")
    @Mapping(target = "status", source = "status", qualifiedByName = "classStatusToString")
    @Mapping(target = "language", source = "language", qualifiedByName = "courseLanguageToString")
    @Mapping(target = "level", source = "level", qualifiedByName = "courseLevelToString")
    @Mapping(target = "teacherName", ignore = true)
    ClassSectionResponse toResponseDTO(ClassSection entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "semester", ignore = true)
    @Mapping(target = "chapters", ignore = true)
    @Mapping(target = "classSectionGradings", ignore = true)
    @Mapping(target = "language", source = "language", qualifiedByName = "stringToCourseLanguage")
    @Mapping(target = "level", source = "level", qualifiedByName = "stringToCourseLevel")
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void updateEntityFromDTO(ClassSectionRequest dto, @MappingTarget ClassSection entity);
    
    @Named("stringToCourseLanguage")
    default CourseLanguage stringToCourseLanguage(String language) {
        if (language == null || language.isEmpty()) {
            return null;
        }
        try {
            return CourseLanguage.valueOf(language.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Named("stringToCourseLevel")
    default CourseLevel stringToCourseLevel(String level) {
        if (level == null || level.isEmpty()) {
            return null;
        }
        try {
            return CourseLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Named("classStatusToString")
    default String classStatusToString(ClassStatus status) {
        return status != null ? status.name() : null;
    }
    
    @Named("courseLanguageToString")
    default String courseLanguageToString(CourseLanguage language) {
        return language != null ? language.name() : null;
    }
    
    @Named("courseLevelToString")
    default String courseLevelToString(CourseLevel level) {
        return level != null ? level.name() : null;
    }
}

