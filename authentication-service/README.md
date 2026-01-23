# Authentication Service - LMS Microservices

## 📋 Mục Lục

1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc](#kiến-trúc)
3. [Tính Năng](#tính-năng)
4. [Luồng Hoạt Động](#luồng-hoạt-động)
5. [Cấu Trúc Code](#cấu-trúc-code)
6. [API Endpoints](#api-endpoints)
7. [Database Schema](#database-schema)
8. [Security](#security)
9. [Cấu Hình](#cấu-hình)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 Tổng Quan

Authentication Service là **core service** chịu trách nhiệm quản lý authentication và authorization trong hệ thống LMS. Service này:

- ✅ **User Authentication** - Xác thực user credentials (email/password)
- ✅ **JWT Token Generation** - Tạo access tokens và refresh tokens với role information
- ✅ **Token Management** - Quản lý refresh tokens, password reset tokens
- ✅ **Account Security** - Lock/unlock accounts, failed login tracking
- ✅ **Password Management** - Change password, reset password, forgot password
- ✅ **Service Integration** - Giao tiếp với user-management-service để lấy role

**Port:** `8081`  
**Technology Stack:** Spring Boot, Spring Security, JWT, PostgreSQL, Feign Client, Eureka

---

## 🏗️ Kiến Trúc

```
┌─────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Port 8080)                   │
│  → Routes requests to /api/auth/**                          │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ HTTP Request
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│          AUTHENTICATION SERVICE (Port 8081)                  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Controllers                                         │  │
│  │  - AuthController (Public endpoints)                 │  │
│  │  - InternalAuthController (Service-to-service)      │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Services                                            │  │
│  │  - AuthService (Login, logout, token management)    │  │
│  │  - InternalAuthService (Credential management)     │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Utilities                                           │  │
│  │  - JwtUtil (Token generation & parsing)              │  │
│  │  - PasswordUtil (BCrypt hashing)                    │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Repositories                                        │  │
│  │  - UserCredentialsRepository                         │  │
│  │  - RefreshTokenRepository                            │  │
│  │  - PasswordResetTokenRepository                      │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────┘
                             │
                             │ Feign Client
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│         USER-MANAGEMENT SERVICE (Port 8082)                  │
│  → Get user role for JWT token generation                   │
└─────────────────────────────────────────────────────────────┘
```

---

## ✨ Tính Năng

### 1. User Authentication (Login)

**Mục đích:** Xác thực user credentials và cấp phát JWT tokens.

**File:** ```44:106:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

**Flow:**
1. Tìm `UserCredentials` theo email
2. Kiểm tra account lock status
3. Validate password với BCrypt
4. Xử lý failed login attempts (lock account nếu cần)
5. Lấy user role từ user-management-service
6. Generate access token và refresh token với role
7. Lưu refresh token vào database (hashed)
8. Return tokens cho client

**Account Lock Mechanism:**
- Sau `max-failed-attempts` (default: 5) lần đăng nhập sai, account bị lock
- Lock duration: `lockout-duration-minutes` (default: 30 phút)
- Tự động unlock sau khi hết thời gian

**Code:** ```108:119:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### 2. Token Refresh

**Mục đích:** Refresh access token mới khi access token hết hạn.

**File:** ```139:173:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

**Flow:**
1. Hash refresh token và tìm trong database
2. Kiểm tra token đã bị revoke chưa
3. Kiểm tra token đã expired chưa
4. Lấy user credentials
5. Lấy latest role (role có thể đã thay đổi)
6. Generate access token mới với role mới
7. Return access token mới (refresh token giữ nguyên)

**Lưu ý:** Refresh token được giữ nguyên, chỉ access token được refresh.

### 3. Logout

**Mục đích:** Revoke refresh token để đăng xuất user.

**File:** ```121:137:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

**Flow:**
1. Hash refresh token và tìm trong database
2. Kiểm tra token đã bị revoke chưa
3. Set `isRevoked = true` và `revokedAt = now()`
4. Lưu vào database

**Lưu ý:** Access tokens không thể revoke (stateless), nhưng refresh tokens có thể revoke.

### 4. Password Management

#### Change Password
**File:** ```215:234:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

- Validate current password
- Hash new password với BCrypt
- Update password hash
- **Revoke tất cả refresh tokens** (force re-login)

#### Forgot Password
**File:** ```236:260:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

- Generate password reset token (UUID)
- Hash token và lưu vào database
- Expiration: 1 hour
- **TODO:** Send email với reset token (hiện tại chỉ log)

#### Reset Password
**File:** ```262:293:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

- Validate reset token (hash và tìm trong DB)
- Kiểm tra token đã được sử dụng chưa
- Kiểm tra token đã expired chưa
- Update password
- Mark token as used
- **Revoke tất cả refresh tokens**

### 5. Internal Service Operations

**Mục đích:** Service-to-service communication cho credential management.

**File:** ```17:50:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/InternalAuthController.java```

**Endpoints:**
- `POST /api/auth/internal/create-credentials` - Tạo credentials khi user được tạo
- `POST /api/auth/internal/lock-account` - Lock account
- `POST /api/auth/internal/unlock-account` - Unlock account
- `POST /api/auth/internal/update-email` - Sync email từ user-management-service

**File:** ```23:114:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/InternalAuthServiceImpl.java```

### 6. JWT Token Generation

**File:** ```19:115:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/util/JwtUtil.java```

#### Access Token
```java
public String generateAccessToken(UUID userId, String email, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId.toString());
    claims.put("email", email);
    claims.put("role", role);
    return createToken(claims, userId.toString(), expiration);
}
```

**Claims trong Access Token:**
- `userId`: UUID của user
- `email`: Email của user
- `role`: Role name (ADMIN, TEACHER, STUDENT, etc.)
- `exp`: Expiration time (24 hours default)
- `iat`: Issued at time
- `jti`: JWT ID (unique identifier)

**Expiration:** 24 hours (configurable)

#### Refresh Token
```java
public String generateRefreshToken(UUID userId, String email, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userId.toString());
    claims.put("email", email);
    claims.put("role", role);
    claims.put("type", "refresh"); // Đánh dấu là refresh token
    return createToken(claims, userId.toString(), refreshExpiration);
}
```

**Khác biệt với Access Token:**
- Có thêm claim `type: "refresh"`
- Expiration time dài hơn (7 days vs 24 hours)
- Được lưu trong database (hashed) để có thể revoke

### 7. Password Hashing

**File:** ```8:29:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/util/PasswordUtil.java```

- **Algorithm:** BCrypt với strength 12
- **Password Hashing:** Hash passwords trước khi lưu vào database
- **Token Hashing:** Hash refresh tokens và password reset tokens trước khi lưu

**Security:**
- Passwords **không bao giờ** được lưu plaintext
- Tokens được hash để bảo vệ khỏi database breaches

---

## 🔄 Luồng Hoạt Động

### Luồng 1: Login Flow

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/login
     │ { email: "user@example.com", password: "password123" }
     ▼
┌─────────────────────────────────────────────────────────┐
│         API GATEWAY                                      │
│  → Route to authentication-service                      │
└────┬────────────────────────────────────────────────────┘
     │
     │ POST /api/auth/login
     │ { email, password }
     ▼
┌─────────────────────────────────────────────────────────┐
│    AUTHENTICATION SERVICE                                │
│                                                         │
│  1. AuthController.login()                              │
│     → Call authService.login()                         │
│                                                         │
│  2. AuthServiceImpl.login()                             │
│     a. Find UserCredentials by email                   │
│        → userCredentialsRepository.findByEmail()       │
│                                                         │
│     b. Check account lock status                       │
│        → if (isAccountLocked)                          │
│        → Check if lock expired                          │
│        → Unlock if expired                              │
│                                                         │
│     c. Validate password                                │
│        → passwordUtil.matches(password, passwordHash)   │
│        → If wrong: handleFailedLogin()                  │
│           - Increment failed attempts                  │
│           - Lock account if >= maxFailedAttempts        │
│                                                         │
│     d. Successful login                                 │
│        → Reset failed attempts = 0                      │
│        → Set lastLoginAt = now()                        │
│                                                         │
│     e. Get user role                                    │
│        → userManagementClient.getUserRole(userId)       │
│        → Fallback to "UNKNOWN" if service unavailable  │
│                                                         │
│     f. Generate tokens                                  │
│        → jwtUtil.generateAccessToken(userId, email, role)│
│        → jwtUtil.generateRefreshToken(userId, email, role)│
│                                                         │
│     g. Save refresh token                               │
│        → Hash refresh token                             │
│        → Save RefreshToken entity to DB                 │
│                                                         │
│     h. Return AuthResponse                              │
│        → { accessToken, refreshToken, tokenType,       │
│            userId, email }                              │
└────┬────────────────────────────────────────────────────┘
     │
     │ HTTP 200 OK
     │ {
     │   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     │   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     │   "tokenType": "Bearer",
     │   "userId": "123e4567-...",
     │   "email": "user@example.com"
     │ }
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Controller: ```27:31:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```
- Service: ```44:106:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```
- Get Role: ```205:213:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### Luồng 2: Refresh Token Flow

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/refresh-token
     │ { refreshToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }
     ▼
┌─────────────────────────────────────────────────────────┐
│         API GATEWAY                                      │
│  → Public endpoint, skip authentication                 │
└────┬────────────────────────────────────────────────────┘
     │
     │ POST /api/auth/refresh-token
     │ { refreshToken }
     ▼
┌─────────────────────────────────────────────────────────┐
│    AUTHENTICATION SERVICE                                │
│                                                         │
│  1. AuthController.refreshToken()                       │
│     → Call authService.refreshToken()                  │
│                                                         │
│  2. AuthServiceImpl.refreshToken()                      │
│     a. Hash refresh token                               │
│        → passwordUtil.hashToken(refreshToken)          │
│                                                         │
│     b. Find RefreshToken in database                   │
│        → refreshTokenRepository.findByTokenHash()      │
│        → Throw InvalidTokenException if not found      │
│                                                         │
│     c. Check if token revoked                           │
│        → if (isRevoked) throw InvalidTokenException    │
│                                                         │
│     d. Check if token expired                           │
│        → if (expiresAt < now()) throw TokenExpiredException│
│                                                         │
│     e. Get UserCredentials                              │
│        → userCredentialsRepository.findByUserId()       │
│                                                         │
│     f. Get latest role                                   │
│        → userManagementClient.getUserRole(userId)       │
│        → Role có thể đã thay đổi                        │
│                                                         │
│     g. Generate new access token                        │
│        → jwtUtil.generateAccessToken(userId, email, role)│
│                                                         │
│     h. Return AuthResponse                              │
│        → { newAccessToken, refreshToken (same), ... }   │
└────┬────────────────────────────────────────────────────┘
     │
     │ HTTP 200 OK
     │ {
     │   "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     │   "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     │   ...
     │ }
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Controller: ```33:37:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```
- Service: ```139:173:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### Luồng 3: Logout Flow

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/logout
     │ { refreshToken: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." }
     ▼
┌─────────────────────────────────────────────────────────┐
│         API GATEWAY                                      │
│  → Route to authentication-service                      │
└────┬────────────────────────────────────────────────────┘
     │
     │ POST /api/auth/logout
     │ { refreshToken }
     ▼
┌─────────────────────────────────────────────────────────┐
│    AUTHENTICATION SERVICE                                │
│                                                         │
│  1. AuthController.logout()                              │
│     → Call authService.logout(refreshToken)            │
│                                                         │
│  2. AuthServiceImpl.logout()                            │
│     a. Hash refresh token                               │
│        → passwordUtil.hashToken(refreshToken)           │
│                                                         │
│     b. Find RefreshToken in database                   │
│        → refreshTokenRepository.findByTokenHash()      │
│                                                         │
│     c. Check if already revoked                         │
│        → if (isRevoked) throw InvalidTokenException    │
│                                                         │
│     d. Revoke token                                     │
│        → setIsRevoked(true)                            │
│        → setRevokedAt(LocalDateTime.now())             │
│        → refreshTokenRepository.save()                 │
│                                                         │
│     e. Return 204 No Content                            │
└────┬────────────────────────────────────────────────────┘
     │
     │ HTTP 204 No Content
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Controller: ```46:50:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```
- Service: ```121:137:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### Luồng 4: Change Password Flow

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/change-password
     │ Authorization: Bearer {accessToken}
     │ {
     │   "currentPassword": "old123",
     │   "newPassword": "new456"
     │ }
     ▼
┌─────────────────────────────────────────────────────────┐
│         API GATEWAY                                      │
│  → Validate JWT, extract userId                         │
│  → Add X-User-Id header                                 │
└────┬────────────────────────────────────────────────────┘
     │
     │ POST /api/auth/change-password
     │ X-User-Id: {userId}
     │ { currentPassword, newPassword }
     ▼
┌─────────────────────────────────────────────────────────┐
│    AUTHENTICATION SERVICE                                │
│                                                         │
│  1. AuthController.changePassword()                      │
│     → Extract userId from JWT token                     │
│     → Call authService.changePassword(userId, request)  │
│                                                         │
│  2. AuthServiceImpl.changePassword()                    │
│     a. Find UserCredentials                             │
│        → userCredentialsRepository.findByUserId()       │
│                                                         │
│     b. Validate current password                        │
│        → passwordUtil.matches(currentPassword, hash)     │
│        → Throw InvalidCredentialsException if wrong     │
│                                                         │
│     c. Hash new password                                │
│        → passwordUtil.hashPassword(newPassword)         │
│                                                         │
│     d. Update password hash                              │
│        → setPasswordHash(newHash)                       │
│        → userCredentialsRepository.save()               │
│                                                         │
│     e. Revoke all refresh tokens                        │
│        → refreshTokenRepository.revokeAllByUserId()     │
│        → Force user to re-login                         │
│                                                         │
│     f. Return 204 No Content                            │
└────┬────────────────────────────────────────────────────┘
     │
     │ HTTP 204 No Content
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Controller: ```64:72:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```
- Service: ```215:234:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### Luồng 5: Forgot Password Flow

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/forgot-password
     │ { email: "user@example.com" }
     ▼
┌─────────────────────────────────────────────────────────┐
│         API GATEWAY                                      │
│  → Public endpoint, skip authentication                 │
└────┬────────────────────────────────────────────────────┘
     │
     │ POST /api/auth/forgot-password
     │ { email }
     ▼
┌─────────────────────────────────────────────────────────┐
│    AUTHENTICATION SERVICE                                │
│                                                         │
│  1. AuthController.forgotPassword()                      │
│     → Call authService.forgotPassword()                │
│                                                         │
│  2. AuthServiceImpl.forgotPassword()                    │
│     a. Find UserCredentials by email                   │
│        → userCredentialsRepository.findByEmail()       │
│                                                         │
│     b. Generate reset token                            │
│        → UUID.randomUUID().toString()                   │
│                                                         │
│     c. Hash reset token                                 │
│        → passwordUtil.hashToken(resetToken)             │
│                                                         │
│     d. Create PasswordResetToken entity                 │
│        → userId, tokenHash, expiresAt (1 hour)         │
│        → isUsed = false                                 │
│                                                         │
│     e. Save to database                                 │
│        → passwordResetTokenRepository.save()            │
│                                                         │
│     f. TODO: Send email with reset token                │
│        → Currently: log token (for development)         │
│                                                         │
│     g. Return 204 No Content                            │
└────┬────────────────────────────────────────────────────┘
     │
     │ HTTP 204 No Content
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Controller: ```52:56:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```
- Service: ```236:260:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### Luồng 6: Reset Password Flow

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/reset-password
     │ {
     │   "token": "reset-token-uuid",
     │   "newPassword": "newpassword123"
     │ }
     ▼
┌─────────────────────────────────────────────────────────┐
│         API GATEWAY                                      │
│  → Public endpoint, skip authentication                 │
└────┬────────────────────────────────────────────────────┘
     │
     │ POST /api/auth/reset-password
     │ { token, newPassword }
     ▼
┌─────────────────────────────────────────────────────────┐
│    AUTHENTICATION SERVICE                                │
│                                                         │
│  1. AuthController.resetPassword()                      │
│     → Call authService.resetPassword()                  │
│                                                         │
│  2. AuthServiceImpl.resetPassword()                      │
│     a. Hash reset token                                 │
│        → passwordUtil.hashToken(token)                 │
│                                                         │
│     b. Find PasswordResetToken                          │
│        → passwordResetTokenRepository.findByTokenHash()│
│        → Throw InvalidTokenException if not found      │
│                                                         │
│     c. Check if token already used                      │
│        → if (isUsed) throw InvalidTokenException       │
│                                                         │
│     d. Check if token expired                           │
│        → if (expiresAt < now()) throw TokenExpiredException│
│                                                         │
│     e. Get UserCredentials                              │
│        → userCredentialsRepository.findByUserId()       │
│                                                         │
│     f. Hash new password                                │
│        → passwordUtil.hashPassword(newPassword)        │
│                                                         │
│     g. Update password                                  │
│        → setPasswordHash(newHash)                       │
│        → userCredentialsRepository.save()               │
│                                                         │
│     h. Mark token as used                               │
│        → setIsUsed(true)                                │
│        → setUsedAt(LocalDateTime.now())                 │
│        → passwordResetTokenRepository.save()            │
│                                                         │
│     i. Revoke all refresh tokens                       │
│        → refreshTokenRepository.revokeAllByUserId()     │
│                                                         │
│     j. Return 204 No Content                            │
└────┬────────────────────────────────────────────────────┘
     │
     │ HTTP 204 No Content
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Controller: ```58:62:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```
- Service: ```262:293:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### Luồng 7: Internal - Create Credentials

```
┌─────────────────────────────────────────────────────────┐
│   USER-MANAGEMENT SERVICE                                │
│  → Create user, then create credentials                 │
└────┬────────────────────────────────────────────────────┘
     │
     │ POST /api/auth/internal/create-credentials
     │ (Service-to-service, không qua Gateway)
     │ {
     │   "userId": "123e4567-...",
     │   "email": "user@example.com",
     │   "password": "temporary-password"
     │ }
     ▼
┌─────────────────────────────────────────────────────────┐
│    AUTHENTICATION SERVICE                                │
│                                                         │
│  1. InternalAuthController.createCredentials()         │
│     → Call internalAuthService.createCredentials()     │
│                                                         │
│  2. InternalAuthServiceImpl.createCredentials()        │
│     a. Check if credentials already exist              │
│        → existsByUserId() or existsByEmail()            │
│        → Throw DuplicateResourceException if exists    │
│                                                         │
│     b. Hash password                                    │
│        → passwordUtil.hashPassword(password)            │
│                                                         │
│     c. Create UserCredentials entity                    │
│        → userId, email, passwordHash                    │
│        → isAccountLocked = false                       │
│        → failedLoginAttempts = 0                        │
│                                                         │
│     d. Save to database                                 │
│        → userCredentialsRepository.save()              │
│                                                         │
│     e. Return 201 Created                               │
└────┬────────────────────────────────────────────────────┘
     │
     │ HTTP 201 Created
     ▼
┌─────────────────────────────────────────────────────────┐
│   USER-MANAGEMENT SERVICE                                │
└─────────────────────────────────────────────────────────┘
```

**Code References:**
- Controller: ```24:28:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/InternalAuthController.java```
- Service: ```29:59:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/InternalAuthServiceImpl.java```

---

## 📁 Cấu Trúc Code

```
authentication-service/
├── src/main/java/com/hcmut/lms/authentication/
│   ├── AuthenticationServiceApplication.java
│   │   → Main application class
│   │   → @EnableFeignClients for service communication
│   │
│   ├── client/
│   │   ├── UserManagementClient.java
│   │   │   → Feign client để gọi user-management-service
│   │   │   → getUserRole(userId) - Lấy role cho JWT token
│   │   │
│   │   └── UserManagementClientFallback.java
│   │       → Fallback khi user-management-service unavailable
│   │       → Return "UNKNOWN" role
│   │
│   ├── config/
│   │   └── SecurityConfig.java
│   │       → Spring Security configuration
│   │       → Permit all /api/auth/** endpoints
│   │       → Stateless session management
│   │
│   ├── controller/
│   │   ├── AuthController.java
│   │   │   → Public endpoints cho client
│   │   │   → login, logout, refreshToken, changePassword, etc.
│   │   │
│   │   └── InternalAuthController.java
│   │       → Internal endpoints cho service-to-service
│   │       → createCredentials, lockAccount, unlockAccount, updateEmail
│   │
│   ├── exception/
│   │   ├── AccountLockedException.java
│   │   ├── AuthenticationException.java
│   │   ├── AuthenticationExceptionHandler.java
│   │   ├── DuplicateResourceException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── InvalidTokenException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── TokenExpiredException.java
│   │
│   ├── model/
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   │   ├── ChangePasswordRequest.java
│   │   │   │   ├── CreateCredentialsRequest.java
│   │   │   │   ├── ForgotPasswordRequest.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RefreshTokenRequest.java
│   │   │   │   ├── ResetPasswordRequest.java
│   │   │   │   ├── UpdateEmailRequest.java
│   │   │   │   └── UserIdRequest.java
│   │   │   │
│   │   │   └── response/
│   │   │       ├── AuthResponse.java
│   │   │       └── TokenValidationResponse.java
│   │   │
│   │   └── entity/
│   │       ├── PasswordResetToken.java
│   │       │   → Entity cho password reset tokens
│   │       │   → Fields: id, userId, tokenHash, expiresAt, isUsed, usedAt
│   │       │
│   │       ├── RefreshToken.java
│   │       │   → Entity cho refresh tokens
│   │       │   → Fields: id, userId, tokenHash, expiresAt, isRevoked, revokedAt
│   │       │
│   │       └── UserCredentials.java
│   │           → Entity cho user credentials
│   │           → Fields: id, userId, email, passwordHash, 
│   │                     isAccountLocked, failedLoginAttempts, 
│   │                     lockedUntil, lastLoginAt
│   │
│   ├── repository/
│   │   ├── PasswordResetTokenRepository.java
│   │   │   → JPA repository cho PasswordResetToken
│   │   │   → findByTokenHash()
│   │   │
│   │   ├── RefreshTokenRepository.java
│   │   │   → JPA repository cho RefreshToken
│   │   │   → findByTokenHash()
│   │   │   → revokeAllByUserId() - Revoke all tokens của user
│   │   │
│   │   └── UserCredentialsRepository.java
│   │       → JPA repository cho UserCredentials
│   │       → findByEmail(), findByUserId(), existsByEmail(), existsByUserId()
│   │
│   ├── service/
│   │   ├── AuthService.java
│   │   │   → Interface cho authentication operations
│   │   │
│   │   ├── impl/
│   │   │   ├── AuthServiceImpl.java
│   │   │   │   → Main authentication logic
│   │   │   │   → login(), logout(), refreshToken(), changePassword(), etc.
│   │   │   │
│   │   │   └── InternalAuthServiceImpl.java
│   │   │       → Internal service operations
│   │   │       → createCredentials(), lockAccount(), unlockAccount(), updateEmail()
│   │   │
│   │   └── InternalAuthService.java
│   │       → Interface cho internal operations
│   │
│   └── util/
│       ├── JwtUtil.java
│       │   → JWT token generation và parsing
│       │   → generateAccessToken(), generateRefreshToken()
│       │   → extractUserId(), extractEmail(), extractRole()
│       │
│       └── PasswordUtil.java
│           → Password và token hashing với BCrypt
│           → hashPassword(), matches(), hashToken()
│
└── src/main/resources/
    ├── application.yml
    │   → Service configuration
    │   → Database, JWT, Eureka, Feign config
    │
    └── db/
        └── authentication.sql
            → Database schema
```

---

## 🛣️ API Endpoints

### Public Endpoints (No Authentication Required)

#### 1. Login
**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "email": "user@example.com"
}
```

**Code:** ```27:31:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```

#### 2. Refresh Token
**Endpoint:** `POST /api/auth/refresh-token`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "userId": "123e4567-...",
  "email": "user@example.com"
}
```

**Code:** ```33:37:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```

#### 3. Forgot Password
**Endpoint:** `POST /api/auth/forgot-password`

**Request:**
```json
{
  "email": "user@example.com"
}
```

**Response:** `204 No Content`

**Code:** ```52:56:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```

#### 4. Reset Password
**Endpoint:** `POST /api/auth/reset-password`

**Request:**
```json
{
  "token": "reset-token-uuid",
  "newPassword": "newpassword123"
}
```

**Response:** `204 No Content`

**Code:** ```58:62:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```

### Protected Endpoints (Authentication Required)

#### 5. Logout
**Endpoint:** `POST /api/auth/logout`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `204 No Content`

**Code:** ```46:50:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```

#### 6. Change Password
**Endpoint:** `POST /api/auth/change-password`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Request:**
```json
{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword456"
}
```

**Response:** `204 No Content`

**Code:** ```64:72:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```

#### 7. Validate Token
**Endpoint:** `POST /api/auth/validate-token`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:** `200 OK`
```json
{
  "valid": true,
  "userId": "123e4567-...",
  "email": "user@example.com",
  "role": "ADMIN"
}
```

**Code:** ```39:44:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/AuthController.java```

### Internal Endpoints (Service-to-Service Only)

**⚠️ LƯU Ý:** Các endpoints này **KHÔNG** được expose qua API Gateway. Chỉ có thể truy cập từ trong cluster.

#### 8. Create Credentials
**Endpoint:** `POST /api/auth/internal/create-credentials`

**Request:**
```json
{
  "userId": "123e4567-...",
  "email": "user@example.com",
  "password": "temporary-password"
}
```

**Response:** `201 Created`

**Code:** ```24:28:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/InternalAuthController.java```

#### 9. Lock Account
**Endpoint:** `POST /api/auth/internal/lock-account`

**Request:**
```json
{
  "userId": "123e4567-..."
}
```

**Response:** `204 No Content`

**Code:** ```30:34:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/InternalAuthController.java```

#### 10. Unlock Account
**Endpoint:** `POST /api/auth/internal/unlock-account`

**Request:**
```json
{
  "userId": "123e4567-..."
}
```

**Response:** `204 No Content`

**Code:** ```36:40:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/InternalAuthController.java```

#### 11. Update Email
**Endpoint:** `POST /api/auth/internal/update-email`

**Request:**
```json
{
  "userId": "123e4567-...",
  "oldEmail": "old@example.com",
  "newEmail": "new@example.com"
}
```

**Response:** `204 No Content`

**Code:** ```42:46:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/controller/InternalAuthController.java```

---

## 🗄️ Database Schema

### UserCredentials Table

**Schema:** `authentication`  
**Table:** `user_credentials`

**File:** ```13:54:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/model/entity/UserCredentials.java```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | UUID | PK, Generated | Primary key |
| `user_id` | UUID | NOT NULL, UNIQUE | Reference to user in user-management-service |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | User email (login identifier) |
| `password_hash` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `is_account_locked` | BOOLEAN | DEFAULT false | Account lock status |
| `failed_login_attempts` | INTEGER | DEFAULT 0 | Number of failed login attempts |
| `locked_until` | TIMESTAMP | NULL | Lock expiration time |
| `last_login_at` | TIMESTAMP | NULL | Last successful login time |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL | Last update timestamp |

### RefreshToken Table

**Schema:** `authentication`  
**Table:** `refresh_tokens`

**File:** ```12:42:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/model/entity/RefreshToken.java```

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | UUID | PK, Generated | Primary key |
| `user_id` | UUID | NOT NULL | Reference to user |
| `token_hash` | VARCHAR(255) | NOT NULL, UNIQUE | BCrypt hashed refresh token |
| `expires_at` | TIMESTAMP | NOT NULL | Token expiration time (7 days) |
| `is_revoked` | BOOLEAN | DEFAULT false | Revocation status |
| `revoked_at` | TIMESTAMP | NULL | Revocation timestamp |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |

### PasswordResetToken Table

**Schema:** `authentication`  
**Table:** `password_reset_tokens`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | UUID | PK, Generated | Primary key |
| `user_id` | UUID | NOT NULL | Reference to user |
| `token_hash` | VARCHAR(255) | NOT NULL, UNIQUE | BCrypt hashed reset token |
| `expires_at` | TIMESTAMP | NOT NULL | Token expiration time (1 hour) |
| `is_used` | BOOLEAN | DEFAULT false | Usage status |
| `used_at` | TIMESTAMP | NULL | Usage timestamp |
| `created_at` | TIMESTAMP | NOT NULL | Creation timestamp |

---

## 🔒 Security

### 1. Password Security

**File:** ```8:29:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/util/PasswordUtil.java```

- **Algorithm:** BCrypt với strength 12
- **Hashing:** Tất cả passwords được hash trước khi lưu
- **Never Plaintext:** Passwords không bao giờ được lưu plaintext trong database

**Code:**
```java
private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

public String hashPassword(String plainPassword) {
    return passwordEncoder.encode(plainPassword);
}

public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
}
```

### 2. Token Security

#### Access Token
- **Stateless:** Không lưu trong database
- **Expiration:** 24 hours (configurable)
- **Signature:** HMAC-SHA256 với secret key
- **Claims:** userId, email, role, exp, iat, jti

#### Refresh Token
- **Stored:** Hashed trong database
- **Expiration:** 7 days
- **Revocable:** Có thể revoke khi logout hoặc change password
- **Type Claim:** Có `type: "refresh"` để phân biệt

**Code:** ```35:50:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/util/JwtUtil.java```

### 3. Account Lock Mechanism

**File:** ```108:119:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

**Flow:**
1. Track failed login attempts
2. Sau `max-failed-attempts` (default: 5) lần sai, lock account
3. Lock duration: `lockout-duration-minutes` (default: 30 phút)
4. Tự động unlock sau khi hết thời gian
5. Reset failed attempts khi login thành công

**Configuration:**
```yaml
auth:
  max-failed-attempts: 5
  lockout-duration-minutes: 30
```

### 4. Token Revocation

**Refresh Tokens:**
- Có thể revoke khi logout
- Tự động revoke tất cả tokens khi change password
- Tự động revoke khi reset password

**Code:**
- Logout: ```123:137:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```
- Change Password: ```230:231:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### 5. Service Communication Security

**Feign Client:**
- Sử dụng Feign Client để gọi user-management-service
- Có fallback mechanism khi service unavailable
- Circuit breaker pattern (nếu config)

**File:** ```13:40:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/client/UserManagementClient.java```

---

## ⚙️ Cấu Hình

### Application Configuration

**File:** ```1:53:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/resources/application.yml```

#### Server Configuration
```yaml
server:
  port: 8081
```

#### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:lms_db}
    username: ${DB_USER:lms_user}
    password: ${DB_PASSWORD:lms_password}
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        default_schema: authentication
```

#### JWT Configuration
```yaml
jwt:
  secret: ${JWT_SECRET:default-secret}
  expiration: 86400000        # 24 hours in milliseconds
  refresh-expiration: 604800000  # 7 days in milliseconds
```

**⚠️ QUAN TRỌNG:** `jwt.secret` **PHẢI GIỐNG** với API Gateway để Gateway có thể validate tokens.

#### Authentication Configuration
```yaml
auth:
  max-failed-attempts: 5
  lockout-duration-minutes: 30
```

#### Eureka Configuration
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
```

#### Feign Client Configuration
```yaml
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 5000
```

### Environment Variables

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=lms_db
DB_USER=lms_user
DB_PASSWORD=lms_password

# JWT Secret (REQUIRED - must match API Gateway)
JWT_SECRET=your-secret-key-here

# Eureka Server (optional)
EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
```

---

## 🐛 Troubleshooting

### Vấn Đề 1: Cannot Connect to User Management Service

**Triệu chứng:**
```
Failed to get user role for {userId}: Connection refused
```

**Nguyên nhân:**
- User-management-service chưa start hoặc chưa register với Eureka
- Network connectivity issues

**Giải pháp:**
1. Kiểm tra user-management-service đang chạy
2. Kiểm tra Eureka registration
3. Service sẽ fallback về "UNKNOWN" role nếu không connect được

**Code:** ```205:213:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/authentication-service/src/main/java/com/hcmut/lms/authentication/service/impl/AuthServiceImpl.java```

### Vấn Đề 2: Account Locked

**Triệu chứng:**
```
Account is locked. Please try again after {timestamp}
```

**Nguyên nhân:**
- Quá nhiều lần đăng nhập sai (>= 5 lần)

**Giải pháp:**
1. Đợi đến khi lock hết hạn (30 phút default)
2. Hoặc unlock account qua internal endpoint: `POST /api/auth/internal/unlock-account`

### Vấn Đề 3: Invalid Refresh Token

**Triệu chứng:**
```
Invalid refresh token
```

**Nguyên nhân:**
- Token đã bị revoke (logout hoặc change password)
- Token đã expired
- Token không tồn tại trong database

**Giải pháp:**
- User cần login lại để nhận refresh token mới

### Vấn Đề 4: Password Reset Token Expired

**Triệu chứng:**
```
Reset token has expired
```

**Nguyên nhân:**
- Token đã hết hạn (1 hour expiration)

**Giải pháp:**
- Request password reset lại để nhận token mới

### Vấn Đề 5: Database Connection Issues

**Triệu chứng:**
```
Unable to acquire JDBC Connection
```

**Nguyên nhân:**
- Database không accessible
- Wrong credentials
- Connection pool exhausted

**Giải pháp:**
1. Kiểm tra database đang chạy
2. Kiểm tra connection string và credentials
3. Kiểm tra connection pool size

---

## 📊 Monitoring & Logging

### Logging

Service log các events quan trọng:

**Login Success:**
```
User {email} logged in successfully
```

**Login Failure:**
```
Account {email} locked after {attempts} failed attempts
```

**Token Refresh:**
```
Token refreshed for user {email}
```

**Password Change:**
```
Password changed for user {email}
```

**Account Lock/Unlock:**
```
Account locked for user {userId}
Account unlocked for user {userId}
```

### Health Checks

Service expose Spring Boot Actuator endpoints:
- `/actuator/health` - Health check
- `/actuator/info` - Application info

---

## 🚀 Deployment

### Build

```bash
mvn clean package
```

### Run Locally

```bash
java -jar target/authentication-service-1.0.0-SNAPSHOT.jar
```

### Docker

```bash
docker build -t authentication-service .
docker run -p 8081:8081 \
  -e JWT_SECRET=your-secret \
  -e DB_HOST=postgres \
  -e DB_USER=lms_user \
  -e DB_PASSWORD=lms_password \
  authentication-service
```

### Environment Variables

```bash
# Required
JWT_SECRET=your-secret-key
DB_HOST=localhost
DB_PORT=5432
DB_NAME=lms_db
DB_USER=lms_user
DB_PASSWORD=lms_password

# Optional
EUREKA_SERVER_URL=http://eureka:8761/eureka/
MAX_FAILED_ATTEMPTS=5
LOCKOUT_DURATION_MINUTES=30
```

---

## 📝 Best Practices

1. **JWT Secret**: Luôn dùng environment variable, không hardcode
2. **Password Hashing**: Luôn hash passwords với BCrypt trước khi lưu
3. **Token Storage**: Chỉ lưu refresh tokens (hashed), không lưu access tokens
4. **Error Messages**: Không expose thông tin nhạy cảm trong error messages
5. **Rate Limiting**: Rely on API Gateway for rate limiting
6. **Service Communication**: Sử dụng Feign Client với fallback cho resilience
7. **Transaction Management**: Sử dụng `@Transactional` cho các operations cần atomicity

---

## 🔗 Related Documentation

- [API Gateway README](../api-gateway/README.md)
- [User Management Service](../user-management-service/README.md)

---

## 📞 Support

Nếu có vấn đề, kiểm tra:
1. Logs trong console
2. Database connectivity
3. Eureka service registration
4. JWT secret configuration (phải match với Gateway)
5. User-management-service availability

---

**Version:** 1.0.0  
**Last Updated:** 2024

