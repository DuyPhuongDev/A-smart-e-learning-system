package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service to extract and transform student progress data from course-management service
 * Provides GPA, credits, and subject completion information for goal validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StudentProgressDataService {

  private final CourseManagementClient courseManagementClient;

  /**
   * Fetches and transforms student progress data
   *
   * @param studentId The student's ID
   * @return StudentProgressData containing GPA, credits, subject lists, and completed subject details
   */
  public StudentProgressData getStudentProgressData(UUID studentId) {
    log.info("Fetching student progress data for studentId={}", studentId);

    try {
      StudentLearningProgressResponse progressResponse = courseManagementClient.getStudentProgress(studentId);

      if (progressResponse == null) {
        log.warn("Null progress response for studentId={}, using defaults", studentId);
        return new StudentProgressData(
            new BigDecimal("2.50"),
            new BigDecimal("2.00"),
            0,
            140,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Map.of()
        );
      }

      StudentLearningProgressResponse.StudentLearningSummary summary = progressResponse.getSummary();
      if (summary == null) {
        log.warn("Null summary in progress response for studentId={}, using defaults", studentId);
        return new StudentProgressData(
            new BigDecimal("2.50"),
            new BigDecimal("2.00"),
            0,
            140,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            Map.of()
        );
      }

      // Extract GPA and credits
      BigDecimal currentGpa10 = summary.getCumulativeGpa10() != null
          ? BigDecimal.valueOf(summary.getCumulativeGpa10())
          : BigDecimal.ZERO;
      BigDecimal currentGpa4 = summary.getCumulativeGpa4() != null
          ? BigDecimal.valueOf(summary.getCumulativeGpa4())
          : BigDecimal.ZERO;
      int earnedCredits = summary.getEarnedCredits() != null ? summary.getEarnedCredits() : 0;
      int requiredCredits = summary.getRequiredCredits() != null ? summary.getRequiredCredits() : 140;
      int remainingCredits = requiredCredits - earnedCredits;

      // Extract completed and remaining subject IDs, plus completed subject details
      List<UUID> completedSubjectIds = new ArrayList<>();
      List<UUID> remainingSubjectIds = new ArrayList<>();
      List<CompletedSubjectDetail> completedSubjects = new ArrayList<>();
      Map<UUID, Integer> remainingSubjectCredits = new HashMap<>();

      if (progressResponse.getSections() != null) {
        for (StudentLearningProgressResponse.StudentProgressSectionItem section : progressResponse.getSections()) {
          if (section.getSubjects() != null) {
            for (StudentLearningProgressResponse.StudentProgressSubjectItem subject : section.getSubjects()) {
              if (subject.getSubjectId() == null || subject.getSubjectId().isBlank()) {
                continue;
              }

              UUID subjectId;
              try {
                subjectId = UUID.fromString(subject.getSubjectId());
              } catch (IllegalArgumentException ex) {
                continue;
              }

              if (Boolean.TRUE.equals(subject.getIsPassed())) {
                completedSubjectIds.add(subjectId);
                // Preserve completed subject with study order and grades (using pass/fail logic from StudentProgressServiceImpl)
                completedSubjects.add(CompletedSubjectDetail.builder()
                    .subjectId(subjectId)
                    .semesterId(parseUuid(subject.getSemesterId()))
                    .academicYearId(parseUuid(subject.getAcademicYearId()))
                    .semesterOrder(subject.getSemesterOrder())
                    .academicYearOrder(subject.getAcademicYearOrder())
                    .subjectCode(subject.getSubjectCode())
                    .subjectName(subject.getSubjectName())
                    .credits(subject.getCredits())
                    .studyOrder(subject.getOrder() != null ? subject.getOrder() : Integer.MAX_VALUE)
                    .grade10(subject.getGrade10())
                    .letterGrade(subject.getLetterGrade())
                    .grade4(subject.getGrade4())
                    .build());
              } else {
                remainingSubjectIds.add(subjectId);
                remainingSubjectCredits.put(subjectId,
                    subject.getCredits() != null ? subject.getCredits() : 3);
              }
            }
          }
        }
      }

      log.info(
          "Student progress data fetched: GPA10={}, GPA4={}, earnedCredits={}, remainingCredits={}, completedSubjects={}, remainingSubjects={}",
          currentGpa10, currentGpa4, earnedCredits, remainingCredits, completedSubjectIds.size(),
          remainingSubjectIds.size());

      return new StudentProgressData(
          currentGpa10,
          currentGpa4,
          earnedCredits,
          remainingCredits,
          completedSubjectIds,
          remainingSubjectIds,
          completedSubjects,
          remainingSubjectCredits
      );

    } catch (Exception e) {
      log.error("Failed to fetch student progress data for studentId={}: {}", studentId, e.getMessage());
      // Return conservative defaults on failure
      return new StudentProgressData(
          new BigDecimal("2.50"),
          new BigDecimal("2.00"),
          60,
          80,
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>(),
          Map.of()
      );
    }
  }

  /**
   * Data class to hold student progress information including completion details
   */
  public record StudentProgressData(
      BigDecimal currentGpa10,
      BigDecimal currentGpa4,
      int earnedCredits,
      int remainingCredits,
      List<UUID> completedSubjectIds,
      List<UUID> remainingSubjectIds,
      List<CompletedSubjectDetail> completedSubjects,
      Map<UUID, Integer> remainingSubjectCredits
  ) {}

  /**
   * Metadata for completed subjects including study order and grades.
   * Uses pass determination formula from StudentProgressServiceImpl.isStudentPassedSubject():
   * - GRADED: grade >= 4.0
   * - PASS_FAIL: use isPassed flag
   * - BOTH: isPassed=true OR grade >= 5.0
   */
  @Builder
  public record CompletedSubjectDetail(
      UUID subjectId,
      UUID semesterId,
      UUID academicYearId,
      Integer semesterOrder,
      Integer academicYearOrder,
      String subjectCode,
      String subjectName,
      Integer credits,
      Integer studyOrder,
      Double grade10,
      String letterGrade,
      Double grade4
  ) {}

  private UUID parseUuid(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
