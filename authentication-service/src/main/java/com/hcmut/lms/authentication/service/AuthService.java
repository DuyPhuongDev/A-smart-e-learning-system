package com.hcmut.lms.authentication.service;

import com.hcmut.lms.authentication.model.dto.request.*;
import com.hcmut.lms.authentication.model.dto.response.AuthResponse;
import com.hcmut.lms.authentication.model.dto.response.TokenValidationResponse;

import java.util.UUID;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    void logout(String refreshToken);
    AuthResponse refreshToken(RefreshTokenRequest request);
    TokenValidationResponse validateToken(String token);
    void changePassword(UUID userId, ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}

