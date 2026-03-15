package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.BatchClassDatasetLookupRequest;
import com.hcmut.lms.learning.client.dto.ClassSectionDatasetResponse;
import com.hcmut.lms.learning.client.dto.SemesterResponse;
import com.hcmut.lms.learning.client.dto.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.learning.client.dto.SubjectWindowDatasetLookupRequest;
import com.hcmut.lms.learning.dto.internal.ExtractedFeatures;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.mapper.FeatureExtractionMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.service.FeatureExtractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeatureExtractionServiceImpl implements FeatureExtractionService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseManagementClient courseManagementClient;
    private final FeatureExtractionMapper featureExtractionMapper;

    /** Default credits when not provided — typical full-time load at HCMUT */
    private static final int DEFAULT_SEM_CREDITS = 17;

    private static final double SHRINKAGE_K = 10.0;
    /** 3-year window for course history (30 semKey units), matching GradePredictionDatasetServiceImpl */
    private static final int WINDOW_SPAN_COURSE = 30;

    @Override
    public Map<String, Object> extractFeatures(UUID studentId,
                                               UUID subjectId,
                                               Integer plannedSemesterCredits) {
        log.info("Extracting features for studentId={}, subjectId={}", studentId, subjectId);

        // Get current semester (includes semKey directly — no second API call needed)
        SemesterResponse currentSemester = getCurrentSemester();
        if (currentSemester == null || currentSemester.getSemKey() == null) {
            log.warn("Cannot resolve current semester or semKey, using defaults");
            Map<String, Object> defaultFeatures = buildDefaultFeatures(plannedSemesterCredits);
            if (log.isDebugEnabled()) {
                log.debug("Extracted default features for studentId={}, subjectId={}: {}",
                        studentId, subjectId, formatFeaturesForLog(defaultFeatures));
            }
            return defaultFeatures;
        }

        int targetSemKey = currentSemester.getSemKey();
        int semCredits = plannedSemesterCredits != null ? plannedSemesterCredits : DEFAULT_SEM_CREDITS;

        // Get all student enrollments with grades
        List<Enrollment> studentEnrollments = enrollmentRepository.findByStudentId(studentId)
                .stream()
                .filter(e -> e.getFinalGrade() != null)
                .toList();

        // Get metadata for all student's classes
        Map<UUID, ClassSectionDatasetResponse> classMetadataMap = getClassMetadataMap(
                studentEnrollments.stream().map(Enrollment::getClassId).toList()
        );
        if (log.isDebugEnabled()) {
            log.debug("Found {} graded enrollments for studentId={}", studentEnrollments.size(), studentId);
        }

        // Filter enrollments before target semester
        List<Enrollment> priorEnrollments = studentEnrollments.stream()
                .filter(e -> {
                    ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
                    return meta != null && meta.getSemKey() < targetSemKey;
                })
                .toList();

        // Attempt count for this subject (0 first attempt)
        int retakeNo = studentEnrollments.stream()
                .filter(e -> {
                    ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
                    return meta != null && subjectId.equals(meta.getSubjectId());
                })
                .mapToInt(e -> e.getAttemptNo() != null ? e.getAttemptNo() : 0)
                .max()
                .orElse(0);

        // Compute student history features (credit-weighted averages on 4-point scale)
        boolean hasStudentHistory = !priorEnrollments.isEmpty();
        int numSemestersPrior = hasStudentHistory
                ? (int) priorEnrollments.stream()
                        .map(e -> classMetadataMap.get(e.getClassId()).getSemKey())
                        .distinct().count()
                : 0;

        double cumulativeGradeAvg = 0.0;
        if (hasStudentHistory) {
            double sumWeightedGrade = 0, sumCredits = 0;
            for (Enrollment e : priorEnrollments) {
                ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
                int credits = meta != null && meta.getCredits() != null ? meta.getCredits() : 0;
                double grade4pt = convertTo4Point(e.getFinalGrade());
                sumWeightedGrade += grade4pt * credits;
                sumCredits += credits;
            }
            cumulativeGradeAvg = sumCredits > 0 ? sumWeightedGrade / sumCredits : 0.0;
        }

        double previousSemGradeAvg = 0.0;
        if (hasStudentHistory) {
            // Use the latest prior semester that has grades (handles gaps/dropped semesters)
            Integer latestPriorSemKey = priorEnrollments.stream()
                    .map(e -> classMetadataMap.get(e.getClassId()))
                    .filter(Objects::nonNull)
                    .map(ClassSectionDatasetResponse::getSemKey)
                    .filter(Objects::nonNull)
                    .filter(sk -> sk < targetSemKey)
                    .max(Integer::compareTo)
                    .orElse(null);

            if (latestPriorSemKey != null) {
                List<Enrollment> prevSemEnrollments = priorEnrollments.stream()
                        .filter(e -> {
                            ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
                            return meta != null && latestPriorSemKey.equals(meta.getSemKey());
                        })
                        .toList();

                if (!prevSemEnrollments.isEmpty()) {
                    double sumWG = 0, sumC = 0;
                    for (Enrollment e : prevSemEnrollments) {
                        ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
                        int credits = meta != null && meta.getCredits() != null ? meta.getCredits() : 0;
                        double grade4pt = convertTo4Point(e.getFinalGrade());
                        sumWG += grade4pt * credits;
                        sumC += credits;
                    }
                    previousSemGradeAvg = sumC > 0 ? sumWG / sumC : 0.0;
                }
            }
        }

        // Subject baseline features
        List<Enrollment> subjectHistory = getCourseHistory(subjectId, targetSemKey);
        int subjectHistCount = subjectHistory.size();
        boolean subjectHistMissing = subjectHistCount == 0;

        double globalMedian = computeGlobalMedian(subjectHistory);
        double rawMedian = computeMedian4pt(subjectHistory);
        double subjectHistMedianSmooth = subjectHistMissing
                ? 0.0
                : (rawMedian * subjectHistCount + globalMedian * SHRINKAGE_K)
                    / (subjectHistCount + SHRINKAGE_K);

        if (log.isDebugEnabled()) {
            log.debug("Subject history count={}, rawMedian={}, globalMedian={}, smoothed={}",
                    subjectHistCount, rawMedian, globalMedian, subjectHistMedianSmooth);
        }

        // Relative course features (prerequisites + recommendations)
        List<UUID> relatedSubjectIds = getRelatedSubjects(subjectId);
        boolean hasRelativeCourse = !relatedSubjectIds.isEmpty();

        double relativeAvgCourseGrade = 0.0;
        boolean hasRelativeEnrollments = false;
        if (hasRelativeCourse) {
            List<Enrollment> relativeEnrollments = priorEnrollments.stream()
                    .filter(e -> {
                        ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
                        return meta != null && relatedSubjectIds.contains(meta.getSubjectId());
                    })
                    .toList();

            if (!relativeEnrollments.isEmpty()) {
                hasRelativeEnrollments = true;
                double sumWeightedGrade = 0, sumCredits = 0;
                for (Enrollment e : relativeEnrollments) {
                    ClassSectionDatasetResponse meta = classMetadataMap.get(e.getClassId());
                    int credits = meta != null && meta.getCredits() != null ? meta.getCredits() : 0;
                    double grade4pt = convertTo4Point(e.getFinalGrade());
                    sumWeightedGrade += grade4pt * credits;
                    sumCredits += credits;
                }
                relativeAvgCourseGrade = sumCredits > 0
                    ? sumWeightedGrade / sumCredits
                    : relativeEnrollments.stream().mapToDouble(e -> convertTo4Point(e.getFinalGrade())).average().orElse(0.0);
            }
        }

        // Fallback: when no related courses with grades, use previous semester GPA if available
        if (!hasRelativeEnrollments) {
            relativeAvgCourseGrade = previousSemGradeAvg > 0.0 ? previousSemGradeAvg : 0.0;
        }

        Map<String, Object> features = featureExtractionMapper.toFeatureMap(ExtractedFeatures.builder()
                .semCredits(semCredits)
                .retakeNo(retakeNo)
                .numSemestersPrior(numSemestersPrior)
                .cumulativeGradeAvg(cumulativeGradeAvg)
                .previousSemGradeAvg(previousSemGradeAvg)
                .subjectHistMedianSmooth(subjectHistMedianSmooth)
                .relativeAvgCourseGrade(relativeAvgCourseGrade)
                .build());

        log.info("Extracted features for studentId={}, subjectId={}: {}",
                studentId, subjectId, formatFeaturesForLog(features));

        return features;
    }

    // ---- helpers ----

    /**
     * Get the current semester from course-management-service.
     * semKey is included directly in the response — no additional API calls needed.
     */
    private SemesterResponse getCurrentSemester() {
        try {
            return courseManagementClient.getCurrentSemester();
        } catch (Exception e) {
            log.error("Failed to get current semester", e);
            return null;
        }
    }

    private Map<UUID, ClassSectionDatasetResponse> getClassMetadataMap(List<UUID> classIds) {
        if (classIds.isEmpty()) return Collections.emptyMap();
        try {
            List<ClassSectionDatasetResponse> responses = courseManagementClient.getClassSectionsForDataset(
                    BatchClassDatasetLookupRequest.builder().classIds(classIds).build());
            return responses.stream()
                    .collect(Collectors.toMap(ClassSectionDatasetResponse::getClassId, r -> r));
        } catch (Exception e) {
            log.error("Failed to fetch class metadata for {} classes", classIds.size(), e);
            return Collections.emptyMap();
        }
    }

    /** Convert 10-point to 4-point scale (matches training logic) */
    private double convertTo4Point(double grade10) {
        if (grade10 >= 8.5) return 4.0;   // A+ (>=9.5) and A (8.5–9.4) → 4.0
        if (grade10 >= 8.0) return 3.5;   // B+
        if (grade10 >= 7.0) return 3.0;   // B
        if (grade10 >= 6.5) return 2.5;   // C+
        if (grade10 >= 5.5) return 2.0;   // C
        if (grade10 >= 5.0) return 1.5;   // D+
        if (grade10 >= 4.0) return 1.0;   // D
        return 0.0;                        // F
    }

    /**
     * Get all graded enrollments for a subject within the 3-year (30 semKey units) window before targetSemKey.
     * Matches the WINDOW_SPAN_COURSE logic in GradePredictionDatasetServiceImpl.
     */
    private List<Enrollment> getCourseHistory(UUID subjectId, int targetSemKey) {
        int windowLow = targetSemKey - WINDOW_SPAN_COURSE;

        List<ClassSectionDatasetResponse> subjectClasses;
        try {
            subjectClasses = courseManagementClient.getClassSectionsBySubjectWindow(
                    SubjectWindowDatasetLookupRequest.builder()
                            .subjectId(subjectId)
                            .targetSemKey(targetSemKey)
                            .windowSpan(WINDOW_SPAN_COURSE)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to fetch subject window classes for subjectId={}", subjectId, e);
            return Collections.emptyList();
        }

        Set<UUID> classIdsForSubject = subjectClasses.stream()
                .filter(meta -> meta.getSemKey() != null
                        && meta.getSemKey() >= windowLow
                        && meta.getSemKey() < targetSemKey)
                .map(ClassSectionDatasetResponse::getClassId)
                .collect(Collectors.toSet());

        if (classIdsForSubject.isEmpty()) return Collections.emptyList();

        return enrollmentRepository.findByClassIdInAndFinalGradeIsNotNull(
                new ArrayList<>(classIdsForSubject));
    }

    /**
     * Compute global median from all course history (4-point scale).
     * Used for shrinkage smoothing.
     */
    private double computeGlobalMedian(List<Enrollment> courseHistory) {
        if (courseHistory.isEmpty()) return 0.0;

        List<Double> grades4pt = courseHistory.stream()
                .map(e -> convertTo4Point(e.getFinalGrade()))
                .sorted()
                .toList();

        int size = grades4pt.size();
        if (size % 2 == 0) {
            return (grades4pt.get(size / 2 - 1) + grades4pt.get(size / 2)) / 2.0;
        } else {
            return grades4pt.get(size / 2);
        }
    }

    private double computeMedian4pt(List<Enrollment> courseHistory) {
        List<Double> grades4pt = courseHistory.stream()
                .map(e -> convertTo4Point(e.getFinalGrade()))
                .sorted()
                .toList();

        int size = grades4pt.size();
        if (size == 0) return 0.0;
        if (size % 2 == 0) {
            return (grades4pt.get(size / 2 - 1) + grades4pt.get(size / 2)) / 2.0;
        } else {
            return grades4pt.get(size / 2);
        }
    }

    private List<UUID> getRelatedSubjects(UUID subjectId) {
        try {
            return courseManagementClient.getPrerequisiteMapping().stream()
                    .filter(m -> subjectId.equals(m.getSubjectId()))
                    .findFirst()
                    .map(SubjectPrerequisiteMapResponse::getRelatedSubjectIds)
                    .orElse(Collections.emptyList());
        } catch (Exception e) {
            log.error("Failed to fetch prerequisite mapping for subjectId={}", subjectId, e);
            return Collections.emptyList();
        }
    }

    /**
     * Fallback when current semester cannot be resolved.
     * All features use required defaults.
     */
    private Map<String, Object> buildDefaultFeatures(Integer plannedSemesterCredits) {
        int semCredits = plannedSemesterCredits != null ? plannedSemesterCredits : DEFAULT_SEM_CREDITS;
        Map<String, Object> features = featureExtractionMapper.toFeatureMap(ExtractedFeatures.builder()
                .semCredits(semCredits)
                .retakeNo(0)
                .numSemestersPrior(0)
                .cumulativeGradeAvg(0.0)
                .previousSemGradeAvg(0.0)
                .subjectHistMedianSmooth(0.0)
                .relativeAvgCourseGrade(0.0)
                .build());
        if (log.isDebugEnabled()) {
            log.debug("Default features used (plannedSemesterCredits={}): {}", plannedSemesterCredits, formatFeaturesForLog(features));
        }
        return features;
    }

    private String formatFeaturesForLog(Map<String, Object> features) {
        return features.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
    }
}
