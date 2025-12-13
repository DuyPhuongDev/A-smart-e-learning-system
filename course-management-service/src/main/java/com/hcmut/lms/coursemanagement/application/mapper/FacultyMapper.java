package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.FacultyRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.FacultyResponse;
import com.hcmut.lms.coursemanagement.domain.entity.faculty.Faculty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FacultyMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Faculty toEntity(FacultyRequest request);
    
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    FacultyResponse toResponse(Faculty entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "departments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(FacultyRequest request, @MappingTarget Faculty entity);
}

