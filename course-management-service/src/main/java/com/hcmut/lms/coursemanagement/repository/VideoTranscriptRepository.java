package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoTranscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoTranscriptRepository extends JpaRepository<VideoTranscript, UUID> {

    /**
     * Find single transcript by video lecture (in case no segmentation)
     */
    Optional<VideoTranscript> findByVideoLectureId(UUID videoLectureId);

    /**
     * Check if transcript exists for video lecture
     */
    boolean existsByVideoLectureId(UUID videoLectureId);

    /**
     * Delete all transcripts for a video lecture
     */
    void deleteByVideoLectureId(UUID videoLectureId);

    /**
     * Get all transcripts for a video lecture, ordered by segment index
     */
    @Query("SELECT vt FROM VideoTranscript vt WHERE vt.videoLecture.id = :videoLectureId ORDER BY vt.segmentIndex ASC")
    List<VideoTranscript> findAllByVideoLectureIdOrderBySegmentIndex(@Param("videoLectureId") UUID videoLectureId);

    /**
     * Find transcripts within a time range for a specific video lecture
     * @param videoLectureId ID of the video lecture
     * @param startTime Start time in seconds
     * @param endTime End time in seconds
     * @return List of transcripts that overlap with the time range
     */
    @Query("SELECT vt FROM VideoTranscript vt WHERE vt.videoLecture.id = :videoLectureId " +
            "AND vt.startTimeSeconds < :endTime AND vt.endTimeSeconds > :startTime " +
            "ORDER BY vt.startTimeSeconds ASC")
    List<VideoTranscript> findTranscriptsInTimeRange(
            @Param("videoLectureId") UUID videoLectureId,
            @Param("startTime") Integer startTime,
            @Param("endTime") Integer endTime
    );

    /**
     * Find transcript by segment index
     */
    Optional<VideoTranscript> findByVideoLectureIdAndSegmentIndex(UUID videoLectureId, Integer segmentIndex);

    /**
     * Get the next segment after current time
     */
    @Query("SELECT vt FROM VideoTranscript vt WHERE vt.videoLecture.id = :videoLectureId " +
            "AND vt.startTimeSeconds >= :currentTime ORDER BY vt.startTimeSeconds ASC LIMIT 1")
    Optional<VideoTranscript> findNextSegmentAfterTime(
            @Param("videoLectureId") UUID videoLectureId,
            @Param("currentTime") Integer currentTime
    );

    /**
     * Count total segments for a video lecture
     */
    Long countByVideoLectureId(UUID videoLectureId);
}
