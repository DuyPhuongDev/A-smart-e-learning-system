package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.DepartmentRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DepartmentResponse;
import com.hcmut.lms.coursemanagement.domain.entity.department.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "specializations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Department toEntity(DepartmentRequest request);
    
    @Mapping(target = "facultyId", source = "faculty.id")
    @Mapping(target = "facultyName", source = "faculty.name")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    DepartmentResponse toResponse(Department entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "faculty", ignore = true)
    @Mapping(target = "specializations", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(DepartmentRequest request, @MappingTarget Department entity);
}

