package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.StudyTimeResponse;
import com.hcmut.lms.learning.dto.response.StudyTimeSummaryResponse;
import com.hcmut.lms.learning.entity.progress.ContentType;
import com.hcmut.lms.learning.entity.progress.LearningProgress;
import com.hcmut.lms.learning.entity.studytime.StudyTime;
import com.hcmut.lms.learning.exception.BusinessException;
import com.hcmut.lms.learning.mapper.LearningProgressMapper;
import com.hcmut.lms.learning.mapper.StudyTimeMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.LearningProgressRepository;
import com.hcmut.lms.learning.repository.StudyTimeRepository;
import com.hcmut.lms.learning.service.EnrollmentService;
import com.hcmut.lms.learning.service.LearningProgressService;
import com.hcmut.lms.learning.service.StudyTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    private final EnrollmentService enrollmentService;

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
        progress.setProgressPercentage(learningProgressService.calcProgress(progress));
        if(progress.getProgressPercentage().compareTo(BigDecimal.valueOf(100L)) == 0){
            progress.setCompletedAt(Instant.now());
            // update enrollment
            enrollmentService.updateProgress(studentId, request.getClassId());
        }

        learningProgressRepository.save(progress);

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
                    Long totalSeconds = ((Number) result[1]).longValue();
                    Long sessionCount = ((Number) result[2]).longValue();

                    return StudyTimeSummaryResponse.builder()
                            .studentId(studentId)
                            .classId(classId)
                            .date(date)
                            .totalSeconds(totalSeconds.intValue())
                            .totalMinutes(totalSeconds.intValue() / 60)
                            .totalHours(totalSeconds.intValue() / 3600)
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

}
