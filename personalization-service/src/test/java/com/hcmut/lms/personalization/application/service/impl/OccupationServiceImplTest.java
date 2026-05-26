package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.application.mapper.OccupationMapper;
import com.hcmut.lms.personalization.application.service.ValuationOccupationService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.OccupationDataRepository;
import com.hcmut.lms.personalization.repository.OccupationSummaryRepository;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OccupationServiceImplTest {

    @Mock private OccupationDataRepository occupationDataRepository;
    @Mock private OccupationSummaryRepository occupationSummaryRepository;
    @Mock private SubjectOccupationValuationRepository subjectOccupationValuationRepository;
    @Mock private OccupationMapper occupationMapper;
    @Mock private ValuationOccupationService valuationOccupationService;
    @Mock private CourseManagementClient courseManagementClient;

    @InjectMocks
    private OccupationServiceImpl occupationService;

    @Test void getOccupations_shouldReturnPage_whenEmpty() {
        when(occupationDataRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        var result = occupationService.getOccupations(0, 10, null);
        assertNotNull(result);
    }

    @Test void getOccupations_shouldReturnPage_whenKeywordSearch() {
        when(occupationDataRepository.findByTitleContainingIgnoreCase(any(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.emptyList()));
        var result = occupationService.getOccupations(0, 10, "Engineer");
        assertNotNull(result);
    }

    @Test void getAllOccupations_shouldReturnList_whenEmpty() {
        when(occupationDataRepository.findAll()).thenReturn(Collections.emptyList());
        var result = occupationService.getAllOccupations();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void getAllOccupations_shouldReturnList_whenDataExists() {
        OccupationData data = new OccupationData();
        data.setOnetsocCode("15-1252.00");
        data.setTitle("Software Engineer");
        when(occupationDataRepository.findAll()).thenReturn(List.of(data));

        OccupationResponse mockResp = new OccupationResponse();
        mockResp.setOnetsocCode("15-1252.00");
        mockResp.setTitle("Software Engineer");
        when(occupationMapper.toResponse(any())).thenReturn(mockResp);

        var result = occupationService.getAllOccupations();
        assertNotNull(result);
        assertTrue(result.size() == 1);
    }

    @Test void getOccupationDetail_shouldReturnDetail_whenExists() {
        OccupationData data = new OccupationData();
        data.setOnetsocCode("15-1252.00");
        data.setTitle("Software Engineer");
        when(occupationDataRepository.findByOnetsocCode("15-1252.00")).thenReturn(Optional.of(data));
        when(occupationSummaryRepository.countTasks("15-1252.00")).thenReturn(10L);
        when(occupationSummaryRepository.countDwa("15-1252.00")).thenReturn(5L);

        com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse mockDetail =
            new com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse();
        mockDetail.setOnetsocCode("15-1252.00");
        when(occupationMapper.toDetailResponse(any())).thenReturn(mockDetail);

        var result = occupationService.getOccupationDetail("15-1252.00");
        assertNotNull(result);
    }

    @Test void getOccupationSubjects_shouldReturnEmpty_whenNoValuations() {
        OccupationData data = new OccupationData();
        data.setOnetsocCode("15-1252.00");
        when(occupationDataRepository.findById(any())).thenReturn(Optional.of(data));
        when(subjectOccupationValuationRepository.findByTargetOccupationCodeOrderByTotalValueDesc(any()))
            .thenReturn(Collections.emptyList());
        when(courseManagementClient.getAllSubjectIds()).thenReturn(Collections.emptyList());

        var result = occupationService.getOccupationSubjects(UUID.randomUUID());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test void getOccupationSubjects_shouldReturnList_whenValuationsExist() {
        OccupationData data = new OccupationData();
        data.setOnetsocCode("15-1252.00");
        when(occupationDataRepository.findById(any())).thenReturn(Optional.of(data));

        SubjectOccupationValuation valuation = new SubjectOccupationValuation();
        valuation.setSubjectId(UUID.randomUUID());
        valuation.setSubjectCode("CS101");
        valuation.setSubjectName("Intro to CS");
        when(subjectOccupationValuationRepository.findByTargetOccupationCodeOrderByTotalValueDesc(any()))
            .thenReturn(List.of(valuation));

        var result = occupationService.getOccupationSubjects(UUID.randomUUID());
        assertNotNull(result);
        assertTrue(result.size() == 1);
    }

    @Test void getOccupationSubjects_shouldTriggerValuation_whenNoValuationsAndSubjectsExist() {
        OccupationData data = new OccupationData();
        data.setOnetsocCode("15-1252.00");
        when(occupationDataRepository.findById(any())).thenReturn(Optional.of(data));

        when(subjectOccupationValuationRepository.findByTargetOccupationCodeOrderByTotalValueDesc(any()))
            .thenReturn(Collections.emptyList())
            .thenReturn(Collections.emptyList());
        when(courseManagementClient.getAllSubjectIds())
            .thenReturn(List.of(UUID.randomUUID(), UUID.randomUUID()));

        java.util.concurrent.CompletableFuture<com.hcmut.lms.personalization.application.dto.response.ValuationSubjectPersistenceResponse> future =
            java.util.concurrent.CompletableFuture.completedFuture(null);
        when(valuationOccupationService.valuateAsync(any())).thenReturn(future);

        var result = occupationService.getOccupationSubjects(UUID.randomUUID());
        assertNotNull(result);
    }

    @Test void getOccupationSubjects_shouldCatchValuationException() {
        OccupationData data = new OccupationData();
        data.setOnetsocCode("15-1252.00");
        when(occupationDataRepository.findById(any())).thenReturn(Optional.of(data));

        when(subjectOccupationValuationRepository.findByTargetOccupationCodeOrderByTotalValueDesc(any()))
            .thenReturn(Collections.emptyList());
        when(courseManagementClient.getAllSubjectIds())
            .thenReturn(List.of(UUID.randomUUID()));

        java.util.concurrent.CompletableFuture<com.hcmut.lms.personalization.application.dto.response.ValuationSubjectPersistenceResponse> future = new java.util.concurrent.CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Valuation failed"));
        when(valuationOccupationService.valuateAsync(any())).thenReturn(future);

        var result = occupationService.getOccupationSubjects(UUID.randomUUID());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
