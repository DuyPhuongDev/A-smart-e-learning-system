package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.department.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByFacultyId(UUID facultyId);
    Page<Department> findByFacultyId(UUID facultyId, Pageable pageable);

    @Query("SELECT d FROM Department d JOIN d.specializations s WHERE s.id = :id")
    Department findBySpecializationId(@Param("id") UUID id);
}

