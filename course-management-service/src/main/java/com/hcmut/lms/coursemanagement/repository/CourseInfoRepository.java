package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfo;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseInfoRepository extends JpaRepository<CourseInfo, CourseInfoId> {
    Optional<CourseInfo> findFirstByClassSection_Id(UUID classSectionId);
}
