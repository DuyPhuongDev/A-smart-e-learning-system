package com.hcmut.lms.coursemanagement.domain.entity.classSection;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "class_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClassSection extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "section_name")
    private String sectionName;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ClassStatus status;
    
    @Column(name = "is_official", nullable = false)
    private Boolean isOfficial;
    
    @Column(name = "teacher_id")
    private UUID teacherId;

    @Column(name = "max_student")
    private Integer maxStudents;

    @Column(name = "current_student")
    private Integer currentStudents;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "description")
    private String description;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private CourseLanguage language;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "intro_video")
    private String introVideo;

    @Column(name = "duration_hours")
    private Integer durationHours;

    @Column(name = "objective")
    private String objective;

    @Column(name = "topic")
    private String topic;

    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @OneToMany(mappedBy = "classSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Chapter> chapters = new ArrayList<>();
    
    @OneToMany(mappedBy = "classSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClassSectionGrading> classSectionGradings = new ArrayList<>();
}

