package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubjectId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CurriculumSubjectRepository extends JpaRepository<CurriculumSubject, CurriculumSubjectId> {
    List<CurriculumSubject> findByIdCurriculumSectionId(UUID curriculumSectionId);

    /**
     * Load all CurriculumSubjects with subject eagerly.
     * Prerequisites and recommendations will be fetched lazily within @Transactional.
     * Used for building the prerequisite mapping for grade prediction dataset.
     */
    @Query("SELECT DISTINCT cs FROM CurriculumSubject cs " +
            "LEFT JOIN FETCH cs.subject")
    List<CurriculumSubject> findAllWithPrerequisitesAndRecommendations();
}

