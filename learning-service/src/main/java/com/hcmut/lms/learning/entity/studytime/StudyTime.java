package com.hcmut.lms.learning.entity.studytime;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Entity to track study time sessions for students
 * Records each study session with duration
 */
@Entity
@Table(name = "study_times", indexes = {
    @Index(name = "idx_study_time_student", columnList = "student_id"),
    @Index(name = "idx_study_time_class", columnList = "class_id"),
    @Index(name = "idx_study_time_lecture", columnList = "lecture_id"),
    @Index(name = "idx_study_time_started_at", columnList = "started_at")
})
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyTime extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(name = "lecture_id", nullable = false)
    private UUID lectureId;

    /**
     * Duration in seconds
     */
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    /**
     * Start time of the study session
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * End time of the study session
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /**
     * Additional metadata (e.g., device type, platform) stored as JSONB
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}
