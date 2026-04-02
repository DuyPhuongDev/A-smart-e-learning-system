package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.PrerequisiteChainRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.PrerequisiteChainResponse;
import com.hcmut.lms.coursemanagement.application.service.PrerequisiteChainService;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrerequisiteChainServiceImpl implements PrerequisiteChainService {

  private final CurriculumSubjectRepository curriculumSubjectRepository;

  private static class ChainResult {
    final int length;
    final List<UUID> path;

    ChainResult(int length, List<UUID> path) {
      this.length = length;
      this.path = path;
    }
  }

  @Override
  @Transactional(readOnly = true)
  public PrerequisiteChainResponse calculatePrerequisiteChain(PrerequisiteChainRequest request) {
    log.info("Calculating prerequisite chain for specializationId={}", request.getSpecializationId());

    Set<UUID> completedSubjectIds = getCompletedSubjectIds(request);
    List<CurriculumSubject> subjects = fetchSubjectsBySpecialization(UUID.fromString(request.getSpecializationId()));
    Set<UUID> remainingSubjectIds = getRemainingSubjectIds(request, subjects, completedSubjectIds);

    int longestChain = calculateLongestChain(subjects, completedSubjectIds, remainingSubjectIds);

    log.info("Longest prerequisite chain: {} semesters", longestChain);
    return PrerequisiteChainResponse.builder().longestChainLength(longestChain).build();
  }

  private Set<UUID> getCompletedSubjectIds(PrerequisiteChainRequest request) {
    return request.getCompletedSubjectIds() != null ? new HashSet<>(request.getCompletedSubjectIds()) : new HashSet<>();
  }

  private List<CurriculumSubject> fetchSubjectsBySpecialization(UUID specializationId) {
    if (specializationId != null) {
      return curriculumSubjectRepository.findBySpecializationIdWithPrerequisites(specializationId);
    }
    return curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations();
  }

  private Set<UUID> getRemainingSubjectIds(
      PrerequisiteChainRequest request,
      List<CurriculumSubject> subjects,
      Set<UUID> completedSubjectIds
  ) {
    if (request.getRemainingSubjectIds() != null && !request.getRemainingSubjectIds().isEmpty()) {
      Set<UUID> remainingSubjectIds = new HashSet<>(request.getRemainingSubjectIds());
      remainingSubjectIds.removeAll(completedSubjectIds);
      return remainingSubjectIds;
    }

    // Backward-compatible fallback: infer remaining from curriculum minus completed.
    return subjects.stream()
        .map(curriculumSubject -> curriculumSubject.getSubject().getId())
        .filter(subjectId -> !completedSubjectIds.contains(subjectId))
        .collect(Collectors.toSet());
  }

  private int calculateLongestChain(
      List<CurriculumSubject> subjects,
      Set<UUID> completedSubjectIds,
      Set<UUID> remainingSubjectIds
  ) {
    Map<UUID, CurriculumSubject> subjectById = subjects.stream()
        .collect(Collectors.toMap(
            curriculumSubject -> curriculumSubject.getSubject().getId(),
            Function.identity(),
            (existing, replacement) -> existing));

    ChainResult longestResult = new ChainResult(0, new ArrayList<>());

    for (UUID subjectId : remainingSubjectIds) {
      CurriculumSubject subject = subjectById.get(subjectId);
      if (subject == null) {
        continue;
      }
      List<UUID> currentPath = new ArrayList<>();
      int chainLength = calculateChainLength(subject, completedSubjectIds, remainingSubjectIds, new HashSet<>(), currentPath);
      if (chainLength > longestResult.length) {
        longestResult = new ChainResult(chainLength, new ArrayList<>(currentPath));
      }
    }

    if (!longestResult.path.isEmpty()) {
      String chainPath = longestResult.path.stream()
          .map(id -> {
            CurriculumSubject cs = subjectById.get(id);
            return cs != null && cs.getSubject() != null ? cs.getSubject().getCode() : id.toString();
          })
          .collect(Collectors.joining(" -> "));
      log.info("Longest prerequisite chain path: {} (length = {})", chainPath, longestResult.length);
    }

    return longestResult.length;
  }

  private int calculateChainLength(
      CurriculumSubject subject,
      Set<UUID> completedSubjectIds,
      Set<UUID> remainingSubjectIds,
      Set<UUID> visited,
      List<UUID> currentPath
  ) {
    UUID subjectId = subject.getSubject().getId();

    if (isSubjectCompleted(subjectId, completedSubjectIds)
        || !remainingSubjectIds.contains(subjectId)
        || isCircularDependency(subjectId, visited)) {
      return 0;
    }

    visited.add(subjectId);
    currentPath.add(subjectId);

    int chainLength = hasPrerequisites(subject) ? calculateMaxPrerequisiteChain(
        subject, completedSubjectIds, remainingSubjectIds, visited, currentPath) + 1 : 1;

    visited.remove(subjectId);
    return chainLength;
  }

  private boolean isSubjectCompleted(UUID subjectId, Set<UUID> completedSubjectIds) {
    return completedSubjectIds.contains(subjectId);
  }

  private boolean isCircularDependency(UUID subjectId, Set<UUID> visited) {
    return visited.contains(subjectId);
  }

  private boolean hasPrerequisites(CurriculumSubject subject) {
    List<SubjectPrerequisite> prerequisites = subject.getPrerequisites();
    return prerequisites != null && !prerequisites.isEmpty();
  }

  private int calculateMaxPrerequisiteChain(
      CurriculumSubject subject, Set<UUID> completedSubjectIds,
      Set<UUID> remainingSubjectIds,
      Set<UUID> visited,
      List<UUID> currentPath) {
    int maxChain = 0;
    List<UUID> bestPath = new ArrayList<>();

    for (SubjectPrerequisite prereq : subject.getPrerequisites()) {
      CurriculumSubject prereqSubject = prereq.getPrerequisiteCurriculumSubject();
      if (prereqSubject != null) {
        List<UUID> prereqPath = new ArrayList<>();
        int prereqChain = calculateChainLength(prereqSubject, completedSubjectIds, remainingSubjectIds, visited, prereqPath);
        if (prereqChain > maxChain) {
          maxChain = prereqChain;
          bestPath = prereqPath;
        }
      }
    }

    currentPath.addAll(bestPath);
    return maxChain;
  }
}
