package com.hcmut.lms.learning.entity.progress;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table(name = "learning_progresses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"student_id", "lecture_id"})
})
@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningProgress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "lecture_id")
    private UUID lectureId;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "current_position")
    private Integer currentPosition;

    @Column(name = "progress_percentage", precision = 5, scale = 2)
    private BigDecimal progressPercentage;

    @Column(name = "total_time_spent")
    private Integer totalTimeSpent;

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "position_unit")
    @Enumerated(EnumType.STRING)
    private PositionUnit positionUnit;
}
