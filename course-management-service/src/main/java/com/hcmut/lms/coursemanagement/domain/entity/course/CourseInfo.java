package com.hcmut.lms.coursemanagement.domain.entity.course;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "course_infos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CourseInfo extends BaseEntity {
    
    @EmbeddedId
    private CourseInfoId id;
    
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
    
    @Column(name = "status")
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classSectionId")
    @JoinColumn(name = "class_section_id")
    private ClassSection classSection;
}

