package com.hcmut.lms.authentication.exception;

import java.time.LocalDateTime;

public class AccountLockedException extends AuthenticationException {
    private final LocalDateTime lockedUntil;
    
    public AccountLockedException(String message, LocalDateTime lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }
    
    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }
}

