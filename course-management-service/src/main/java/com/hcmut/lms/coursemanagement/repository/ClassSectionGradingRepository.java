package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSectionGrading;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSectionGradingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassSectionGradingRepository extends JpaRepository<ClassSectionGrading, ClassSectionGradingId> {

    List<ClassSectionGrading> findByClassSectionId(UUID classSectionId);

    Optional<ClassSectionGrading> findByClassSectionIdAndGradingId(UUID classSectionId, UUID gradingId);

    @Query("SELECT COALESCE(SUM(csg.weight), 0.0) FROM ClassSectionGrading csg WHERE csg.classSection.id = :classSectionId")
    Float sumWeightByClassSectionId(@Param("classSectionId") UUID classSectionId);

    boolean existsByClassSectionIdAndGradingId(UUID classSectionId, UUID gradingId);
}
