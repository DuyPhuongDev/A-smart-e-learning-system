package com.hcmut.lms.authentication.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public String hashToken(String token) {
        // Hash token để lưu vào database (không phải password hash)
        return passwordEncoder.encode(token);
    }
    
    public boolean matchesToken(String rawToken, String hashedToken) {
        return passwordEncoder.matches(rawToken, hashedToken);
    }
}

