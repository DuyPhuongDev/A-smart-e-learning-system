package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.client.CourseManagementClient;
import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.client.dto.LectureType;
import com.hcmut.lms.learning.dto.request.LearningProgressRequest;
import com.hcmut.lms.learning.dto.response.ClassProgressResponse;
import com.hcmut.lms.learning.dto.response.LearningProgressResponse;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.progress.ContentType;
import com.hcmut.lms.learning.entity.progress.LearningProgress;
import com.hcmut.lms.learning.exception.BusinessException;
import com.hcmut.lms.learning.mapper.LearningProgressMapper;
import com.hcmut.lms.learning.repository.EnrollmentRepository;
import com.hcmut.lms.learning.repository.LearningProgressRepository;
import com.hcmut.lms.learning.repository.StudyTimeRepository;
import com.hcmut.lms.learning.service.EnrollmentService;
import com.hcmut.lms.learning.service.LearningProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LearningProgressServiceImpl implements LearningProgressService {

    private final LearningProgressRepository learningProgressRepository;
    private final LearningProgressMapper learningProgressMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final StudyTimeRepository studyTimeRepository;
    private final EnrollmentService enrollmentService;
    private final CourseManagementClient courseManagementClient;

    @Override
    @Transactional
    public LearningProgressResponse trackingProgress(UUID studentId, LearningProgressRequest request) {

        log.info("Tracking learning progress for student {} in lecture {}", studentId, request.getLectureId());

        // Find existing progress
        LearningProgress progress = learningProgressRepository
                .findByStudentIdAndLectureId(studentId, request.getLectureId())
                .orElseGet(() -> {
                    // CREATE NEW nếu chưa có
                    LearningProgress newProgress = learningProgressMapper.toEntity(request);
                    newProgress.setStudentId(studentId);
                    newProgress.setLectureId(request.getLectureId());
                    return newProgress;
                });

        // UPDATE EXISTING
        learningProgressMapper.updateEntityFromRequest(request, progress);


        // BE tính progressPercentage
        // Hardcode auto complete
        if(Boolean.TRUE.equals(request.getIsCompleted())){
            progress.setProgressPercentage(BigDecimal.valueOf(100L));
            progress.setCompletedAt(Instant.now());
            enrollmentService.updateProgress(studentId, request.getClassId());
        }

        // Save (Hibernate sẽ auto insert/update)
        LearningProgress savedProgress = learningProgressRepository.save(progress);

        log.info("Learning progress updated successfully for student {} in lecture {}", studentId, request.getLectureId());

        return learningProgressMapper.toResponse(savedProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public LearningProgressResponse getProgressForStudent(UUID studentId, UUID lectureId) {
        log.info("Getting learning progress for student {} in lecture {}", studentId, lectureId);

        LearningProgress progress = learningProgressRepository.findByStudentIdAndLectureId(studentId, lectureId)
                .orElse(null);

        if (progress == null) {
            // Return empty progress response
            return LearningProgressResponse.builder()
                    .studentId(studentId)
                    .lectureId(lectureId)
                    .progressPercentage(BigDecimal.ZERO)
                    .currentPosition(0)
                    .totalTimeSpent(0)
                    .build();
        }

        return learningProgressMapper.toResponse(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningProgressResponse> getProgressesByClassForStudent(UUID studentId, UUID classId) {
        log.info("Getting all learning progress for student {} in class {}", studentId, classId);

        // Validate enrollment
        enrollmentRepository.findByStudentIdAndClassId(studentId, classId)
                .orElseThrow(() -> new BusinessException("STUDENT_NOT_ENROLLED"));

        // Get all progress for student
        // Note: We need to filter by lectures in this class
        // This requires fetching lectures from course-management-service
        // For now, return all progress for student (can be filtered in controller/service layer)
        List<LearningProgress> progresses = learningProgressRepository.findByStudentId(studentId);

        return progresses.stream()
                .map(learningProgressMapper::toResponse)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public ClassProgressResponse getClassProgressSummaryForStudent(UUID studentId, UUID classId) {
        log.info("Getting class progress summary for student {} in class {}", studentId, classId);

        if(enrollmentRepository.existsByStudentIdAndClassId(studentId, classId)) throw new BusinessException("STUDENT_NOT_ENROLLED");

        // Get total study time
        Integer totalStudyTime = studyTimeRepository.getTotalStudyTimeByStudentAndClass(studentId, classId);
        if (totalStudyTime == null) {
            totalStudyTime = 0;
        }

        // Get all progress for student
        List<LearningProgress> progresses = learningProgressRepository.findByStudentId(studentId);

        // Calculate statistics
        long completedLectures = progresses.stream()
                .filter(p -> p.getCompletedAt() != null)
                .count();

        // Calculate overall progress percentage
        BigDecimal overallProgress = BigDecimal.ZERO;
        if (!progresses.isEmpty()) {
            BigDecimal sumProgress = progresses.stream()
                    .map(p -> p.getProgressPercentage() != null ? p.getProgressPercentage() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            overallProgress = sumProgress.divide(BigDecimal.valueOf(progresses.size()), 2, BigDecimal.ROUND_HALF_UP);
        }

        return ClassProgressResponse.builder()
                .studentId(studentId)
                .classId(classId)
                .overallProgressPercentage(overallProgress)
                .totalLectures(progresses.size())
                .completedLectures((int) completedLectures)
                .totalStudyTimeSeconds(totalStudyTime)
                .build();
    }

    @Override
    public BigDecimal calcProgress(LearningProgress progress) {
        LectureResponse lectureResponse = courseManagementClient.getLectureById(progress.getLectureId());
        return BigDecimal.valueOf(progress.getTotalTimeSpent())
                .divide(
                        BigDecimal.valueOf(lectureResponse.getEstimateTimeSpent()),
                        2,
                        RoundingMode.HALF_UP
                )
                .multiply(BigDecimal.valueOf(100))
                .min(BigDecimal.valueOf(100));

    }
}
