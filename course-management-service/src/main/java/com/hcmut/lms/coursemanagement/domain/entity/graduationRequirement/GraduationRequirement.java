package com.hcmut.lms.coursemanagement.domain.entity.graduationRequirement;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "graduation_requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraduationRequirement extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "code")
    private String code;
    
    @Column(name = "thresh_hold")
    private String threshHold;
    
    @OneToMany(mappedBy = "graduationRequirement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GraduationRequirementCurriculum> curriculums = new ArrayList<>();
}

