package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EnrollmentMapper {

    @Mapping(target = "id", ignore = true)
    Enrollment toEntity(EnrollmentRequest request);

    EnrollmentResponse toResponse(Enrollment enrollment);
}
