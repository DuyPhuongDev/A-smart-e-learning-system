package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurriculumSubjectRepository extends JpaRepository<CurriculumSubject, CurriculumSubjectId> {
  /**
   * Load all CurriculumSubjects with subject eagerly.
   * Prerequisites and recommendations will be fetched lazily within @Transactional.
   * Used for building the prerequisite mapping for grade prediction dataset.
   */
  @Query("SELECT DISTINCT cs FROM CurriculumSubject cs " + "LEFT JOIN FETCH cs.subject")
  List<CurriculumSubject> findAllWithPrerequisitesAndRecommendations();


  @Query("SELECT cs FROM CurriculumSubject cs " + "LEFT JOIN FETCH cs.subject s " + "LEFT JOIN FETCH cs" +
      ".curriculumSection section " + "WHERE cs.id.subjectId = :subjectId " + "AND section.curriculum.id" +
      ".specializationId = :specializationId " + "AND section.curriculum.id.intakeYearId = :intakeYearId")
  Optional<CurriculumSubject> findBySubjectIdAndCurriculum(
      @Param("subjectId") UUID subjectId, @Param("specializationId") UUID specializationId,
      @Param("intakeYearId") UUID intakeYearId);

  @Query("SELECT cs FROM CurriculumSubject cs " + "LEFT JOIN FETCH cs.prerequisites p " + "LEFT JOIN FETCH p" +
      ".prerequisiteCurriculumSubject pcs " + "LEFT JOIN FETCH pcs.subject " + "WHERE cs.id.curriculumSectionId = " +
      ":curriculumSectionId " + "AND cs.id.subjectId = :subjectId " + "AND cs.id.id = :id")
  Optional<CurriculumSubject> findByIdWithPrerequisites(
      @Param("curriculumSectionId") UUID curriculumSectionId, @Param("subjectId") UUID subjectId,
      @Param("id") Integer id);

  @Query("SELECT cs FROM CurriculumSubject cs " + "LEFT JOIN FETCH cs.recommendations r " + "LEFT JOIN FETCH r" +
      ".recommendedCurriculumSubject rcs " + "LEFT JOIN FETCH rcs.subject " + "WHERE cs.id.curriculumSectionId = " +
      ":curriculumSectionId " + "AND cs.id.subjectId = :subjectId " + "AND cs.id.id = :id")
  Optional<CurriculumSubject> findByIdWithRecommendations(
      @Param("curriculumSectionId") UUID curriculumSectionId, @Param("subjectId") UUID subjectId,
      @Param("id") Integer id);

  @Query("SELECT cs FROM CurriculumSubject cs " + "LEFT JOIN FETCH cs.parallels p " + "LEFT JOIN FETCH p" +
      ".parallelCurriculumSubject pcs " + "LEFT JOIN FETCH pcs.subject " + "WHERE cs.id.curriculumSectionId = " +
      ":curriculumSectionId " + "AND cs.id.subjectId = :subjectId " + "AND cs.id.id = :id")
  Optional<CurriculumSubject> findByIdWithParallels(
      @Param("curriculumSectionId") UUID curriculumSectionId, @Param("subjectId") UUID subjectId,
      @Param("id") Integer id);

  @Query("SELECT DISTINCT cs FROM CurriculumSubject cs " + "LEFT JOIN FETCH cs.subject s " + "LEFT JOIN FETCH cs" +
      ".curriculumSection section " + "LEFT JOIN FETCH section.curriculum curriculum " + "LEFT JOIN FETCH cs" +
      ".prerequisites p " + "LEFT JOIN FETCH p.prerequisiteCurriculumSubject pcs " + "LEFT JOIN FETCH pcs.subject " + "WHERE curriculum.id.specializationId = :specializationId")
  List<CurriculumSubject> findBySpecializationIdWithPrerequisites(@Param("specializationId") UUID specializationId);
}

