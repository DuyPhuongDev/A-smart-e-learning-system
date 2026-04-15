package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.ClassSectionReportMetadataResponse;
import com.hcmut.lms.learning.client.dto.LectureReportMetadataResponse;
import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.LectureFrequencyItemResponse;
import com.hcmut.lms.learning.dto.response.LectureFrequencyResponse;
import com.hcmut.lms.learning.dto.response.StudyTimeResponse;
import com.hcmut.lms.learning.dto.response.StudyTimeSummaryResponse;
import com.hcmut.lms.learning.entity.progress.LearningProgress;
import com.hcmut.lms.learning.entity.studytime.StudyTime;
import com.hcmut.lms.learning.exception.BusinessException;
import com.hcmut.lms.learning.exception.ForbiddenException;
import com.hcmut.lms.learning.exception.ResourceNotFoundException;
import com.hcmut.lms.learning.mapper.StudyTimeMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.LearningProgressRepository;
import com.hcmut.lms.learning.repository.StudyTimeRepository;
import com.hcmut.lms.learning.service.LearningProgressService;
import com.hcmut.lms.learning.service.StudyTimeService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyTimeServiceImpl implements StudyTimeService {

    private final StudyTimeRepository studyTimeRepository;
    private final StudyTimeMapper studyTimeMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final LearningProgressRepository learningProgressRepository;
    private final LearningProgressService learningProgressService;
    private final CourseManagementClient courseManagementClient;

    @Override
    @Transactional
    public StudyTimeResponse recordStudyTime(UUID studentId, StudyTimeRequest request) {
        log.info("Recording study time for student {} in class {}", studentId, request.getClassId());

        if(!enrollmentRepository.existsByStudentIdAndClassId(studentId, request.getClassId()))
            throw new BusinessException("STUDENT_NOT_ENROLLED");

        // Create study time record
        StudyTime studyTime = studyTimeMapper.toEntity(request);
        studyTime.setStudentId(studentId);

        // Calculate endedAt
        if (studyTime.getEndedAt() == null && studyTime.getStartedAt() != null) {
            studyTime.setEndedAt(studyTime.getStartedAt().plusSeconds(request.getDurationSeconds()));
        }

        StudyTime savedStudyTime = studyTimeRepository.save(studyTime);

        LearningProgress progress = learningProgressRepository
                .findByStudentIdAndLectureId(studentId, request.getLectureId())
                .orElseGet(() -> {
                    log.info("Creating progress for student {} in class {}", studentId, request.getClassId());
                    LearningProgress lp = new LearningProgress();
                    lp.setStudentId(studentId);
                    lp.setLectureId(request.getLectureId());
                    lp.setTotalTimeSpent(0);
                    // Default content type khi tạo từ StudyTime (có thể chỉnh sau nếu BE/FE cung cấp được kiểu lecture)
                    lp.setContentType(request.getContentType());
                    lp.setPositionUnit(request.getPositionUnit());
                    return lp;
                });

        //update
        log.info("Update progress for student {} in class {}", studentId, request.getClassId());
        progress.setCurrentPosition(request.getCurrentPosition());
        progress.setTotalTimeSpent(progress.getTotalTimeSpent()+request.getDurationSeconds());

        if(progress.getProgressPercentage() == null || progress.getProgressPercentage().compareTo(BigDecimal.valueOf(100L))<0){
            progress.setProgressPercentage(learningProgressService.calcProgress(progress));
            if(progress.getProgressPercentage().compareTo(BigDecimal.valueOf(100L)) == 0){
                progress.setCompletedAt(Instant.now());
            }

            learningProgressRepository.save(progress);
        }


        log.info("Study time recorded successfully: {} seconds for student {} in class {}",
                request.getDurationSeconds(), studentId, request.getClassId());

        return studyTimeMapper.toResponse(savedStudyTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyTimeResponse> getStudyTimeHistory(UUID studentId, UUID classId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();

        List<StudyTime> studyTimes = studyTimeRepository.findByStudentIdAndClassIdAndDateRange(
                studentId, classId, startDateTime, endDateTime);

        return studyTimes.stream()
                .map(studyTimeMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudyTimeSummaryResponse> getStudyTimeSummary(UUID studentId, UUID classId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();

        List<Object[]> results = studyTimeRepository.getStudyTimeSummaryByDate(
                studentId, classId, startDateTime, endDateTime);

        return results.stream()
                .map(result -> {
                    LocalDate date = ((java.sql.Date) result[0]).toLocalDate();
                    long totalSeconds = ((Number) result[1]).longValue();
                    Long sessionCount = ((Number) result[2]).longValue();

                    return StudyTimeSummaryResponse.builder()
                            .studentId(studentId)
                            .classId(classId)
                            .date(date)
                            .totalSeconds((int) totalSeconds)
                            .totalMinutes((int) totalSeconds / 60)
                            .totalHours((int) totalSeconds / 3600)
                            .sessionCount(sessionCount)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalStudyTime(UUID studentId, UUID classId) {
        Integer totalSeconds = studyTimeRepository.getTotalStudyTimeByStudentAndClass(studentId, classId);
        return totalSeconds != null ? totalSeconds : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public LectureFrequencyResponse getLectureFrequencyForTeacher(UUID classId, CurrentUserInfo currentUserInfo) {
        ClassSectionReportMetadataResponse classMetadata = getClassSectionReportMetadataOrThrow(classId);
        assertTeacherOrAdmin(currentUserInfo, classMetadata.getTeacherId());

        List<UUID> studentIds = enrollmentRepository.findDistinctStudentIdsByClassId(classId);
        int totalStudents = studentIds.size();

        Map<UUID, Long> totalSpentByLecture = new HashMap<>();
        for (Object[] row : studyTimeRepository.sumDurationByClassGroupedByLecture(classId)) {
            UUID lectureId = (UUID) row[0];
            long totalSpentSeconds = row[1] != null ? ((Number) row[1]).longValue() : 0L;
            totalSpentByLecture.put(lectureId, totalSpentSeconds);
        }

        List<LectureReportMetadataResponse> allLectures = classMetadata.getLectures() == null
                ? List.of()
                : classMetadata.getLectures();

        List<LectureFrequencyItemResponse> eligibleLectureRows = allLectures.stream()
                .filter(lecture -> lecture.getEstimateTimeSpent() != null && lecture.getEstimateTimeSpent() > 0)
                .sorted(Comparator.comparing(LectureReportMetadataResponse::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(lecture -> {
                    long totalSpentSeconds = totalSpentByLecture.getOrDefault(lecture.getLectureId(), 0L);

                    BigDecimal ratio;
                    if (totalStudents == 0) {
                        ratio = BigDecimal.ZERO;
                    } else {
                        BigDecimal denominator = BigDecimal.valueOf((long) totalStudents)
                                .multiply(BigDecimal.valueOf(lecture.getEstimateTimeSpent()))
                                .multiply(BigDecimal.valueOf(60L));
                        ratio = denominator.compareTo(BigDecimal.ZERO) == 0
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(totalSpentSeconds)
                                .divide(denominator, 6, RoundingMode.HALF_UP);
                    }

                    BigDecimal percent = ratio.multiply(BigDecimal.valueOf(100L)).setScale(2, RoundingMode.HALF_UP);

                    return LectureFrequencyItemResponse.builder()
                            .lectureId(lecture.getLectureId())
                            .title(lecture.getTitle())
                            .order(lecture.getOrder())
                            .estimateTimeMinutes(lecture.getEstimateTimeSpent())
                            .studentCount(totalStudents)
                            .totalSpentSeconds(totalSpentSeconds)
                            .frequencyRatio(ratio.setScale(6, RoundingMode.HALF_UP))
                            .frequencyPercent(percent)
                            .build();
                })
                .toList();

        BigDecimal overallRatio = eligibleLectureRows.isEmpty()
                ? BigDecimal.ZERO
                : eligibleLectureRows.stream()
                .map(LectureFrequencyItemResponse::getFrequencyRatio)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(eligibleLectureRows.size()), 6, RoundingMode.HALF_UP);

        BigDecimal overallPercent = overallRatio.multiply(BigDecimal.valueOf(100L)).setScale(2, RoundingMode.HALF_UP);

        return LectureFrequencyResponse.builder()
                .classId(classId)
                .totalStudents(totalStudents)
                .totalLectures(allLectures.size())
                .eligibleLectures(eligibleLectureRows.size())
                .overallFrequencyRatio(overallRatio)
                .overallFrequencyPercent(overallPercent)
                .lectures(eligibleLectureRows)
                .generatedAt(Instant.now())
                .build();
    }

    private void assertTeacherOrAdmin(CurrentUserInfo currentUserInfo, UUID classTeacherId) {
        if (currentUserInfo == null || currentUserInfo.getRole() == null) {
            throw new ForbiddenException("Access denied");
        }

        String role = currentUserInfo.getRole().toUpperCase();
        if ("ADMIN".equals(role)) {
            return;
        }

        if (!"TEACHER".equals(role)) {
            throw new ForbiddenException("Access denied");
        }

        if (classTeacherId == null || !classTeacherId.equals(currentUserInfo.getId())) {
            throw new ForbiddenException("Teacher is not assigned to this class");
        }
    }

    private ClassSectionReportMetadataResponse getClassSectionReportMetadataOrThrow(UUID classId) {
        try {
            return courseManagementClient.getClassSectionReportMetadata(classId);
        } catch (FeignException ex) {
            if (ex.status() == 404) {
                throw new ResourceNotFoundException("ClassSection", "id", classId);
            }
            throw ex;
        }
    }

}
