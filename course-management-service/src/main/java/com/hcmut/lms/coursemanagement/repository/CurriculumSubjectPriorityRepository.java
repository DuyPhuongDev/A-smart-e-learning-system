package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubjectPriority;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubjectPriorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CurriculumSubjectPriorityRepository extends JpaRepository<CurriculumSubjectPriority, CurriculumSubjectPriorityId> {

    @Query("SELECT csp FROM CurriculumSubjectPriority csp " +
           "WHERE csp.subjectId = :subjectId " +
           "AND csp.curriculumSectionId = :curriculumSectionId " +
           "AND csp.curriculumSubjectId = :curriculumSubjectId")
    Optional<CurriculumSubjectPriority> findByCompositeId(
        @Param("subjectId") UUID subjectId,
        @Param("curriculumSectionId") UUID curriculumSectionId,
        @Param("curriculumSubjectId") Integer curriculumSubjectId
    );
}
