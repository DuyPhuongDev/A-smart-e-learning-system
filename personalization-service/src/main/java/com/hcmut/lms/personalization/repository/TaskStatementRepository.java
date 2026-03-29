package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.application.entity.TaskStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskStatementRepository extends JpaRepository<TaskStatement, java.util.UUID> {
    List<TaskStatement> findByOccupation_OnetsocCode(String occupationCode);
}
