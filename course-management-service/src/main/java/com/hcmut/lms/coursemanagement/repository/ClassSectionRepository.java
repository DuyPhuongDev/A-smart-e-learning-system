package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
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

       @EntityGraph(attributePaths = { "chapters" })
       @Query("SELECT DISTINCT cs FROM ClassSection cs LEFT JOIN FETCH cs.chapters WHERE cs.id = :id")
       Optional<ClassSection> findByIdWithChapters(@Param("id") UUID id);

       @Query("SELECT cs FROM ClassSection cs WHERE " +
                     "(:semesterCode IS NULL OR cs.semester.semesterCode = :semesterCode) AND " +
                     "(:teacherId IS NULL OR cs.teacherId = :teacherId)")
       Page<ClassSection> findByFilters(@Param("semesterCode") String semesterCode,
                     @Param("teacherId") UUID teacherId,
                     Pageable pageable);

       /**
        * Find class sections by list of IDs
        */
       @Query("SELECT cs FROM ClassSection cs " +
                     "LEFT JOIN FETCH cs.subject " +
                     "LEFT JOIN FETCH cs.semester " +
                     "WHERE cs.id IN :ids")
       List<ClassSection> findByIdIn(@Param("ids") List<UUID> ids);

       /**
        * Find class sections by list of IDs with filters (semester and search term)
        */
       @Query("SELECT cs FROM ClassSection cs " +
                     "LEFT JOIN FETCH cs.subject s " +
                     "LEFT JOIN FETCH cs.semester sem " +
                     "WHERE cs.id IN :ids " +
                     "AND (:semesterCode IS NULL OR sem.semesterCode = :semesterCode) " +
                     "AND (:searchTerm IS NULL OR LOWER(cs.sectionName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
                     "     OR LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
       List<ClassSection> findByIdInWithFilters(@Param("ids") List<UUID> ids,
                     @Param("semesterCode") String semesterCode,
                     @Param("searchTerm") String searchTerm);

       /**
        * Find class sections by semester ID and subject ID
        */
       @Query("SELECT cs FROM ClassSection cs " +
                     "LEFT JOIN FETCH cs.subject " +
                     "LEFT JOIN FETCH cs.semester " +
                     "WHERE cs.semester.id = :semesterId AND cs.subject.id = :subjectId")
       List<ClassSection> findBySemesterIdAndSubjectId(@Param("semesterId") UUID semesterId,
                     @Param("subjectId") UUID subjectId);

       /**
        * Find all class sections with subject and semester for export
        */
       @Query("SELECT cs FROM ClassSection cs " +
                     "LEFT JOIN FETCH cs.subject " +
                     "LEFT JOIN FETCH cs.semester")
       List<ClassSection> findAllWithSubjectAndSemester();

       /**
        * Find class sections by list of IDs with eager loading of subject, semester, academic year,
        * and curriculumSubjects (needed for extracting curriculumSectionId for dataset computation).
        */
       @Query("SELECT DISTINCT cs FROM ClassSection cs " +
                     "LEFT JOIN FETCH cs.subject s " +
                     "LEFT JOIN FETCH s.curriculumSubjects " +
                     "LEFT JOIN FETCH cs.semester sem " +
                     "LEFT JOIN FETCH sem.academicYear " +
                     "WHERE cs.id IN :ids")
       List<ClassSection> findByIdInWithAcademicYear(@Param("ids") List<UUID> ids);

       /**
        * Check if class code exists
        */
       boolean existsByCode(String code);
}
