package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.response.TestDataGenerationResponse;
import com.hcmut.lms.personalization.application.service.TestDataService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.LearningServiceClient;
import com.hcmut.lms.personalization.client.dto.ClassSectionTestDataResponse;
import com.hcmut.lms.personalization.client.dto.CreateTestEnrollmentRequest;
import com.hcmut.lms.personalization.client.dto.EnsureClassSectionRequest;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import com.hcmut.lms.personalization.repository.LearningPathRepository;
import com.hcmut.lms.personalization.repository.LearningPathSectionRepository;
import com.hcmut.lms.personalization.repository.LearningPathSubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestDataServiceImpl implements TestDataService {

  private static final String MODE_PASSED = "PASSED";
  private static final String MODE_FAILED = "FAILED";
  private static final String MODE_MIXED = "MIXED";

  private final LearningPathRepository learningPathRepository;
  private final LearningPathSectionRepository sectionRepository;
  private final LearningPathSubjectRepository subjectRepository;
  private final CourseManagementClient courseManagementClient;
  private final LearningServiceClient learningServiceClient;

  @Override
  @Transactional
  public TestDataGenerationResponse generateTestData(UUID studentId, UUID learningPathId, String mode) {
    String resolvedMode = mode != null ? mode.toUpperCase() : MODE_MIXED;

    learningPathRepository.findByLearningPathIdAndStudentIdAndIsActiveTrue(learningPathId, studentId)
        .orElseThrow(() -> new EntityNotFoundException("Active learning path not found: " + learningPathId));

    List<LearningPathSection> sections = sectionRepository.findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(learningPathId);
    if (sections.isEmpty()) {
      throw new IllegalStateException("Learning path has no sections");
    }

    List<SemesterResponse> allSemesters = fetchAllSemesters();
    UUID currentSemesterId = findCurrentSemesterId(allSemesters);
    if (currentSemesterId == null) {
      currentSemesterId = sections.getFirst().getSemesterId();
      log.info("No current semester found by date, using first section's semester");
    }

    Map<UUID, SemesterResponse> semesterById = allSemesters.stream()
        .collect(Collectors.toMap(SemesterResponse::getId, s -> s));

    LearningPathSection targetSection = findTargetSection(sections, currentSemesterId, semesterById, studentId, learningPathId);

    List<LearningPathSubject> subjects = subjectRepository.findByLearningPathId(learningPathId).stream()
        .filter(s -> s.getLearningPathSectionId().equals(targetSection.getLearningPathSectionId()))
        .toList();

    if (subjects.isEmpty()) {
      throw new IllegalStateException("No subjects found in target section");
    }

    SemesterResponse targetSemester = semesterById.get(targetSection.getSemesterId());
    List<TestDataGenerationResponse.TestDataSubjectDetail> details = new ArrayList<>();
    int passedCount = 0;
    int failedCount = 0;
    int idx = 0;

    for (LearningPathSubject subject : subjects) {
      ClassSectionTestDataResponse classSection = courseManagementClient.ensureClassSection(
          EnsureClassSectionRequest.builder()
              .subjectId(subject.getSubjectId())
              .semesterId(targetSection.getSemesterId())
              .createdBy(studentId)
              .build());

      boolean isPassed = switch (resolvedMode) {
        case MODE_PASSED -> true;
        case MODE_FAILED -> false;
        default -> idx % 2 == 0;
      };
      double grade = isPassed ? 7.0 + Math.random() * 2.0 : Math.random() * 3.9;

      learningServiceClient.createTestEnrollment(CreateTestEnrollmentRequest.builder()
          .studentId(studentId)
          .classId(classSection.getId())
          .subjectId(subject.getSubjectId())
          .finalGrade(Math.round(grade * 10.0) / 10.0)
          .isPassed(isPassed)
          .attemptNo(subject.getAttemptNo() != null ? subject.getAttemptNo() : 1)
          .gradingType("GRADED")
          .build());

      details.add(TestDataGenerationResponse.TestDataSubjectDetail.builder()
          .subjectCode(subject.getSubjectCode())
          .subjectName(subject.getSubjectName())
          .grade(Math.round(grade * 10.0) / 10.0)
          .isPassed(isPassed)
          .build());

      if (isPassed) passedCount++; else failedCount++;
      idx++;
    }

    log.info("Generated test data: semester={} subjects={} passed={} failed={}",
        targetSemester != null ? targetSemester.getSemesterCode() : "?", subjects.size(), passedCount, failedCount);

    return TestDataGenerationResponse.builder()
        .semesterCode(targetSemester != null ? targetSemester.getSemesterCode() : null)
        .semesterOrder(targetSection.getSemesterOrder())
        .subjectsGenerated(subjects.size())
        .passedCount(passedCount)
        .failedCount(failedCount)
        .details(details)
        .build();
  }

  private LearningPathSection findTargetSection(
      List<LearningPathSection> sections,
      UUID currentSemesterId,
      Map<UUID, SemesterResponse> semesterById,
      UUID studentId,
      UUID learningPathId) {

    // Load all LP subjects once, grouped by section
    List<LearningPathSubject> allSubjects = subjectRepository.findByLearningPathId(learningPathId);
    Map<UUID, List<LearningPathSubject>> subjectsBySection = allSubjects.stream()
        .collect(Collectors.groupingBy(LearningPathSubject::getLearningPathSectionId));

    int currentIdx = -1;
    for (int i = 0; i < sections.size(); i++) {
      if (sections.get(i).getSemesterId().equals(currentSemesterId)) {
        currentIdx = i;
        break;
      }
    }
    if (currentIdx == -1) {
      currentIdx = 0;
    }

    // Walk forward from current section to find one without completion grades
    for (int i = currentIdx; i < sections.size(); i++) {
      LearningPathSection section = sections.get(i);
      List<LearningPathSubject> sectionSubjects = subjectsBySection.getOrDefault(section.getLearningPathSectionId(), List.of());

      // Check if ANY subject in this section already has a completion grade
      boolean hasGrades = sectionSubjects.stream()
          .anyMatch(s -> s.getCompletionGrade() != null);

      if (!hasGrades) {
        SemesterResponse sem = semesterById.get(section.getSemesterId());
        log.info("Target section: semesterOrder={} semesterCode={} ({} subjects, graded={})",
            section.getSemesterOrder(),
            sem != null ? sem.getSemesterCode() : "?",
            sectionSubjects.size(),
            sectionSubjects.stream().filter(s -> s.getCompletionGrade() != null).count());
        return section;
      }
      SemesterResponse sem = semesterById.get(section.getSemesterId());
      log.info("Skipping section semesterOrder={} semesterCode={} (all {} subjects have grades)",
          section.getSemesterOrder(), sem != null ? sem.getSemesterCode() : "?", sectionSubjects.size());
    }

    throw new IllegalStateException("All semesters already have grade data for all subjects");
  }

  private List<SemesterResponse> fetchAllSemesters() {
    try {
      return courseManagementClient.getAllSemesters();
    } catch (Exception e) {
      log.warn("Failed to fetch semesters: {}", e.getMessage());
      return List.of();
    }
  }

  private UUID findCurrentSemesterId(List<SemesterResponse> allSemesters) {
    if (allSemesters == null || allSemesters.isEmpty()) return null;
    LocalDate today = LocalDate.now();
    for (SemesterResponse semester : allSemesters) {
      if (semester.getStartDate() != null && semester.getEndDate() != null) {
        LocalDate start = LocalDate.parse(semester.getStartDate());
        LocalDate end = LocalDate.parse(semester.getEndDate());
        if (!today.isBefore(start) && !today.isAfter(end)) {
          return semester.getId();
        }
      }
    }
    return null;
  }
}
