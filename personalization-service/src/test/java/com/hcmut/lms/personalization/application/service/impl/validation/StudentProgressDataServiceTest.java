package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.*;

import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.exception.ServiceUnavailableException;
import com.hcmut.lms.personalization.exception.StudentDataUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StudentProgressDataServiceTest {

    @Mock private CourseManagementClient courseManagementClient;

    @InjectMocks
    private StudentProgressDataService studentProgressDataService;

    private static StudentLearningProgressResponse.StudentLearningSummary makeSummary(
        int earned, int required, int remaining, double gpa4) {
        StudentLearningProgressResponse.StudentLearningSummary s =
            new StudentLearningProgressResponse.StudentLearningSummary();
        s.setEarnedCredits(earned);
        s.setRequiredCredits(required);
        s.setRemainingCredits(remaining);
        s.setCumulativeGpa4(gpa4);
        return s;
    }

    private static StudentLearningProgressResponse makeProgress(
        StudentLearningProgressResponse.StudentLearningSummary summary,
        List<StudentLearningProgressResponse.StudentProgressSectionItem> sections,
        Integer studentIntakeYear) {
        StudentLearningProgressResponse resp = new StudentLearningProgressResponse();
        resp.setSummary(summary);
        resp.setSections(sections);
        StudentLearningProgressResponse.StudentProgramInfo info =
            new StudentLearningProgressResponse.StudentProgramInfo();
        info.setStudentIntakeYear(studentIntakeYear);
        resp.setProgramInfo(info);
        return resp;
    }

    @Test void getStudentProgressData_shouldReturnData_whenValidResponse() {
        UUID studentId = UUID.randomUUID();
        var progress = makeProgress(makeSummary(90, 120, 30, 3.2), Collections.emptyList(), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        assertNotNull(result);
        assertTrue(result.earnedCredits() == 90);
        assertTrue(result.remainingCredits() == 30);
    }

    @Test void getStudentProgressData_shouldThrowServiceUnavailable_whenClientFails() {
        UUID studentId = UUID.randomUUID();
        when(courseManagementClient.getStudentProgress(any(), any()))
            .thenThrow(new RuntimeException("Connection refused"));

        try {
            studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
            fail("Expected ServiceUnavailableException");
        } catch (ServiceUnavailableException e) {
            assertTrue(e.getMessage().contains("unavailable"));
        }
    }

    @Test void getStudentProgressData_shouldUsePreFetched_whenNotNull() {
        UUID studentId = UUID.randomUUID();
        var progress = makeProgress(makeSummary(50, 100, 50, 2.8), Collections.emptyList(), 2022);
        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID(), progress);
        assertNotNull(result);
        assertTrue(result.earnedCredits() == 50);
    }

    @Test void getStudentProgressData_shouldFallbackToFetch_whenPreFetchedNull() {
        UUID studentId = UUID.randomUUID();
        var progress = makeProgress(makeSummary(60, 100, 40, 3.0), Collections.emptyList(), 2021);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID(), null);
        assertNotNull(result);
        assertTrue(result.earnedCredits() == 60);
    }

    @Test void getStudentProgressData_shouldThrow_whenNullResponse() {
        UUID studentId = UUID.randomUUID();
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(null);

        try {
            studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
            fail("Expected StudentDataUnavailableException");
        } catch (StudentDataUnavailableException e) {
            assertTrue(e.getMessage().contains("no progress data"));
        }
    }

    @Test void getStudentProgressData_shouldThrow_whenNullSummary() {
        UUID studentId = UUID.randomUUID();
        var progress = makeProgress(null, Collections.emptyList(), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        try {
            studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
            fail("Expected StudentDataUnavailableException");
        } catch (StudentDataUnavailableException e) {
            assertTrue(e.getMessage().contains("summary"));
        }
    }

    @Test void getStudentProgressData_shouldThrow_whenNullEarnedCredits() {
        UUID studentId = UUID.randomUUID();
        var summary = new StudentLearningProgressResponse.StudentLearningSummary();
        summary.setRequiredCredits(120);
        summary.setRemainingCredits(30);
        var progress = makeProgress(summary, Collections.emptyList(), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        try {
            studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
            fail("Expected StudentDataUnavailableException");
        } catch (StudentDataUnavailableException e) {
            assertTrue(e.getMessage().contains("earned credits"));
        }
    }

    @Test void getStudentProgressData_shouldThrow_whenNullRequiredCredits() {
        UUID studentId = UUID.randomUUID();
        var summary = new StudentLearningProgressResponse.StudentLearningSummary();
        summary.setEarnedCredits(90);
        summary.setRemainingCredits(30);
        var progress = makeProgress(summary, Collections.emptyList(), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        try {
            studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
            fail("Expected StudentDataUnavailableException");
        } catch (StudentDataUnavailableException e) {
            assertTrue(e.getMessage().contains("required credits"));
        }
    }

    @Test void getStudentProgressData_shouldComputeRemainingCredits_whenNullRemaining() {
        UUID studentId = UUID.randomUUID();
        var summary = new StudentLearningProgressResponse.StudentLearningSummary();
        summary.setEarnedCredits(90);
        summary.setRequiredCredits(120);
        // remainingCredits is null → computed as 120-90=30
        var progress = makeProgress(summary, Collections.emptyList(), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        assertTrue(result.remainingCredits() == 30);
    }

    @Test void getStudentProgressData_shouldClassifyPassedSubjects_whenSectionsWithPassed() {
        UUID studentId = UUID.randomUUID();
        UUID passedId = UUID.randomUUID();
        UUID failedId = UUID.randomUUID();

        var passedSubject = StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId(passedId.toString()).subjectCode("CS101").subjectName("Intro")
            .credits(3).isPassed(true).isHighestResult(true)
            .semesterId(UUID.randomUUID().toString())
            .academicYearId(UUID.randomUUID().toString())
            .semesterOrder(1).academicYearOrder(1).attemptNo(1).build();

        var failedSubject = StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId(failedId.toString()).subjectCode("CS102").subjectName("Data Struct")
            .credits(4).isPassed(false).isHighestResult(true)
            .semesterId(UUID.randomUUID().toString())
            .academicYearId(UUID.randomUUID().toString())
            .semesterOrder(1).academicYearOrder(1).attemptNo(1).build();

        var section = StudentLearningProgressResponse.StudentProgressSectionItem.builder()
            .subjects(List.of(passedSubject, failedSubject)).build();

        var progress = makeProgress(makeSummary(90, 120, 30, 3.0), List.of(section), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        assertTrue(result.completedSubjectIds().contains(passedId));
        assertTrue(result.remainingSubjectIds().contains(failedId));
        assertTrue(result.completedSubjects().size() == 2);
    }

    @Test void getStudentProgressData_shouldSkipSubject_whenBlankSubjectId() {
        UUID studentId = UUID.randomUUID();

        var blankSubject = StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId("  ").subjectCode("XX").credits(1).isPassed(true)
            .semesterId(UUID.randomUUID().toString())
            .academicYearId(UUID.randomUUID().toString()).build();

        var section = StudentLearningProgressResponse.StudentProgressSectionItem.builder()
            .subjects(List.of(blankSubject)).build();

        var progress = makeProgress(makeSummary(10, 20, 10, 3.0), List.of(section), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        assertTrue(result.completedSubjectIds().isEmpty());
    }

    @Test void getStudentProgressData_shouldFallbackToCurriculumYear_whenIntakeYearNull() {
        UUID studentId = UUID.randomUUID();
        var progress = new StudentLearningProgressResponse();
        progress.setSummary(makeSummary(10, 20, 10, 3.0));
        progress.setSections(Collections.emptyList());
        var info = new StudentLearningProgressResponse.StudentProgramInfo();
        info.setCurriculumYear(2020);
        // studentIntakeYear is null
        progress.setProgramInfo(info);

        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        assertTrue(result.studentIntakeYear() == 2020);
    }

    @Test void getStudentProgressData_shouldReturnNullIntakeYear_whenNoProgramInfo() {
        UUID studentId = UUID.randomUUID();
        var progress = makeProgress(makeSummary(10, 20, 10, 3.0), Collections.emptyList(), null);
        progress.setProgramInfo(null);

        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        assertNull(result.studentIntakeYear());
    }

    @Test void getStudentProgressData_shouldReturnNullGpa_whenCumulativeGpa4Null() {
        UUID studentId = UUID.randomUUID();
        var summary = new StudentLearningProgressResponse.StudentLearningSummary();
        summary.setEarnedCredits(10);
        summary.setRequiredCredits(20);
        summary.setRemainingCredits(10);
        // cumulativeGpa4 is null
        var progress = makeProgress(summary, Collections.emptyList(), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        assertNull(result.currentGpa4());
    }

    @Test void getStudentProgressData_shouldThrow_whenRemainingSubjectCreditsNull() {
        UUID studentId = UUID.randomUUID();
        UUID failedId = UUID.randomUUID();

        var failedSubject = StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId(failedId.toString()).subjectCode("CS102").subjectName("DS")
            .credits(null).isPassed(false).isHighestResult(true)
            .semesterId(UUID.randomUUID().toString())
            .academicYearId(UUID.randomUUID().toString()).build();

        var section = StudentLearningProgressResponse.StudentProgressSectionItem.builder()
            .subjects(List.of(failedSubject)).build();

        var progress = makeProgress(makeSummary(10, 20, 10, 3.0), List.of(section), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        try {
            studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
            fail("Expected StudentDataUnavailableException");
        } catch (StudentDataUnavailableException e) {
            assertTrue(e.getMessage().contains("credits"));
        }
    }

    @Test void getStudentProgressData_shouldSkipAttempt_whenMissingSemesterMetadata() {
        UUID studentId = UUID.randomUUID();
        UUID passedId = UUID.randomUUID();

        // Attempt missing semesterId — should be skipped in completedSubjects
        var subjectNoSemester = StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId(passedId.toString()).subjectCode("CS103")
            .credits(3).isPassed(true)
            // semesterId and academicYearId are null
            .build();

        var section = StudentLearningProgressResponse.StudentProgressSectionItem.builder()
            .subjects(List.of(subjectNoSemester)).build();

        var progress = makeProgress(makeSummary(10, 20, 10, 3.0), List.of(section), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        // passed subject is in completedSubjectIds but not in completedSubjects (missing metadata)
        assertTrue(result.completedSubjectIds().contains(passedId));
        assertTrue(result.completedSubjects().isEmpty());
    }

    @Test void getStudentProgressData_shouldHandleAnyPassedInMultipleAttempts() {
        UUID studentId = UUID.randomUUID();
        UUID subjectId = UUID.randomUUID();

        var attempt1 = StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId(subjectId.toString()).subjectCode("CS104")
            .credits(3).isPassed(false).isHighestResult(false).attemptNo(1)
            .semesterId(UUID.randomUUID().toString())
            .academicYearId(UUID.randomUUID().toString()).build();

        var attempt2 = StudentLearningProgressResponse.StudentProgressSubjectItem.builder()
            .subjectId(subjectId.toString()).subjectCode("CS104")
            .credits(3).isPassed(true).isHighestResult(true).attemptNo(2)
            .semesterId(UUID.randomUUID().toString())
            .academicYearId(UUID.randomUUID().toString()).build();

        var section = StudentLearningProgressResponse.StudentProgressSectionItem.builder()
            .subjects(List.of(attempt1, attempt2)).build();

        var progress = makeProgress(makeSummary(10, 20, 10, 3.0), List.of(section), 2023);
        when(courseManagementClient.getStudentProgress(any(), any())).thenReturn(progress);

        var result = studentProgressDataService.getStudentProgressData(studentId, UUID.randomUUID());
        // Any passed → completed
        assertTrue(result.completedSubjectIds().contains(subjectId));
        assertFalse(result.remainingSubjectIds().contains(subjectId));
    }
}
