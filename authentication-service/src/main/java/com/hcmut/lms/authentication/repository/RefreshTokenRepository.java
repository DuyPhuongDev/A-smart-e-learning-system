package com.hcmut.lms.authentication.repository;

import com.hcmut.lms.authentication.model.entity.RefreshToken;
import com.hcmut.lms.authentication.model.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findByUserCredentials(UserCredentials userCredentials);
    List<RefreshToken> findByUserCredentialsAndIsRevokedFalse(UserCredentials userCredentials);
    List<RefreshToken> findByUserCredentials_UserId(UUID userId);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true, rt.revokedAt = :revokedAt WHERE rt.userCredentials.userId = :userId AND rt.isRevoked = false")
    int revokeAllByUserId(@Param("userId") UUID userId, @Param("revokedAt") LocalDateTime revokedAt);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);
}

