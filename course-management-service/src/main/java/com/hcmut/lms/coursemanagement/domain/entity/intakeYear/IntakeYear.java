package com.hcmut.lms.coursemanagement.domain.entity.intakeYear;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "intake_years")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IntakeYear extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "start_year", unique = true, nullable = false)
    private Integer startYear;
    
    @Column(name = "name")
    private String name;
    
    @OneToMany(mappedBy = "intakeYear", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Curriculum> curriculums = new ArrayList<>();
}

