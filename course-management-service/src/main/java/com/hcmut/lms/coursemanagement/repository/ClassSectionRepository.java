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
    Page<ClassSection> findByTeacherId(UUID teacherId, Pageable pageable);
    
    @EntityGraph(attributePaths = {"chapters"})
    @Query("SELECT DISTINCT cs FROM ClassSection cs LEFT JOIN FETCH cs.chapters WHERE cs.id = :id")
    Optional<ClassSection> findByIdWithChapters(@Param("id") UUID id);
    
    @Query("SELECT cs FROM ClassSection cs WHERE " +
           "(:semesterCode IS NULL OR cs.semester.semesterCode = :semesterCode) AND " +
           "(:teacherId IS NULL OR cs.teacherId = :teacherId)")
    Page<ClassSection> findByFilters(@Param("semesterCode") String semesterCode,
                                     @Param("teacherId") UUID teacherId,
                                     Pageable pageable);
}

