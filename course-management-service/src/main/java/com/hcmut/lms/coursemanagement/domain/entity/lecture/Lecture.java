package com.hcmut.lms.coursemanagement.domain.entity.lecture;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "lectures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Lecture extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "order_index")
    private Integer orderIndex;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "is_mandatory")
    private Boolean isMandatory;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "lecture_type")
    private LectureType lectureType;
    
    @Column(name = "completion_rate")
    private Float completionRate;
    
    @Column(name = "view_count")
    private Integer viewCount;
    
    @Column(name = "allow_preview")
    private Boolean allowPreview;
    
    @Column(name = "is_downloadable")
    private Boolean isDownloadable;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;
}

