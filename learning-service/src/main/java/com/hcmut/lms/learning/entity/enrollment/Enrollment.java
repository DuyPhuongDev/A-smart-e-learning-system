package com.hcmut.lms.learning.entity.enrollment;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "class_id"})
})
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    @Column(name = "completion_time")
    private LocalDateTime completionTime;

    @Column(name = "final_grade")
    private Double finalGrade;

    @Column(name = "attempt_no")
    private Integer attemptNo;

    @Column(name = "progress_percentage")
    private Double progressPercentage;
}
