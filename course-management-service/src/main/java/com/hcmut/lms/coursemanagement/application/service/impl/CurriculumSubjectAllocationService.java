package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.util.StudentGradeUtil;
import com.hcmut.lms.coursemanagement.client.dto.StudentEnrollmentResponse;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSection;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurriculumSubjectAllocationService {

  static final String FREE_ELECTIVE_SECTION_KEY = normalizeSectionName("Tự chọn tự do");

  public record SectionAllocationResult(Set<UUID> countedSubjectIds,
                                        Map<UUID, Integer> effectiveCompletedCreditsBySection,
                                        Set<UUID> freeElectiveAcceptedExcessSubjectIds) {
  }

  record AllocationCandidate(UUID subjectId, int credits, Double grade4, Double grade10) {
  }

  private final StudentProgressGradeUtil gradeUtil;

  public SectionAllocationResult allocateSubjectsToSections(
      List<CurriculumSection> sections,
      Map<UUID, StudentEnrollmentResponse> bestEnrollmentBySubjectMap,
      Map<UUID, SubjectGradingType> subjectGradingTypeMap) {

    Set<UUID> countedSubjectIds = new HashSet<>();
    Map<UUID, Integer> effectiveCompletedCreditsBySection = new HashMap<>();
    List<AllocationCandidate> excessPool = new ArrayList<>();
    Set<UUID> freeElectiveAcceptedExcessSubjectIds = new HashSet<>();

    CurriculumSection freeElectiveSection = sections.stream()
        .filter(s -> FREE_ELECTIVE_SECTION_KEY.equals(normalizeSectionName(s.getName())))
        .findFirst()
        .orElse(null);

    for (CurriculumSection section : sections) {
      if (section == freeElectiveSection) {
        continue;
      }

      int requiredCredits = section.getRequiredCredits() != null ? section.getRequiredCredits() : 0;
      List<AllocationCandidate> candidates = buildAllocationCandidates(
          section, bestEnrollmentBySubjectMap, subjectGradingTypeMap, countedSubjectIds);

      sortCandidatesByGrade(candidates);

      int runningTotal = 0;
      for (AllocationCandidate candidate : candidates) {
        if (requiredCredits == 0) {
          excessPool.add(candidate);
          continue;
        }
        if (runningTotal + candidate.credits() <= requiredCredits) {
          countedSubjectIds.add(candidate.subjectId());
          runningTotal += candidate.credits();
        } else {
          excessPool.add(candidate);
        }
      }

      effectiveCompletedCreditsBySection.put(section.getId(), runningTotal);
    }

    if (freeElectiveSection != null) {
      allocateFreeElectiveSection(
          freeElectiveSection, bestEnrollmentBySubjectMap, subjectGradingTypeMap,
          countedSubjectIds, excessPool, effectiveCompletedCreditsBySection,
          freeElectiveAcceptedExcessSubjectIds);
    }

    return new SectionAllocationResult(
        countedSubjectIds, effectiveCompletedCreditsBySection, freeElectiveAcceptedExcessSubjectIds);
  }

  private List<AllocationCandidate> buildAllocationCandidates(
      CurriculumSection section,
      Map<UUID, StudentEnrollmentResponse> bestEnrollmentBySubjectMap,
      Map<UUID, SubjectGradingType> subjectGradingTypeMap,
      Set<UUID> countedSubjectIds) {

    List<AllocationCandidate> candidates = new ArrayList<>();
    for (CurriculumSubject cs : section.getCurriculumSubjects()) {
      UUID subjectId = cs.getSubject().getId();
      StudentEnrollmentResponse enrollment = bestEnrollmentBySubjectMap.get(subjectId);
      if (enrollment == null) {
        continue;
      }

      SubjectGradingType gradingType = subjectGradingTypeMap.getOrDefault(subjectId, SubjectGradingType.GRADED);
      Boolean isPassed = gradeUtil.isStudentPassedSubject(enrollment, gradingType);
      if (isPassed == null || !isPassed) {
        continue;
      }

      int credits = cs.getSubject().getCredits();
      if (credits == 0) {
        countedSubjectIds.add(subjectId);
        continue;
      }

      Double grade4 = enrollment.getFinalGrade() != null ? StudentGradeUtil.convertTo4Scale(enrollment.getFinalGrade()) : null;
      Double grade10 = enrollment.getFinalGrade();
      candidates.add(new AllocationCandidate(subjectId, credits, grade4, grade10));
    }
    return candidates;
  }

  private void allocateFreeElectiveSection(
      CurriculumSection freeElectiveSection,
      Map<UUID, StudentEnrollmentResponse> bestEnrollmentBySubjectMap,
      Map<UUID, SubjectGradingType> subjectGradingTypeMap,
      Set<UUID> countedSubjectIds,
      List<AllocationCandidate> excessPool,
      Map<UUID, Integer> effectiveCompletedCreditsBySection,
      Set<UUID> freeElectiveAcceptedExcessSubjectIds) {

    int freeRequiredCredits = freeElectiveSection.getRequiredCredits() != null ?
        freeElectiveSection.getRequiredCredits() : 0;

    List<AllocationCandidate> freeCandidates = buildAllocationCandidates(
        freeElectiveSection, bestEnrollmentBySubjectMap, subjectGradingTypeMap, countedSubjectIds);

    List<AllocationCandidate> combined = new ArrayList<>(freeCandidates);
    combined.addAll(excessPool);

    sortCandidatesByGrade(combined);

    int runningTotal = 0;
    for (AllocationCandidate candidate : combined) {
      if (freeRequiredCredits == 0) {
        break;
      }
      if (runningTotal + candidate.credits() <= freeRequiredCredits) {
        countedSubjectIds.add(candidate.subjectId());
        runningTotal += candidate.credits();
        if (!freeCandidates.contains(candidate)) {
          freeElectiveAcceptedExcessSubjectIds.add(candidate.subjectId());
        }
      }
    }

    effectiveCompletedCreditsBySection.put(freeElectiveSection.getId(), runningTotal);
  }

  private void sortCandidatesByGrade(List<AllocationCandidate> candidates) {
    candidates.sort((a, b) -> {
      boolean aHasGrade = a.grade4() != null;
      boolean bHasGrade = b.grade4() != null;
      if (aHasGrade && bHasGrade) {
        int cmp = Double.compare(b.grade4(), a.grade4());
        if (cmp != 0) return cmp;
        double a10 = a.grade10() != null ? a.grade10() : 0.0;
        double b10 = b.grade10() != null ? b.grade10() : 0.0;
        return Double.compare(b10, a10);
      }
      if (aHasGrade) return -1;
      if (bHasGrade) return 1;
      return 0;
    });
  }

  static String normalizeSectionName(String name) {
    if (name == null) {
      return "";
    }
    String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    return normalized.trim().toLowerCase(java.util.Locale.ROOT);
  }
}