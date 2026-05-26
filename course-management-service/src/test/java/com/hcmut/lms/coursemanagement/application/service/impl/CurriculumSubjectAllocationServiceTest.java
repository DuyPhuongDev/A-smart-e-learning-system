package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSection;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurriculumSubjectAllocationServiceTest {

    @Mock
    private StudentProgressGradeUtil gradeUtil;

    @InjectMocks
    private CurriculumSubjectAllocationService allocationService;

    private UUID section1Id;
    private UUID section2Id;
    private UUID subject1Id;
    private UUID subject2Id;
    private UUID class1Id;
    private UUID class2Id;

    @BeforeEach
    void setUp() {
        section1Id = UUID.randomUUID();
        section2Id = UUID.randomUUID();
        subject1Id = UUID.randomUUID();
        subject2Id = UUID.randomUUID();
        class1Id = UUID.randomUUID();
        class2Id = UUID.randomUUID();
    }

    @Test
    void allocateSubjectsToSections_shouldAllocateSubjects_whenPassed() {
        CurriculumSection section = buildSection(section1Id, "Core", 6);
        CurriculumSubject cs = buildCurriculumSubject(subject1Id, 3);
        section.setCurriculumSubjects(List.of(cs));

        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setClassId(class1Id);
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(subject1Id, enrollment),
                        Map.of(subject1Id, SubjectGradingType.GRADED));

        assertNotNull(result);
        assertEquals(3, result.effectiveCompletedCreditsBySection().get(section1Id));
        assertTrue(result.countedSubjectIds().contains(subject1Id));
    }

    @Test
    void allocateSubjectsToSections_shouldSkipFailedSubjects() {
        CurriculumSection section = buildSection(section1Id, "Core", 6);
        CurriculumSubject cs = buildCurriculumSubject(subject1Id, 3);
        section.setCurriculumSubjects(List.of(cs));

        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setClassId(class1Id);
        enrollment.setFinalGrade(3.0);
        enrollment.setIsPassed(false);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(false);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(subject1Id, enrollment),
                        Map.of(subject1Id, SubjectGradingType.GRADED));

        assertEquals(0, result.effectiveCompletedCreditsBySection().get(section1Id));
        assertFalse(result.countedSubjectIds().contains(subject1Id));
    }

    @Test
    void allocateSubjectsToSections_shouldMoveToExcess_whenRequiredCreditsZero() {
        CurriculumSection section = buildSection(section1Id, "Core", 0);
        CurriculumSubject cs = buildCurriculumSubject(subject1Id, 3);
        section.setCurriculumSubjects(List.of(cs));

        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setClassId(class1Id);
        enrollment.setFinalGrade(8.0);
        enrollment.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(subject1Id, enrollment),
                        Map.of(subject1Id, SubjectGradingType.GRADED));

        // Subject was moved to excess pool
        assertFalse(result.countedSubjectIds().contains(subject1Id));
        assertEquals(0, result.effectiveCompletedCreditsBySection().get(section1Id));
    }

    @Test
    void allocateSubjectsToSections_shouldSortByGrade_whenMultipleSubjects() {
        CurriculumSection section = buildSection(section1Id, "Core", 3); // Only 3 credits needed

        Subject sub1 = new Subject();
        sub1.setId(subject1Id);
        sub1.setCredits(3);
        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);

        Subject sub2 = new Subject();
        sub2.setId(subject2Id);
        sub2.setCredits(3);
        CurriculumSubject cs2 = new CurriculumSubject();
        cs2.setSubject(sub2);

        section.setCurriculumSubjects(List.of(cs1, cs2));

        StudentEnrollmentResponse enrollment1 = new StudentEnrollmentResponse();
        enrollment1.setClassId(class1Id);
        enrollment1.setFinalGrade(9.0);
        enrollment1.setIsPassed(true);

        StudentEnrollmentResponse enrollment2 = new StudentEnrollmentResponse();
        enrollment2.setClassId(class2Id);
        enrollment2.setFinalGrade(6.0);
        enrollment2.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        // With only 3 credits available, only the highest-grade subject fits
        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(subject1Id, enrollment1, subject2Id, enrollment2),
                        Map.of(subject1Id, SubjectGradingType.GRADED, subject2Id, SubjectGradingType.GRADED));

        assertEquals(3, result.effectiveCompletedCreditsBySection().get(section1Id));
        assertTrue(result.countedSubjectIds().contains(subject1Id)); // Higher grade counted
    }

    @Test
    void allocateSubjectsToSections_shouldCountZeroCreditSubjectImmediately() {
        CurriculumSection section = buildSection(section1Id, "Core", 6);
        Subject sub = new Subject();
        sub.setId(subject1Id);
        sub.setCredits(0);
        CurriculumSubject cs = new CurriculumSubject();
        cs.setSubject(sub);
        section.setCurriculumSubjects(List.of(cs));

        StudentEnrollmentResponse enrollment = new StudentEnrollmentResponse();
        enrollment.setClassId(class1Id);
        enrollment.setFinalGrade(null);
        enrollment.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(subject1Id, enrollment),
                        Map.of(subject1Id, SubjectGradingType.GRADED));

        assertTrue(result.countedSubjectIds().contains(subject1Id));
    }

    @Test
    void allocateSubjectsToSections_shouldAllocateFreeElective_whenSectionExists() {
        CurriculumSection coreSection = buildSection(section1Id, "Core", 6);
        CurriculumSection freeElectiveSection = buildSection(section2Id,
                "Tự chọn tự do", 6); // "Tự chọn tự do"

        Subject sub1 = new Subject();
        sub1.setId(subject1Id);
        sub1.setCredits(3);
        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);
        coreSection.setCurriculumSubjects(List.of(cs1));

        Subject sub2 = new Subject();
        sub2.setId(subject2Id);
        sub2.setCredits(3);
        CurriculumSubject cs2 = new CurriculumSubject();
        cs2.setSubject(sub2);
        freeElectiveSection.setCurriculumSubjects(List.of(cs2));

        StudentEnrollmentResponse enrollment1 = new StudentEnrollmentResponse();
        enrollment1.setClassId(class1Id);
        enrollment1.setFinalGrade(8.0);
        enrollment1.setIsPassed(true);

        StudentEnrollmentResponse enrollment2 = new StudentEnrollmentResponse();
        enrollment2.setClassId(class2Id);
        enrollment2.setFinalGrade(7.0);
        enrollment2.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(coreSection, freeElectiveSection),
                        Map.of(subject1Id, enrollment1, subject2Id, enrollment2),
                        Map.of(subject1Id, SubjectGradingType.GRADED, subject2Id, SubjectGradingType.GRADED));

        assertNotNull(result);
    }

    @Test
    void allocateSubjectsToSections_shouldHandleEnrollmentNotInMap() {
        CurriculumSection section = buildSection(section1Id, "Core", 6);
        CurriculumSubject cs = buildCurriculumSubject(subject1Id, 3);
        section.setCurriculumSubjects(List.of(cs));

        // No enrollment for this subject
        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(), // empty map
                        Map.of());

        assertEquals(0, result.effectiveCompletedCreditsBySection().get(section1Id));
        assertFalse(result.countedSubjectIds().contains(subject1Id));
    }

    @Test
    void normalizeSectionName_shouldNormalizeVietnamese() {
        String result = CurriculumSubjectAllocationService.normalizeSectionName("Tự chọn tự do");
        assertEquals("tu chon tu do", result);
    }

    @Test
    void normalizeSectionName_shouldHandleNull() {
        assertEquals("", CurriculumSubjectAllocationService.normalizeSectionName(null));
    }

    @Test
    void allocateSubjectsToSections_shouldBreakFreeElective_whenRequiredCreditsZero() {
        CurriculumSection coreSection = buildSection(section1Id, "Core", 6);
        CurriculumSection freeElectiveSection = buildSection(section2Id, "Tự chọn tự do", 0);

        Subject sub1 = new Subject();
        sub1.setId(subject1Id);
        sub1.setCredits(3);
        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);
        coreSection.setCurriculumSubjects(List.of(cs1));

        Subject sub2 = new Subject();
        sub2.setId(subject2Id);
        sub2.setCredits(3);
        CurriculumSubject cs2 = new CurriculumSubject();
        cs2.setSubject(sub2);
        freeElectiveSection.setCurriculumSubjects(List.of(cs2));

        StudentEnrollmentResponse enrollment1 = new StudentEnrollmentResponse();
        enrollment1.setClassId(class1Id);
        enrollment1.setFinalGrade(8.0);
        enrollment1.setIsPassed(true);

        StudentEnrollmentResponse enrollment2 = new StudentEnrollmentResponse();
        enrollment2.setClassId(class2Id);
        enrollment2.setFinalGrade(7.0);
        enrollment2.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(coreSection, freeElectiveSection),
                        Map.of(subject1Id, enrollment1, subject2Id, enrollment2),
                        Map.of(subject1Id, SubjectGradingType.GRADED, subject2Id, SubjectGradingType.GRADED));

        assertEquals(3, result.effectiveCompletedCreditsBySection().get(section1Id));
        assertEquals(0, result.effectiveCompletedCreditsBySection().get(section2Id));
        assertTrue(result.freeElectiveAcceptedExcessSubjectIds().isEmpty());
    }

    @Test
    void allocateSubjectsToSections_shouldAcceptExcessIntoFreeElective() {
        CurriculumSection coreSection = buildSection(section1Id, "Core", 3);
        CurriculumSection freeElectiveSection = buildSection(section2Id, "Tự chọn tự do", 6);

        Subject sub1 = new Subject();
        sub1.setId(subject1Id);
        sub1.setCredits(6);
        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);
        coreSection.setCurriculumSubjects(List.of(cs1));

        // Free elective has NO subjects of its own — empty list
        freeElectiveSection.setCurriculumSubjects(List.of());

        StudentEnrollmentResponse enrollment1 = new StudentEnrollmentResponse();
        enrollment1.setClassId(class1Id);
        enrollment1.setFinalGrade(8.0);
        enrollment1.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(coreSection, freeElectiveSection),
                        Map.of(subject1Id, enrollment1),
                        Map.of(subject1Id, SubjectGradingType.GRADED));

        // Core only needs 3 credits, subject has 6 (doesn't fit) → goes to excess → free elective picks it up
        assertEquals(0, result.effectiveCompletedCreditsBySection().get(section1Id));
        assertEquals(6, result.effectiveCompletedCreditsBySection().get(section2Id));
        assertTrue(result.countedSubjectIds().contains(subject1Id));
        assertTrue(result.freeElectiveAcceptedExcessSubjectIds().contains(subject1Id));
    }

    @Test
    void allocateSubjectsToSections_shouldTiebreakByGrade10() {
        CurriculumSection section = buildSection(section1Id, "Core", 3);

        Subject sub1 = new Subject();
        sub1.setId(subject1Id);
        sub1.setCredits(3);
        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);

        Subject sub2 = new Subject();
        sub2.setId(subject2Id);
        sub2.setCredits(3);
        CurriculumSubject cs2 = new CurriculumSubject();
        cs2.setSubject(sub2);

        section.setCurriculumSubjects(List.of(cs1, cs2));

        // Both grade10 values map to the same grade4 (4.0), so tiebreak uses grade10
        StudentEnrollmentResponse enrollment1 = new StudentEnrollmentResponse();
        enrollment1.setClassId(class1Id);
        enrollment1.setFinalGrade(9.0);
        enrollment1.setIsPassed(true);

        StudentEnrollmentResponse enrollment2 = new StudentEnrollmentResponse();
        enrollment2.setClassId(class2Id);
        enrollment2.setFinalGrade(8.5);
        enrollment2.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(subject1Id, enrollment1, subject2Id, enrollment2),
                        Map.of(subject1Id, SubjectGradingType.GRADED, subject2Id, SubjectGradingType.GRADED));

        assertEquals(3, result.effectiveCompletedCreditsBySection().get(section1Id));
        // subject1 has higher grade10 (9.0 > 8.5), should be counted
        assertTrue(result.countedSubjectIds().contains(subject1Id));
    }

    @Test
    void allocateSubjectsToSections_shouldHandleBothGradesNull() {
        CurriculumSection section = buildSection(section1Id, "Core", 6);

        Subject sub1 = new Subject();
        sub1.setId(subject1Id);
        sub1.setCredits(3);
        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);

        Subject sub2 = new Subject();
        sub2.setId(subject2Id);
        sub2.setCredits(3);
        CurriculumSubject cs2 = new CurriculumSubject();
        cs2.setSubject(sub2);

        section.setCurriculumSubjects(List.of(cs1, cs2));

        StudentEnrollmentResponse enrollment1 = new StudentEnrollmentResponse();
        enrollment1.setClassId(class1Id);
        enrollment1.setFinalGrade(null);
        enrollment1.setIsPassed(true);

        StudentEnrollmentResponse enrollment2 = new StudentEnrollmentResponse();
        enrollment2.setClassId(class2Id);
        enrollment2.setFinalGrade(null);
        enrollment2.setIsPassed(true);

        when(gradeUtil.isStudentPassedSubject(any(), any())).thenReturn(true);

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                allocationService.allocateSubjectsToSections(
                        List.of(section),
                        Map.of(subject1Id, enrollment1, subject2Id, enrollment2),
                        Map.of(subject1Id, SubjectGradingType.GRADED, subject2Id, SubjectGradingType.GRADED));

        assertNotNull(result);
        // Both should be counted since section needs 6 and both are 3 each
        assertTrue(result.countedSubjectIds().contains(subject1Id));
        assertTrue(result.countedSubjectIds().contains(subject2Id));
    }

    @Test
    void createSectionAllocationResult_shouldCreateRecord() {
        Set<UUID> countedIds = Set.of(UUID.randomUUID());
        Map<UUID, Integer> credits = Map.of(UUID.randomUUID(), 5);
        Set<UUID> freeElectiveIds = Set.of();

        CurriculumSubjectAllocationService.SectionAllocationResult result =
                new CurriculumSubjectAllocationService.SectionAllocationResult(countedIds, credits, freeElectiveIds);

        assertEquals(countedIds, result.countedSubjectIds());
        assertEquals(credits, result.effectiveCompletedCreditsBySection());
        assertEquals(freeElectiveIds, result.freeElectiveAcceptedExcessSubjectIds());
    }

    // -- helpers --

    private CurriculumSection buildSection(UUID id, String name, Integer requiredCredits) {
        CurriculumSection section = new CurriculumSection();
        section.setId(id);
        section.setName(name);
        section.setRequiredCredits(requiredCredits);
        section.setDisplayOrder(1);
        return section;
    }

    private CurriculumSubject buildCurriculumSubject(UUID subjectId, int credits) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCredits(credits);

        CurriculumSubject cs = new CurriculumSubject();
        cs.setSubject(subject);
        return cs;
    }
}
