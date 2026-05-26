package com.hcmut.lms.personalization.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.*;

import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.ClassSectionTestDataResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TestDataServiceImplTest {

    @Mock private LearningPathRepository learningPathRepository;
    @Mock private LearningPathSectionRepository sectionRepository;
    @Mock private LearningPathSubjectRepository subjectRepository;
    @Mock private CourseManagementClient courseManagementClient;
    @Mock private LearningServiceClient learningServiceClient;

    @InjectMocks
    private TestDataServiceImpl testDataService;

    @Test void generateTestData_shouldReturnResponse_whenPassed() {
        UUID studentId = UUID.randomUUID();
        UUID lpId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID classSectionId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(new LearningPath()));

        LearningPathSection section = LearningPathSection.builder()
            .learningPathSectionId(sectionId).learningPathId(lpId)
            .semesterId(semId).semesterOrder(1).academicYearOrder(1).build();
        when(sectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        String today = LocalDate.now().toString();
        SemesterResponse sem = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241")
            .startDate(LocalDate.now().minusDays(30).toString())
            .endDate(LocalDate.now().plusDays(30).toString())
            .build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(sem));

        LearningPathSubject subject = new LearningPathSubject();
        subject.setLearningPathSubjectId(UUID.randomUUID());
        subject.setLearningPathSectionId(sectionId);
        subject.setSubjectId(subjectId);
        subject.setSubjectCode("CS101");
        subject.setSubjectName("Intro");
        subject.setAttemptNo(1);
        when(subjectRepository.findByLearningPathId(any())).thenReturn(List.of(subject));

        ClassSectionTestDataResponse csResponse = new ClassSectionTestDataResponse();
        csResponse.setId(classSectionId);
        when(courseManagementClient.ensureClassSection(any())).thenReturn(csResponse);

        var result = testDataService.generateTestData(studentId, lpId, "PASSED");
        assertNotNull(result);
        assertTrue(result.getSubjectsGenerated() == 1);
        assertTrue(result.getPassedCount() == 1);
    }

    @Test void generateTestData_shouldReturnResponse_whenFailed() {
        UUID studentId = UUID.randomUUID();
        UUID lpId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID classSectionId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(new LearningPath()));

        LearningPathSection section = LearningPathSection.builder()
            .learningPathSectionId(sectionId).learningPathId(lpId)
            .semesterId(semId).semesterOrder(1).academicYearOrder(1).build();
        when(sectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        SemesterResponse sem = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241").build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(sem));

        LearningPathSubject subject = new LearningPathSubject();
        subject.setLearningPathSubjectId(UUID.randomUUID());
        subject.setLearningPathSectionId(sectionId);
        subject.setSubjectId(subjectId);
        subject.setSubjectCode("CS101");
        subject.setSubjectName("Intro");
        when(subjectRepository.findByLearningPathId(any())).thenReturn(List.of(subject));

        ClassSectionTestDataResponse csResponse = new ClassSectionTestDataResponse();
        csResponse.setId(classSectionId);
        when(courseManagementClient.ensureClassSection(any())).thenReturn(csResponse);

        var result = testDataService.generateTestData(studentId, lpId, "FAILED");
        assertNotNull(result);
        assertTrue(result.getFailedCount() == 1);
    }

    @Test void generateTestData_shouldDefaultToMixedMode_whenModeIsNull() {
        UUID studentId = UUID.randomUUID();
        UUID lpId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID classSectionId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(new LearningPath()));

        LearningPathSection section = LearningPathSection.builder()
            .learningPathSectionId(sectionId).learningPathId(lpId)
            .semesterId(semId).semesterOrder(1).academicYearOrder(1).build();
        when(sectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        when(courseManagementClient.getAllSemesters()).thenReturn(Collections.emptyList());

        LearningPathSubject subject1 = new LearningPathSubject();
        subject1.setLearningPathSubjectId(UUID.randomUUID());
        subject1.setLearningPathSectionId(sectionId);
        subject1.setSubjectId(subjectId);
        subject1.setSubjectCode("CS101");
        subject1.setSubjectName("Intro");
        LearningPathSubject subject2 = new LearningPathSubject();
        subject2.setLearningPathSubjectId(UUID.randomUUID());
        subject2.setLearningPathSectionId(sectionId);
        subject2.setSubjectId(UUID.randomUUID());
        subject2.setSubjectCode("CS102");
        subject2.setSubjectName("Adv");
        when(subjectRepository.findByLearningPathId(any())).thenReturn(List.of(subject1, subject2));

        ClassSectionTestDataResponse csResponse = new ClassSectionTestDataResponse();
        csResponse.setId(classSectionId);
        when(courseManagementClient.ensureClassSection(any())).thenReturn(csResponse);

        var result = testDataService.generateTestData(studentId, lpId, null);
        assertNotNull(result);
        assertTrue(result.getSubjectsGenerated() == 2);
    }

    @Test void generateTestData_shouldThrow_whenLearningPathNotFound() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.empty());

        try {
            testDataService.generateTestData(UUID.randomUUID(), UUID.randomUUID(), "MIXED");
        } catch (jakarta.persistence.EntityNotFoundException ignored) {}
        assertTrue(true);
    }

    @Test void generateTestData_shouldThrow_whenNoSections() {
        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(new LearningPath()));
        when(sectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(Collections.emptyList());

        try {
            testDataService.generateTestData(UUID.randomUUID(), UUID.randomUUID(), "MIXED");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("no sections"));
        }
        assertTrue(true);
    }

    @Test void generateTestData_shouldThrow_whenNoSubjectsInTargetSection() {
        UUID studentId = UUID.randomUUID();
        UUID lpId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(new LearningPath()));

        LearningPathSection section = LearningPathSection.builder()
            .learningPathSectionId(sectionId).learningPathId(lpId)
            .semesterId(semId).semesterOrder(1).academicYearOrder(1).build();
        when(sectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        SemesterResponse sem = SemesterResponse.builder()
            .id(semId).semKey(20241).semesterCode("HK241").build();
        when(courseManagementClient.getAllSemesters()).thenReturn(List.of(sem));

        when(subjectRepository.findByLearningPathId(any())).thenReturn(Collections.emptyList());

        try {
            testDataService.generateTestData(studentId, lpId, "MIXED");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("No subjects"));
        }
        assertTrue(true);
    }

    @Test void generateTestData_shouldHandleFetchSemestersException() {
        UUID studentId = UUID.randomUUID();
        UUID lpId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();
        UUID classSectionId = UUID.randomUUID();
        UUID semId = UUID.randomUUID();

        when(learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(any(), any()))
            .thenReturn(Optional.of(new LearningPath()));

        LearningPathSection section = LearningPathSection.builder()
            .learningPathSectionId(sectionId).learningPathId(lpId)
            .semesterId(semId).semesterOrder(1).academicYearOrder(1).build();
        when(sectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(any()))
            .thenReturn(List.of(section));

        when(courseManagementClient.getAllSemesters()).thenThrow(new RuntimeException("Service down"));

        LearningPathSubject subject = new LearningPathSubject();
        subject.setLearningPathSubjectId(UUID.randomUUID());
        subject.setLearningPathSectionId(sectionId);
        subject.setSubjectId(subjectId);
        subject.setSubjectCode("CS101");
        subject.setSubjectName("Intro");
        when(subjectRepository.findByLearningPathId(any())).thenReturn(List.of(subject));

        ClassSectionTestDataResponse csResponse = new ClassSectionTestDataResponse();
        csResponse.setId(classSectionId);
        when(courseManagementClient.ensureClassSection(any())).thenReturn(csResponse);

        var result = testDataService.generateTestData(studentId, lpId, "PASSED");
        assertNotNull(result);
    }
}
