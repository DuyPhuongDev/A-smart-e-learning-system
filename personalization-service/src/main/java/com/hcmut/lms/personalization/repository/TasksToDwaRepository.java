package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.occupationData.TasksToDwa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksToDwaRepository extends JpaRepository<TasksToDwa, java.util.UUID> {
  List<TasksToDwa> findByOnetsocCode(String occupationCode);
}
