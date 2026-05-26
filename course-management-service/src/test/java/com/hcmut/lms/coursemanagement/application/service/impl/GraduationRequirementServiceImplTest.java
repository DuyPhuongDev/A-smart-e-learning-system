package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.InternalGraduationRequirementResponse;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.graduationRequirement.GraduationRequirement;
import com.hcmut.lms.coursemanagement.repository.GraduationRequirementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GraduationRequirementServiceImplTest {

    @Mock
    private GraduationRequirementRepository graduationRequirementRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private GraduationRequirementServiceImpl graduationRequirementService;

    @Test
    void getActiveRequirementsByStudentId_shouldReturnList_whenRequirementsExist() {
        UUID studentId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setDepartmentId(departmentId);
        student.setIntakeYearId(intakeYearId);

        GraduationRequirement req1 = createRequirement(UUID.randomUUID(), "IELTS", "IELTS-6.5", "English proficiency",
                new BigDecimal("6.5"), "points", "EXACT");
        GraduationRequirement req2 = createRequirement(UUID.randomUUID(), "Credits", "CR-120", "Total credits",
                new BigDecimal("120"), "credits", "AT_LEAST");

        when(userServiceClient.getUserById(studentId)).thenReturn(student);
        when(graduationRequirementRepository.findByDepartmentIdAndIntakeYearIdAndIsActiveTrue(departmentId, intakeYearId))
                .thenReturn(List.of(req1, req2));

        List<InternalGraduationRequirementResponse> result =
                graduationRequirementService.getActiveRequirementsByStudentId(studentId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("IELTS");
        assertThat(result.get(0).getCode()).isEqualTo("IELTS-6.5");
        assertThat(result.get(0).getThresholdValue()).isEqualByComparingTo("6.5");
        assertThat(result.get(1).getName()).isEqualTo("Credits");
        assertThat(result.get(1).getEvaluationRule()).isEqualTo("AT_LEAST");
    }

    @Test
    void getActiveRequirementsByStudentId_shouldReturnEmptyList_whenNoRequirements() {
        UUID studentId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setDepartmentId(departmentId);
        student.setIntakeYearId(intakeYearId);

        when(userServiceClient.getUserById(studentId)).thenReturn(student);
        when(graduationRequirementRepository.findByDepartmentIdAndIntakeYearIdAndIsActiveTrue(departmentId, intakeYearId))
                .thenReturn(List.of());

        List<InternalGraduationRequirementResponse> result =
                graduationRequirementService.getActiveRequirementsByStudentId(studentId);

        assertThat(result).isEmpty();
    }

    @Test
    void getActiveRequirementsByStudentId_shouldThrowException_whenStudentNotFound() {
        UUID studentId = UUID.randomUUID();
        when(userServiceClient.getUserById(studentId)).thenReturn(null);

        assertThatThrownBy(() -> graduationRequirementService.getActiveRequirementsByStudentId(studentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Student not found");
        verify(graduationRequirementRepository, never())
                .findByDepartmentIdAndIntakeYearIdAndIsActiveTrue(any(), any());
    }

    @Test
    void getActiveRequirementsByStudentId_shouldThrowException_whenStudentMissingDepartmentId() {
        UUID studentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setDepartmentId(null);
        student.setIntakeYearId(intakeYearId);

        when(userServiceClient.getUserById(studentId)).thenReturn(student);

        assertThatThrownBy(() -> graduationRequirementService.getActiveRequirementsByStudentId(studentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Student does not have department or intake year configured");
        verify(graduationRequirementRepository, never())
                .findByDepartmentIdAndIntakeYearIdAndIsActiveTrue(any(), any());
    }

    @Test
    void getActiveRequirementsByStudentId_shouldThrowException_whenStudentMissingIntakeYearId() {
        UUID studentId = UUID.randomUUID();
        UUID departmentId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setDepartmentId(departmentId);
        student.setIntakeYearId(null);

        when(userServiceClient.getUserById(studentId)).thenReturn(student);

        assertThatThrownBy(() -> graduationRequirementService.getActiveRequirementsByStudentId(studentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Student does not have department or intake year configured");
        verify(graduationRequirementRepository, never())
                .findByDepartmentIdAndIntakeYearIdAndIsActiveTrue(any(), any());
    }

    // --- helper methods ---

    private GraduationRequirement createRequirement(UUID id, String name, String code, String description,
                                                     BigDecimal thresholdValue, String unit, String evaluationRule) {
        GraduationRequirement req = new GraduationRequirement();
        req.setGraduationRequirementId(id);
        req.setName(name);
        req.setCode(code);
        req.setDescription(description);
        req.setThresholdValue(thresholdValue);
        req.setUnit(unit);
        req.setEvaluationRule(evaluationRule);
        req.setIsActive(true);
        return req;
    }
}
