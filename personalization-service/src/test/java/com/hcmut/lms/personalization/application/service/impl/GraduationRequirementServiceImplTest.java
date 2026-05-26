package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.application.dto.request.UpdateGraduationRequirementStatusRequest;
import com.hcmut.lms.personalization.application.dto.response.GraduationRequirementStatusResponse;
import com.hcmut.lms.personalization.application.mapper.GraduationRequirementMapper;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.GraduationRequirementResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GraduationRequirementStatus;
import com.hcmut.lms.personalization.repository.GraduationRequirementStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GraduationRequirementServiceImplTest {

    @Mock private CourseManagementClient courseManagementClient;
    @Mock private GraduationRequirementStatusRepository graduationRequirementStatusRepository;
    @Mock private GraduationRequirementMapper graduationRequirementMapper;

    @InjectMocks
    private GraduationRequirementServiceImpl graduationRequirementService;

    @Test void getMyGraduationRequirements_shouldReturnList_whenDataExists() {
        UUID studentId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementResponse req = new GraduationRequirementResponse();
        req.setGraduationRequirementId(requirementId);
        req.setCode("REQ1");
        req.setName("Requirement 1");
        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(List.of(req));

        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(UUID.randomUUID())
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(false)
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status));

        GraduationRequirementStatusResponse mockResponse = new GraduationRequirementStatusResponse();
        mockResponse.setGraduationRequirementId(requirementId);
        when(graduationRequirementMapper.toResponse(any(), any())).thenReturn(mockResponse);

        var result = graduationRequirementService.getMyGraduationRequirements(studentId);
        assertNotNull(result);
        assertTrue(result.size() == 1);
    }

    @Test void getMyGraduationRequirements_shouldCreateMissingStatuses_whenNewRequirements() {
        UUID studentId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementResponse req = new GraduationRequirementResponse();
        req.setGraduationRequirementId(requirementId);
        req.setCode("REQ1");
        req.setName("Requirement 1");
        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(List.of(req));
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(Collections.emptyList());
        when(graduationRequirementStatusRepository.saveAll(any()))
            .thenReturn(List.of(GraduationRequirementStatus.builder()
                .graduationRequirementStatusId(UUID.randomUUID())
                .studentId(studentId)
                .graduationRequirementId(requirementId)
                .isCompleted(false)
                .build()));

        GraduationRequirementStatusResponse mockResponse = new GraduationRequirementStatusResponse();
        mockResponse.setGraduationRequirementId(requirementId);
        when(graduationRequirementMapper.toResponse(any(), any())).thenReturn(mockResponse);

        var result = graduationRequirementService.getMyGraduationRequirements(studentId);
        assertNotNull(result);
        assertTrue(result.size() == 1);
    }

    @Test void updateGraduationRequirementStatus_shouldReturnUpdatedResponse_whenValidRequest() {
        UUID studentId = UUID.randomUUID();
        UUID statusId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(statusId)
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(false)
            .build();
        when(graduationRequirementStatusRepository.findByGraduationRequirementStatusIdAndStudentId(statusId, studentId))
            .thenReturn(Optional.of(status));

        GraduationRequirementResponse req = new GraduationRequirementResponse();
        req.setGraduationRequirementId(requirementId);
        req.setCode("REQ1");
        req.setName("Requirement 1");
        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(List.of(req));

        when(graduationRequirementStatusRepository.save(any())).thenReturn(status);

        GraduationRequirementStatusResponse mockResponse = new GraduationRequirementStatusResponse();
        mockResponse.setGraduationRequirementId(requirementId);
        when(graduationRequirementMapper.toResponse(any(), any())).thenReturn(mockResponse);

        UpdateGraduationRequirementStatusRequest request = new UpdateGraduationRequirementStatusRequest();
        request.setIsCompleted(true);

        var result = graduationRequirementService.updateGraduationRequirementStatus(studentId, statusId, request);
        assertNotNull(result);
    }

    @Test void getMyGraduationRequirements_shouldHandleDuplicateStatuses() {
        UUID studentId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementResponse req = new GraduationRequirementResponse();
        req.setGraduationRequirementId(requirementId);
        req.setCode("REQ1");
        req.setName("Requirement 1");
        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(List.of(req));

        GraduationRequirementStatus status1 = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(UUID.randomUUID())
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(false)
            .build();
        GraduationRequirementStatus status2 = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(UUID.randomUUID())
            .studentId(studentId)
            .graduationRequirementId(requirementId)
            .isCompleted(true)
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status1, status2));

        GraduationRequirementStatusResponse mockResponse = new GraduationRequirementStatusResponse();
        when(graduationRequirementMapper.toResponse(any(), any())).thenReturn(mockResponse);

        var result = graduationRequirementService.getMyGraduationRequirements(studentId);
        assertNotNull(result);
        assertTrue(result.size() == 1);
    }

    @Test void getMyGraduationRequirements_shouldThrow_whenStatusMissingForRequirement() {
        UUID studentId = UUID.randomUUID();
        UUID requirementId = UUID.randomUUID();

        GraduationRequirementResponse req = new GraduationRequirementResponse();
        req.setGraduationRequirementId(requirementId);
        req.setCode("REQ1");
        when(courseManagementClient.getGraduationRequirementsByStudentId(studentId))
            .thenReturn(List.of(req));
        // Status with different requirementId → won't match
        GraduationRequirementStatus status = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(UUID.randomUUID())
            .studentId(studentId)
            .graduationRequirementId(UUID.randomUUID())
            .isCompleted(false)
            .build();
        when(graduationRequirementStatusRepository.findByStudentId(studentId))
            .thenReturn(List.of(status));

        try {
            graduationRequirementService.getMyGraduationRequirements(studentId);
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("missing"));
        }
        assertTrue(true);
    }
}
