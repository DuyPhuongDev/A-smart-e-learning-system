package com.hcmut.lms.coursemanagement.domain.entity.chapter;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chapters",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"order_index", "class_section_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Chapter extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "order_index",  nullable = false)
    private Integer orderIndex;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ChapterStatus status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_section_id", nullable = false)
    private ClassSection classSection;
    
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Lecture> lectures = new ArrayList<>();
}

