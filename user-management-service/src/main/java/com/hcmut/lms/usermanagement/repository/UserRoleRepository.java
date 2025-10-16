package com.hcmut.lms.usermanagement.repository;

import com.hcmut.lms.usermanagement.model.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    
    List<UserRole> findByUserId(UUID userId);
    
    List<UserRole> findByRoleId(UUID roleId);
    
    Optional<UserRole> findByUserIdAndRoleId(UUID userId, UUID roleId);
    
    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);
    
    boolean existsByRoleId(UUID roleId);
    
    void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}

