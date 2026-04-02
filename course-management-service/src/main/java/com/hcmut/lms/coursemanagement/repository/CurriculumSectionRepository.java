package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CurriculumSectionRepository extends JpaRepository<CurriculumSection, UUID> {

    @Query("SELECT cs FROM CurriculumSection cs " +
           "WHERE cs.curriculum.id.code = :code " +
           "AND cs.curriculum.id.specializationId = :specializationId " +
           "AND cs.curriculum.id.intakeYearId = :intakeYearId " +
           "ORDER BY cs.displayOrder")
    List<CurriculumSection> findByCurriculumId(
        @Param("code") String code,
        @Param("specializationId") UUID specializationId,
        @Param("intakeYearId") UUID intakeYearId
    );

    @Query("SELECT DISTINCT cs FROM CurriculumSection cs " +
           "LEFT JOIN FETCH cs.curriculumSubjects csub " +
           "LEFT JOIN FETCH csub.subject s " +
           "WHERE cs.curriculum.id.specializationId = :specializationId " +
           "AND cs.curriculum.id.intakeYearId = :intakeYearId " +
           "ORDER BY cs.displayOrder, csub.displayOrder")
    List<CurriculumSection> findBySpecializationAndIntakeYearWithSubjects(
        @Param("specializationId") UUID specializationId,
        @Param("intakeYearId") UUID intakeYearId
    );
}

