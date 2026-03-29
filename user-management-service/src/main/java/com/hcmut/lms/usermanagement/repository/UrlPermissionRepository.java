package com.hcmut.lms.usermanagement.repository;

import com.hcmut.lms.usermanagement.model.entity.UrlPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UrlPermissionRepository extends JpaRepository<UrlPermission, UUID> {
    List<UrlPermission> findByServiceFunctionId(UUID serviceFunctionId);
}

