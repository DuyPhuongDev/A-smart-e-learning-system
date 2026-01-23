package com.hcmut.lms.coursemanagement.domain.entity.lecture;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Entity for storing video transcript segments
 * Maps to video_transcripts table in course_management schema
 * Each transcript can have multiple segments with timing information
 */
@Entity
@Table(name = "video_transcripts", schema = "course_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VideoTranscript extends BaseEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_lecture_id", nullable = false)
    private VideoLecture videoLecture;
    
    /**
     * Transcript text content
     */
    @Column(name = "transcript_text", columnDefinition = "TEXT")
    private String transcriptText;

    /**
     * Language code of the transcript (e.g., 'en', 'vi')
     * Required field - NOT NULL
     */
    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    /**
     * Duration of the audio in seconds
     */
    @Column(name = "audio_duration")
    private Integer audioDuration;

    /**
     * Word count in the transcript
     */
    @Column(name = "word_count")
    private Integer wordCount;

    /**
     * Thời gian bắt đầu đoạn transcript này trong video (tính bằng giây)
     * Default value: 0, NOT NULL
     */
    @Column(name = "start_time_seconds", nullable = false)
    @Builder.Default
    private Integer startTimeSeconds = 0;

    /**
     * Thời gian kết thúc đoạn transcript này trong video (tính bằng giây)
     */
    @Column(name = "end_time_seconds")
    private Integer endTimeSeconds;

    /**
     * Chỉ số của đoạn/phần trong video (0, 1, 2, ...)
     * Giúp sắp xếp các đoạn transcript theo thứ tự
     */
    @Column(name = "segment_index")
    private Integer segmentIndex;
}
