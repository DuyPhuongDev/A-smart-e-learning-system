package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.studytime.StudyTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StudyTimeRepository extends JpaRepository<StudyTime, UUID> {

    /**
     * Find study times by student and class
     */
    List<StudyTime> findByStudentIdAndClassIdOrderByStartedAtDesc(UUID studentId, UUID classId);

    /**
     * Find study times by student, class and date range
     */
    @Query("SELECT st FROM StudyTime st WHERE st.studentId = :studentId " +
           "AND st.classId = :classId " +
           "AND st.startedAt >= :startDate AND st.startedAt < :endDate " +
           "ORDER BY st.startedAt DESC")
    List<StudyTime> findByStudentIdAndClassIdAndDateRange(
            @Param("studentId") UUID studentId,
            @Param("classId") UUID classId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get total study time in seconds for a student in a class
     */
    @Query("SELECT COALESCE(SUM(st.durationSeconds), 0) FROM StudyTime st " +
           "WHERE st.studentId = :studentId AND st.classId = :classId")
    Integer getTotalStudyTimeByStudentAndClass(@Param("studentId") UUID studentId, 
                                               @Param("classId") UUID classId);

    /**
     * Get total study time in seconds for a student in a lecture
     */
    @Query("SELECT COALESCE(SUM(st.durationSeconds), 0) FROM StudyTime st " +
           "WHERE st.studentId = :studentId AND st.lectureId = :lectureId")
    Integer getTotalStudyTimeByStudentAndLecture(@Param("studentId") UUID studentId, 
                                                 @Param("lectureId") UUID lectureId);

    /**
     * Get study time summary by date for a student in a class
     */
    @Query("SELECT DATE(st.startedAt) as date, " +
           "SUM(st.durationSeconds) as totalSeconds, " +
           "COUNT(st.id) as sessionCount " +
           "FROM StudyTime st " +
           "WHERE st.studentId = :studentId AND st.classId = :classId " +
           "AND st.startedAt >= :startDate AND st.startedAt < :endDate " +
           "GROUP BY DATE(st.startedAt) " +
           "ORDER BY date DESC")
    List<Object[]> getStudyTimeSummaryByDate(
            @Param("studentId") UUID studentId,
            @Param("classId") UUID classId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Count study sessions for a student in a class
     */
    long countByStudentIdAndClassId(UUID studentId, UUID classId);

    /**
     * Aggregate total spent seconds by lecture in a class.
     */
    @Query("""
            SELECT st.lectureId, COALESCE(SUM(st.durationSeconds), 0)
            FROM StudyTime st
            WHERE st.classId = :classId
            GROUP BY st.lectureId
            """)
    List<Object[]> sumDurationByClassGroupedByLecture(@Param("classId") UUID classId);

    /**
     * Count distinct students who spent time on each lecture in a class.
     */
    @Query("""
            SELECT st.lectureId, COUNT(DISTINCT st.studentId)
            FROM StudyTime st
            WHERE st.classId = :classId
              AND st.durationSeconds > 0
            GROUP BY st.lectureId
            """)
    List<Object[]> countDistinctStudentsByClassGroupedByLecture(@Param("classId") UUID classId);

    /**
     * Aggregate total spent seconds by student in a class.
     */
    @Query("""
            SELECT st.studentId, COALESCE(SUM(st.durationSeconds), 0)
            FROM StudyTime st
            WHERE st.classId = :classId
            GROUP BY st.studentId
            """)
    List<Object[]> sumDurationByClassGroupedByStudent(@Param("classId") UUID classId);
}
