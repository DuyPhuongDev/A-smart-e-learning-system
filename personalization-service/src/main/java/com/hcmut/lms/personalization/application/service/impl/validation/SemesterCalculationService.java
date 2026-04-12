package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.service.impl.support.SemesterClassifier;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service to calculate remaining semesters for a student.
 * Supports scoping by expected graduation semester for timeline-based validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SemesterCalculationService {

  private final CourseManagementClient courseManagementClient;

  /**
   * Calculates semester availability from now until the expected completed semester.
   * If expected semester is null, uses all remaining semesters returned by course-management.
   */
  public SemesterAvailability calculateAvailableSemesters(UUID studentId, UUID expectedCompletedSemesterId) {
    log.info(
        "Calculating available semesters for studentId={} with expectedCompletedSemesterId={}", studentId,
        expectedCompletedSemesterId);

    try {
      List<SemesterResponse> remainingSemesters = courseManagementClient.getRemainingSemesters(studentId);
      if (remainingSemesters == null || remainingSemesters.isEmpty()) {
        return new SemesterAvailability(0, 0, List.of());
      }

      List<SemesterResponse> sortedBySemKey = remainingSemesters.stream()
          .sorted(Comparator.comparing(SemesterResponse::getSemKey, Comparator.nullsLast(Integer::compareTo)))
          .toList();

      List<SemesterResponse> scopedSemesters = sortedBySemKey;
      if (expectedCompletedSemesterId != null) {
        SemesterResponse expectedSemester = sortedBySemKey.stream()
            .filter(semester -> expectedCompletedSemesterId.equals(semester.getId()))
            .findFirst()
            .orElse(null);

        if (expectedSemester == null) {
          log.warn(
              "Expected semester {} is not in remaining semester set for studentId={}; using full remaining set",
              expectedCompletedSemesterId, studentId);
        } else if (expectedSemester.getSemKey() != null) {
          int expectedSemKey = expectedSemester.getSemKey();
          scopedSemesters = sortedBySemKey.stream()
              .filter(semester -> semester.getSemKey() != null && semester.getSemKey() <= expectedSemKey)
              .toList();
        }
      }

      int availableSummerSemesters = (int) scopedSemesters.stream().filter(SemesterClassifier::isSummerSemester).count();
      int availableMainSemesters = scopedSemesters.size() - availableSummerSemesters;

      log.info(
          "Calculated available semesters for studentId={}: main={}, summer={}, total={}", studentId,
          availableMainSemesters, availableSummerSemesters, scopedSemesters.size());

      return new SemesterAvailability(availableMainSemesters, availableSummerSemesters, scopedSemesters);

    } catch (Exception e) {
      log.error("Failed to calculate available semesters for studentId={}: {}", studentId, e.getMessage());
      // Conservative fallback for resiliency when semester service is unavailable.
      return new SemesterAvailability(8, 0, List.of());
    }
  }

  public record SemesterAvailability(
      int availableMainSemesters,
      int availableSummerSemesters,
      List<SemesterResponse> sortedRemainingSemesters) {
    public int totalSemesters() {
      return availableMainSemesters + availableSummerSemesters;
    }

    public int availableMainSemestersOnly() {
      return availableMainSemesters;
    }
  }
}
