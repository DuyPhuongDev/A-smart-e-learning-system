package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.exception.ServiceUnavailableException;
import com.hcmut.lms.personalization.exception.StudentDataUnavailableException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
   * @param specializationId The target specialization ID; if null, falls back to the student's default
   * @return StudentProgressData containing GPA, credits, subject lists, and completed subject details
   */
  public StudentProgressData getStudentProgressData(UUID studentId, UUID specializationId) {
    log.info("Fetching student progress data for studentId={}, specializationId={}", studentId, specializationId);

    StudentLearningProgressResponse progressResponse;
    try {
      progressResponse = courseManagementClient.getStudentProgress(studentId, specializationId);
    } catch (Exception e) {
      log.error("Failed to fetch student progress data for studentId={}: {}", studentId, e.getMessage());
      throw new ServiceUnavailableException("course-management",
          "Cannot validate learning goal: student progress data is currently unavailable. Please try again later.");
    }

    return transformProgressResponse(progressResponse, studentId);
  }

  public StudentProgressData getStudentProgressData(UUID studentId, UUID specializationId,
      StudentLearningProgressResponse preFetchedProgress) {
    if (preFetchedProgress != null) {
      log.info("Using pre-fetched student progress data for studentId={}", studentId);
      return transformProgressResponse(preFetchedProgress, studentId);
    }
    return getStudentProgressData(studentId, specializationId);
  }

  private StudentProgressData transformProgressResponse(StudentLearningProgressResponse progressResponse, UUID studentId) {
    if (progressResponse == null) {
      throw new StudentDataUnavailableException("summary",
          "Cannot validate learning goal: no progress data found for student " + studentId);
    }

    StudentLearningProgressResponse.StudentLearningSummary summary = progressResponse.getSummary();
    if (summary == null) {
      throw new StudentDataUnavailableException("summary",
          "Cannot validate learning goal: progress summary is missing for student " + studentId);
    }

    if (summary.getEarnedCredits() == null) {
      throw new StudentDataUnavailableException("earnedCredits",
          "Cannot validate learning goal: earned credits is missing for student " + studentId);
    }
    if (summary.getRequiredCredits() == null) {
      throw new StudentDataUnavailableException("requiredCredits",
          "Cannot validate learning goal: required credits is missing for student " + studentId);
    }

    BigDecimal currentGpa4 = summary.getCumulativeGpa4() != null
        ? BigDecimal.valueOf(summary.getCumulativeGpa4())
        : null;

    int earnedCredits = summary.getEarnedCredits();
    int remainingCredits = summary.getRemainingCredits() != null
        ? summary.getRemainingCredits()
        : (summary.getRequiredCredits() - earnedCredits);

    List<UUID> completedSubjectIds = new ArrayList<>();
    List<UUID> remainingSubjectIds = new ArrayList<>();
    List<CompletedSubjectDetail> completedSubjects = new ArrayList<>();
    Map<UUID, Integer> remainingSubjectCredits = new HashMap<>();

    if (progressResponse.getSections() != null) {
      // First pass: group all subject items by subjectId to determine per-subject pass status
      Map<UUID, List<StudentLearningProgressResponse.StudentProgressSubjectItem>> itemsBySubjectId = new LinkedHashMap<>();
      for (StudentLearningProgressResponse.StudentProgressSectionItem section : progressResponse.getSections()) {
        if (section.getSubjects() != null) {
          for (StudentLearningProgressResponse.StudentProgressSubjectItem subject : section.getSubjects()) {
            if (subject.getSubjectId() == null || subject.getSubjectId().isBlank()) {
              continue;
            }
            try {
              UUID subjectId = UUID.fromString(subject.getSubjectId());
              itemsBySubjectId.computeIfAbsent(subjectId, k -> new ArrayList<>()).add(subject);
            } catch (IllegalArgumentException ex) {
              continue;
            }
          }
        }
      }

      // Second pass: classify subjects and build detail lists
      for (Map.Entry<UUID, List<StudentLearningProgressResponse.StudentProgressSubjectItem>> entry : itemsBySubjectId.entrySet()) {
        UUID subjectId = entry.getKey();
        List<StudentLearningProgressResponse.StudentProgressSubjectItem> attempts = entry.getValue();

        // A subject is "completed" if any attempt passed
        boolean anyPassed = attempts.stream().anyMatch(a -> Boolean.TRUE.equals(a.getIsPassed()));

        if (anyPassed) {
          completedSubjectIds.add(subjectId);
        }

        // Include ALL attempts (passed and failed) to preserve full history
        for (StudentLearningProgressResponse.StudentProgressSubjectItem attempt : attempts) {
          // Skip attempts missing semester metadata (needed for timeline grouping)
          if (attempt.getSemesterId() == null || attempt.getAcademicYearId() == null) continue;
          completedSubjects.add(CompletedSubjectDetail.builder()
              .subjectId(subjectId)
              .semesterId(parseUuid(attempt.getSemesterId()))
              .academicYearId(parseUuid(attempt.getAcademicYearId()))
              .semesterOrder(attempt.getSemesterOrder())
              .academicYearOrder(attempt.getAcademicYearOrder())
              .subjectCode(attempt.getSubjectCode())
              .subjectName(attempt.getSubjectName())
              .credits(attempt.getCredits())
              .studyOrder(attempt.getOrder() != null ? attempt.getOrder() : Integer.MAX_VALUE)
              .grade10(attempt.getGrade10())
              .letterGrade(attempt.getLetterGrade())
              .grade4(attempt.getGrade4())
              .attemptNo(attempt.getAttemptNo())
              .isHighestResult(attempt.getIsHighestResult())
              .isPassed(attempt.getIsPassed())
              .build());
        }

        if (!anyPassed) {
          remainingSubjectIds.add(subjectId);
          Integer subjectCredits = attempts.get(0).getCredits();
          if (subjectCredits == null) {
            throw new StudentDataUnavailableException("credits",
                "Cannot validate learning goal: credits is missing for subject " + subjectId);
          }
          remainingSubjectCredits.put(subjectId, subjectCredits);
        }
      }
    }

    log.info(
        "Student progress data fetched: GPA4={}, earnedCredits={}, remainingCredits={}, completedSubjects={}, remainingSubjects={}",
        currentGpa4, earnedCredits, remainingCredits, completedSubjectIds.size(),
        remainingSubjectIds.size());

    // Extract student intake year for computing the full past semester timeline.
    // Use studentIntakeYear (the year the student enrolled) instead of curriculumYear
    // (the year the curriculum was established), since multiple cohorts study under
    // the same curriculum and the student's actual start year may differ.
    Integer studentIntakeYear = progressResponse.getProgramInfo() != null
        ? progressResponse.getProgramInfo().getStudentIntakeYear()
        : null;
    // Fallback to curriculumYear if studentIntakeYear is not available
    if (studentIntakeYear == null && progressResponse.getProgramInfo() != null) {
      studentIntakeYear = progressResponse.getProgramInfo().getCurriculumYear();
    }

    return new StudentProgressData(
        currentGpa4,
        earnedCredits,
        remainingCredits,
        completedSubjectIds,
        remainingSubjectIds,
        completedSubjects,
        remainingSubjectCredits,
        studentIntakeYear
    );
  }

  /**
   * Data class to hold student progress information including completion details
   */
  public record StudentProgressData(
      BigDecimal currentGpa4,
      int earnedCredits,
      int remainingCredits,
      List<UUID> completedSubjectIds,
      List<UUID> remainingSubjectIds,
      List<CompletedSubjectDetail> completedSubjects,
      Map<UUID, Integer> remainingSubjectCredits,
      Integer studentIntakeYear
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
      Double grade4,
      Integer attemptNo,
      Boolean isHighestResult,
      Boolean isPassed
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
