package com.hcmut.lms.usermanagement.repository;

import com.hcmut.lms.usermanagement.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    
    List<Permission> findByRoleId(UUID roleId);
    
    @Query("SELECT p FROM Permission p WHERE p.role.id = :roleId AND p.isAllowed = true")
    List<Permission> findAllowedPermissionsByRoleId(@Param("roleId") UUID roleId);
    
    Optional<Permission> findByRoleIdAndEndpointAndHttpMethod(UUID roleId, String endpoint, String httpMethod);
    
    void deleteByRoleId(UUID roleId);
    
    @Query("SELECT DISTINCT p FROM Permission p " +
           "JOIN p.role r " +
           "JOIN r.userRoles ur " +
           "WHERE ur.user.id = :userId AND p.isAllowed = true")
    List<Permission> findAllowedPermissionsByUserId(@Param("userId") UUID userId);
}

