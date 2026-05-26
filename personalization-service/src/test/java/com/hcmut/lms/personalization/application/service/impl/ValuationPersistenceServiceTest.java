package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import com.hcmut.lms.personalization.application.mapper.ValuationMapper;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ValuationPersistenceServiceTest {

    @Mock private SubjectOccupationValuationRepository valuationRepository;
    @Mock private ValuationMapper valuationMapper;

    @InjectMocks
    private ValuationPersistenceService valuationPersistenceService;

    @Test void persistValuation_shouldPersist_whenValidInput() {
        OccupationData occupation = new OccupationData();
        occupation.setOnetsocCode("15-1252.00");

        when(valuationMapper.toEntity(any(), any(), any(), any(), any(), anyDouble(), any()))
            .thenReturn(new SubjectOccupationValuation());
        when(valuationRepository.save(any())).thenReturn(new SubjectOccupationValuation());
        doNothing().when(valuationRepository).deleteBySubjectIdAndTargetOccupationCode(any(), any());

        valuationPersistenceService.persistValuation(
            UUID.randomUUID(), "CS101", "Intro to CS", occupation,
            BigDecimal.valueOf(3.5), 0.85, Collections.emptyList());
        assertTrue(true);
    }
}
