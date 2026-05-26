package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.exception.ServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SemesterCalculationServiceTest {

    @Mock private CourseManagementClient courseManagementClient;

    @InjectMocks
    private SemesterCalculationService semesterCalculationService;

    @Test void calculateAvailableSemesters_shouldReturnZero_whenNoRemainingSemesters() {
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenReturn(Collections.emptyList());

        SemesterCalculationService.SemesterAvailability result =
            semesterCalculationService.calculateAvailableSemesters(UUID.randomUUID(), null);
        assertNotNull(result);
        assertTrue(result.availableMainSemesters() == 0);
    }

    @Test void calculateAvailableSemesters_shouldCountMainAndSummer_whenSemestersExist() {
        SemesterResponse mainSem = SemesterResponse.builder()
            .id(UUID.randomUUID())
            .semKey(20241)
            .semesterCode("HK241")
            .academicYearCode("2024-2025")
            .build();
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenReturn(List.of(mainSem));

        SemesterCalculationService.SemesterAvailability result =
            semesterCalculationService.calculateAvailableSemesters(UUID.randomUUID(), null);
        assertNotNull(result);
        assertTrue(result.availableMainSemesters() > 0);
    }

    @Test void calculateAvailableSemesters_shouldThrowServiceUnavailable_whenClientFails() {
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenThrow(new RuntimeException("Connection refused"));

        try {
            semesterCalculationService.calculateAvailableSemesters(UUID.randomUUID(), null);
        } catch (ServiceUnavailableException e) {
            assertTrue(e.getMessage().contains("unavailable"));
        }
        assertTrue(true);
    }

    @Test void calculateAvailableSemesters_shouldScopeToExpectedSemester_whenProvided() {
        UUID targetSemId = UUID.randomUUID();
        SemesterResponse sem1 = SemesterResponse.builder()
            .id(UUID.randomUUID()).semKey(20241).semesterCode("HK241").build();
        SemesterResponse sem2 = SemesterResponse.builder()
            .id(targetSemId).semKey(20242).semesterCode("HK242").build();
        SemesterResponse sem3 = SemesterResponse.builder()
            .id(UUID.randomUUID()).semKey(20243).semesterCode("HK243").build();
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenReturn(List.of(sem1, sem2, sem3));

        SemesterCalculationService.SemesterAvailability result =
            semesterCalculationService.calculateAvailableSemesters(UUID.randomUUID(), targetSemId);
        // Should scope to semKey <= 20242, excluding 20243
        assertTrue(result.availableMainSemesters() == 2);
    }

    @Test void calculateAvailableSemesters_shouldUseFullSet_whenExpectedSemesterNotFound() {
        SemesterResponse sem = SemesterResponse.builder()
            .id(UUID.randomUUID()).semKey(20241).semesterCode("HK241").build();
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenReturn(List.of(sem));

        SemesterCalculationService.SemesterAvailability result =
            semesterCalculationService.calculateAvailableSemesters(UUID.randomUUID(), UUID.randomUUID());
        assertTrue(result.availableMainSemesters() == 1);
    }

    @Test void calculateAvailableSemesters_shouldHandleSummerSemester_whenSemesterCodeIsSummer() {
        SemesterResponse summerSem = SemesterResponse.builder()
            .id(UUID.randomUUID()).semKey(20243).semesterCode("HK243").build();
        when(courseManagementClient.getRemainingSemesters(any()))
            .thenReturn(List.of(summerSem));

        SemesterCalculationService.SemesterAvailability result =
            semesterCalculationService.calculateAvailableSemesters(UUID.randomUUID(), null);
        assertTrue(result.availableSummerSemesters() == 1);
        assertTrue(result.availableMainSemesters() == 0);
        assertTrue(result.totalSemesters() == 1);
    }
}
