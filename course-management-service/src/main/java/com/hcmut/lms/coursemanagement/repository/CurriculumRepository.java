package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, CurriculumId> {
    List<Curriculum> findByIdSpecializationId(UUID specializationId);
    Page<Curriculum> findByIdSpecializationId(UUID specializationId, Pageable pageable);
    List<Curriculum> findByIdIntakeYearId(UUID intakeYearId);
    Page<Curriculum> findByIdIntakeYearId(UUID intakeYearId, Pageable pageable);

    @Query("SELECT c FROM Curriculum c " +
           "LEFT JOIN FETCH c.specialization " +
           "LEFT JOIN FETCH c.intakeYear " +
           "WHERE c.id.specializationId = :specializationId " +
           "AND c.id.intakeYearId = :intakeYearId")
    Optional<Curriculum> findBySpecializationIdAndIntakeYearIdWithDetails(
        @Param("specializationId") UUID specializationId,
        @Param("intakeYearId") UUID intakeYearId
    );
}

