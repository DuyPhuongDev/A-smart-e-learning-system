package com.hcmut.lms.learning.entity.lecturenote;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "lecture_notes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LectureNote extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "lecture_id", nullable = false)
    private UUID lectureId;

    @Column(name = "lecture_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LectureType lectureType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_position", nullable = false)
    private Integer contentPosition;
}
