package com.hcmut.lms.usermanagement.repository;

import com.hcmut.lms.usermanagement.model.entity.SystemService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SystemServiceRepository extends JpaRepository<SystemService, UUID> {
}

