package com.hcmut.lms.usermanagement.client;

import com.hcmut.lms.usermanagement.client.dto.DepartmentResponse;
import com.hcmut.lms.usermanagement.client.dto.SemesterResponse;
import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CourseServiceClientFallback implements CourseServiceClient {
    @Override
    public DepartmentResponse getDepartmentBySpecializationId(UUID specializationId) {
        throw new ResourceNotFoundException("Not supported yet.");
    }

    @Override
    public SemesterResponse getCurrentSemester() {
        throw new ResourceNotFoundException("Not supported yet.");
    }
}
