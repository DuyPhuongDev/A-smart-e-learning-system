package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.CourseInfoRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseInfoResponse;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseInfoMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classSection", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "introVideo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseInfo toEntity(CourseInfoRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classSection", ignore = true)
    @Mapping(target = "thumbnailUrl", ignore = true)
    @Mapping(target = "introVideo", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CourseInfoRequest request, @MappingTarget CourseInfo entity);

    @Mapping(target = "classSectionId", source = "classSection.id")
    @Mapping(target = "thumbnail", source = "thumbnailUrl")
    CourseInfoResponse toResponseEntity(CourseInfo entity);
}

