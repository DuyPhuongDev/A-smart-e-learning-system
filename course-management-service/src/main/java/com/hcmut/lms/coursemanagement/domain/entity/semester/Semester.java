package com.hcmut.lms.coursemanagement.domain.entity.semester;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.exception.DomainException;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "semesters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Semester extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "semester_code")
    private String semesterCode;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;
    
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClassSection> classSections = new ArrayList<>();

    // handle domain rule
    public void validateAgainstAcademicYear(AcademicYear year) {
        if (startDate.isAfter(endDate)){
            throw new DomainException("Start date cannot be after end date");
        }
        if (startDate.isBefore(year.getStartDate())) {
            throw new DomainException("Semester start date cannot be before academic year start date");
        }

        if (endDate.isAfter(year.getEndDate())) {
            throw new DomainException("Semester end date cannot be after academic year end date");
        }
    }


}

