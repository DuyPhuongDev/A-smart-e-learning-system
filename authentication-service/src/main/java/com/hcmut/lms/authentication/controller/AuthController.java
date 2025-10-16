package com.hcmut.lms.authentication.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public String login() {
        // TODO: Implement login logic
        return "Login endpoint - To be implemented";
    }

    @PostMapping("/register")
    public String register() {
        // TODO: Implement registration logic
        return "Register endpoint - To be implemented";
    }

    @PostMapping("/refresh-token")
    public String refreshToken() {
        // TODO: Implement token refresh logic
        return "Refresh token endpoint - To be implemented";
    }

    @PostMapping("/validate-token")
    public Boolean validateToken() {
        // TODO: Implement token validation logic
        return true;
    }
}

