package com.hcmut.lms.authentication.repository;

import com.hcmut.lms.authentication.model.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCredentialsRepository extends JpaRepository<UserCredentials, UUID> {
    Optional<UserCredentials> findByEmail(String email);
    Optional<UserCredentials> findByUserId(UUID userId);
    boolean existsByEmail(String email);
    boolean existsByUserId(UUID userId);
}

