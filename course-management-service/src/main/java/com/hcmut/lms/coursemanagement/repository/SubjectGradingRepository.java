package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.subject.GradingType;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGrading;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectGradingRepository extends JpaRepository<SubjectGrading, SubjectGradingId> {

    List<SubjectGrading> findBySubjectId(UUID subjectId);

    Optional<SubjectGrading> findBySubjectIdAndGrading_GradingType(UUID subjectId, GradingType gradingType);
}
