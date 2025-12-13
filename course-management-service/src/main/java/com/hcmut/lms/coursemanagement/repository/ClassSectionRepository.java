package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassSectionRepository extends JpaRepository<ClassSection, UUID> {
    List<ClassSection> findBySubjectId(UUID subjectId);
    Page<ClassSection> findBySubjectId(UUID subjectId, Pageable pageable);
    List<ClassSection> findBySemesterId(UUID semesterId);
    Page<ClassSection> findBySemesterId(UUID semesterId, Pageable pageable);
    List<ClassSection> findByTeacherId(UUID teacherId);
    Page<ClassSection> findByTeacherId(UUID teacherId, Pageable pageable);
    List<ClassSection> findByStatus(ClassStatus status);
    Page<ClassSection> findByStatus(ClassStatus status, Pageable pageable);
    
    @EntityGraph(attributePaths = {"chapters"})
    @Query("SELECT DISTINCT cs FROM ClassSection cs LEFT JOIN FETCH cs.chapters WHERE cs.id = :id")
    Optional<ClassSection> findByIdWithChapters(@Param("id") UUID id);
}

