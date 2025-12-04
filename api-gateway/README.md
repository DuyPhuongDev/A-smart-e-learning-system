# API Gateway - LMS Microservices

## 📋 Mục Lục

1. [Tổng Quan](#tổng-quan)
2. [Kiến Trúc](#kiến-trúc)
3. [Tính Năng](#tính-năng)
4. [Luồng Hoạt Động](#luồng-hoạt-động)
5. [Cấu Trúc Code](#cấu-trúc-code)
6. [Cấu Hình](#cấu-hình)
7. [API Routes](#api-routes)
8. [Security](#security)
9. [Troubleshooting](#troubleshooting)

---

## 🎯 Tổng Quan

API Gateway là **single entry point** cho tất cả các requests từ client đến hệ thống microservices. Gateway đóng vai trò như một **firewall** và **reverse proxy**, thực hiện:

- ✅ **JWT Authentication & Authorization** - Validate tất cả JWT tokens
- ✅ **Rate Limiting** - Bảo vệ hệ thống khỏi DDoS attacks
- ✅ **Request Routing** - Route requests đến đúng microservice
- ✅ **Request Logging** - Log tất cả requests và responses
- ✅ **CORS Handling** - Xử lý Cross-Origin Resource Sharing
- ✅ **Internal Endpoint Protection** - Chặn truy cập vào internal endpoints
- ✅ **User Context Injection** - Thêm user info vào headers cho downstream services

**Port:** `8080`  
**Technology Stack:** Spring Cloud Gateway, Spring Boot, Netflix Eureka, JWT

---

## 🏗️ Kiến Trúc

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser/Mobile)                   │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ HTTP/HTTPS Requests
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Port 8080)                      │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  FILTER CHAIN (Order of Execution)                      │  │
│  │                                                          │  │
│  │  1. RequestLoggingFilter (Order: -1000)                │  │
│  │     → Log incoming request                              │  │
│  │                                                          │  │
│  │  2. InternalEndpointBlockFilter (Order: -300)          │  │
│  │     → Block /api/*/internal/* endpoints                │  │
│  │                                                          │  │
│  │  3. RateLimitingFilter (Order: -200)                   │  │
│  │     → Limit 100 requests/second per client              │  │
│  │                                                          │  │
│  │  4. JwtAuthenticationFilter (Order: -100)              │  │
│  │     → Validate JWT token                                │  │
│  │     → Extract userId, email, role                       │  │
│  │     → Add X-User-Id, X-User-Email, X-Role headers     │  │
│  │                                                          │  │
│  │  5. CORS Filter (CorsConfig)                           │  │
│  │     → Handle CORS preflight requests                    │  │
│  │                                                          │  │
│  │  6. Route to Downstream Service                         │  │
│  └──────────────────────────────────────────────────────────┘  │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             │ Load Balanced Requests
                             │ (with user context headers)
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│ Auth Service  │  │ User Service  │  │ Other Services│
│  (Port 8081)  │  │  (Port 8082)  │  │               │
└───────────────┘  └───────────────┘  └───────────────┘
```

---

## ✨ Tính Năng

### 1. JWT Authentication & Authorization

**Mục đích:** Validate tất cả JWT tokens trước khi cho phép request vào hệ thống.

**File:** ```32:136:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/JwtAuthenticationFilter.java```

**Chức năng:**
- ✅ Validate JWT token signature và expiration
- ✅ Extract user information (userId, email, role) từ token
- ✅ Thêm user context vào request headers cho downstream services
- ✅ Block refresh tokens ở các endpoint không phải `/api/auth/refresh-token`
- ✅ Skip authentication cho public endpoints

**Headers được thêm vào request:**
- `X-User-Id`: UUID của user
- `X-User-Email`: Email của user  
- `X-Role`: Role của user (ADMIN, TEACHER, STUDENT, etc.)

### 2. Rate Limiting

**Mục đích:** Bảo vệ hệ thống khỏi DDoS attacks và abuse.

**File:** ```29:150:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/RateLimitingFilter.java```

**Chức năng:**
- ✅ Giới hạn số lượng requests per second (default: 100 req/sec)
- ✅ Rate limiting theo IP address hoặc User ID
- ✅ In-memory storage (có thể upgrade sang Redis)
- ✅ Tự động cleanup old buckets để tránh memory leak

**Client Identification:**
1. Ưu tiên: User ID từ authenticated request (`user:{userId}`)
2. Fallback: IP address từ `X-Forwarded-For` header
3. Fallback: IP address từ `request.getRemoteAddress()`

**Response khi vượt quá limit:**
- HTTP Status: `429 Too Many Requests`
- Header: `Retry-After: 1`

### 3. Request Logging

**Mục đích:** Log tất cả requests và responses để monitoring và debugging.

**File:** ```17:63:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/RequestLoggingFilter.java```

**Chức năng:**
- ✅ Log incoming requests với request ID, method, path, client IP
- ✅ Log responses với status code và duration
- ✅ Tính toán thời gian xử lý request

**Log Format:**
```
[request-id] --> GET /api/users/me from 192.168.1.1
[request-id] <-- GET /api/users/me 200 (45ms)
```

### 4. Internal Endpoint Protection

**Mục đích:** Chặn truy cập vào các internal endpoints từ bên ngoài.

**File:** ```28:82:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/InternalEndpointBlockFilter.java```

**Chức năng:**
- ✅ Block tất cả requests đến `/api/*/internal/*` endpoints
- ✅ Chỉ cho phép service-to-service communication trong cluster
- ✅ Trả về `403 Forbidden` cho external requests

**Pattern Matching:**
```java
Pattern.compile("^/api/.*/internal/.*")
Pattern.compile("^/internal/.*")
```

### 5. CORS Configuration

**Mục đích:** Xử lý Cross-Origin Resource Sharing cho frontend applications.

**File:** ```1:62:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/config/CorsConfig.java```

**Chức năng:**
- ✅ Cho phép requests từ các origins được config
- ✅ Expose custom headers (`X-User-Id`, `X-User-Email`) cho frontend
- ✅ Support credentials (cookies, authorization headers)
- ✅ Cache preflight responses

**Allowed Origins (Development):**
- `http://localhost:3000` (React)
- `http://localhost:5173` (Vite)
- `http://localhost:4200` (Angular)

### 6. Route Validation

**Mục đích:** Định nghĩa các endpoints không cần authentication.

**File:** ```14:73:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/RouteValidator.java```

**Public Endpoints:**
- `/api/auth/login`
- `/api/auth/refresh-token`
- `/api/auth/forgot-password`
- `/api/auth/reset-password`
- `/actuator/**`
- `/swagger-ui/**`
- `/v3/api-docs/**`

---

## 🔄 Luồng Hoạt Động

### Luồng 1: Public Request (Login)

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/login
     │ { email, password }
     ▼
┌─────────────────────────────────────────┐
│         API GATEWAY                      │
│                                         │
│  1. RequestLoggingFilter               │
│     → Log: [id] --> POST /api/auth/login│
│                                         │
│  2. InternalEndpointBlockFilter         │
│     → Check: not /internal/*          │
│                                         │
│  3. RateLimitingFilter                 │
│     → Check: < 100 req/sec?            │
│                                         │
│  4. JwtAuthenticationFilter            │
│     → RouteValidator.isPublicEndpoint() │
│     → Yes, skip authentication         │
│                                         │
│  5. Route to authentication-service    │
└────┬────────────────────────────────────┘
     │
     │ POST /api/auth/login
     │ { email, password }
     ▼
┌─────────────────────────────────────────┐
│    AUTHENTICATION SERVICE               │
│  → Validate credentials                │
│  → Generate JWT tokens                 │
│  → Return { accessToken, refreshToken } │
└────┬────────────────────────────────────┘
     │
     │ { accessToken, refreshToken, ... }
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- RouteValidator: ```53:55:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/JwtAuthenticationFilter.java```
- Public endpoints: ```20:36:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/RouteValidator.java```

### Luồng 2: Authenticated Request

```
┌─────────┐
│ Client  │
└────┬────┘
     │ GET /api/users/me
     │ Authorization: Bearer {accessToken}
     ▼
┌─────────────────────────────────────────┐
│         API GATEWAY                      │
│                                         │
│  1. RequestLoggingFilter               │
│     → Log: [id] --> GET /api/users/me  │
│                                         │
│  2. InternalEndpointBlockFilter         │
│     → Check: not /internal/*          │
│                                         │
│  3. RateLimitingFilter                 │
│     → Check: < 100 req/sec?            │
│                                         │
│  4. JwtAuthenticationFilter            │
│     a. RouteValidator: isPublic?       │
│        → No, requires auth             │
│     b. Extract token from header       │
│        → "Bearer {token}"              │
│     c. Validate token (JwtUtil)         │
│        → Check signature               │
│        → Check expiration              │
│     d. Check: isRefreshToken?         │
│        → No, it's access token         │
│     e. Extract claims:                 │
│        - userId: "123e4567-..."        │
│        - email: "user@example.com"     │
│        - role: "ADMIN"                 │
│     f. Add headers:                    │
│        - X-User-Id: "123e4567-..."    │
│        - X-User-Email: "user@..."     │
│        - X-Role: "ADMIN"              │
│                                         │
│  5. Route to user-management-service   │
└────┬────────────────────────────────────┘
     │
     │ GET /api/users/me
     │ X-User-Id: 123e4567-...
     │ X-User-Email: user@example.com
     │ X-Role: ADMIN
     ▼
┌─────────────────────────────────────────┐
│   USER-MANAGEMENT SERVICE                │
│  → UserContextFilter extracts headers   │
│  → UserContextHolder.setContext()       │
│  → Controller uses UserContextHolder     │
│  → Return user data                     │
└────┬────────────────────────────────────┘
     │
     │ { user data }
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- JWT Validation: ```73:77:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/JwtAuthenticationFilter.java```
- Extract claims: ```87:95:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/JwtAuthenticationFilter.java```

### Luồng 3: Refresh Token Request

```
┌─────────┐
│ Client  │
└────┬────┘
     │ POST /api/auth/refresh-token
     │ Authorization: Bearer {refreshToken}
     │ { refreshToken }
     ▼
┌─────────────────────────────────────────┐
│         API GATEWAY                      │
│                                         │
│  1. RequestLoggingFilter               │
│  2. InternalEndpointBlockFilter         │
│  3. RateLimitingFilter                 │
│                                         │
│  4. JwtAuthenticationFilter            │
│     → RouteValidator: isPublic?         │
│     → Yes, /api/auth/refresh-token     │
│     → Skip authentication               │
│                                         │
│  5. Route to authentication-service    │
└────┬────────────────────────────────────┘
     │
     │ POST /api/auth/refresh-token
     │ { refreshToken }
     ▼
┌─────────────────────────────────────────┐
│    AUTHENTICATION SERVICE               │
│  → Validate refresh token              │
│  → Generate new access token            │
│  → Return { accessToken, refreshToken } │
└────┬────────────────────────────────────┘
     │
     │ { accessToken, refreshToken }
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Lưu ý:** Refresh token endpoint là public endpoint, nhưng nếu có Authorization header với refresh token, Gateway sẽ validate nhưng không block vì đã check path.

**Code References:**
- Refresh token check: ```79:83:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/JwtAuthenticationFilter.java```

### Luồng 4: Rate Limit Exceeded

```
┌─────────┐
│ Client  │
└────┬────┘
     │ GET /api/users/me (Request #101 in 1 second)
     │ Authorization: Bearer {token}
     ▼
┌─────────────────────────────────────────┐
│         API GATEWAY                      │
│                                         │
│  1. RequestLoggingFilter               │
│  2. InternalEndpointBlockFilter         │
│                                         │
│  3. RateLimitingFilter                 │
│     → Get client ID: "user:123..."     │
│     → Check bucket: 101 requests       │
│     → Limit: 100 req/sec               │
│     → EXCEEDED!                         │
│                                         │
│  4. Return 429 Too Many Requests       │
└────┬────────────────────────────────────┘
     │
     │ HTTP 429
     │ { "error": "Too Many Requests", ... }
     │ Retry-After: 1
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Rate limiting logic: ```91:103:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/RateLimitingFilter.java```

### Luồng 5: Invalid Token

```
┌─────────┐
│ Client  │
└────┬────┘
     │ GET /api/users/me
     │ Authorization: Bearer {invalid-token}
     ▼
┌─────────────────────────────────────────┐
│         API GATEWAY                      │
│                                         │
│  1. RequestLoggingFilter               │
│  2. InternalEndpointBlockFilter         │
│  3. RateLimitingFilter                 │
│                                         │
│  4. JwtAuthenticationFilter            │
│     → RouteValidator: isPublic?         │
│     → No, requires auth                │
│     → Extract token                    │
│     → Validate token (JwtUtil)         │
│     → INVALID! (expired/signature)     │
│                                         │
│  5. Return 401 Unauthorized            │
└────┬────────────────────────────────────┘
     │
     │ HTTP 401
     │ { "error": "Unauthorized", 
     │   "message": "Invalid or expired token" }
     ▼
┌─────────┐
│ Client  │
└─────────┘
```

**Code References:**
- Token validation: ```38:57:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/util/JwtUtil.java```
- Error handling: ```116:132:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/JwtAuthenticationFilter.java```

---

## 📁 Cấu Trúc Code

```
api-gateway/
├── src/main/java/com/hcmut/lms/gateway/
│   ├── ApiGatewayApplication.java          # Main application class
│   │
│   ├── config/
│   │   ├── CorsConfig.java                 # CORS configuration
│   │   └── RateLimitCleanupScheduler.java  # Cleanup scheduler for rate limit buckets
│   │
│   ├── filter/
│   │   ├── InternalEndpointBlockFilter.java  # Block internal endpoints
│   │   ├── JwtAuthenticationFilter.java      # JWT validation & user context
│   │   ├── RateLimitingFilter.java          # Rate limiting
│   │   ├── RequestLoggingFilter.java        # Request/response logging
│   │   └── RouteValidator.java              # Define public endpoints
│   │
│   └── util/
│       └── JwtUtil.java                     # JWT parsing utilities
│
└── src/main/resources/
    └── application.yml                       # Gateway configuration
```

### Component Details

#### 1. ApiGatewayApplication
**File:** ```1:15:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/ApiGatewayApplication.java```

Main application class với annotations:
- `@SpringBootApplication`: Enable Spring Boot auto-configuration
- `@EnableDiscoveryClient`: Enable Eureka service discovery

#### 2. JwtUtil
**File:** ```23:132:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/util/JwtUtil.java```

JWT utility class với các methods:
- `validateToken(String token)`: Validate JWT signature và expiration
- `extractAllClaims(String token)`: Extract tất cả claims từ token
- `extractUserId(String token)`: Extract user ID
- `extractEmail(String token)`: Extract email
- `extractRole(String token)`: Extract role
- `isRefreshToken(String token)`: Check nếu là refresh token
- `isTokenExpired(String token)`: Check nếu token đã expired

#### 3. Filter Order

Filters được thực hiện theo thứ tự (Order value):

| Filter | Order | Mục đích |
|--------|-------|----------|
| RequestLoggingFilter | -1000 | Log requests đầu tiên để có accurate timing |
| InternalEndpointBlockFilter | -300 | Block internal endpoints sớm |
| RateLimitingFilter | -200 | Rate limiting trước authentication |
| JwtAuthenticationFilter | -100 | Authentication và authorization |
| CORS Filter | Default | Xử lý CORS preflight |

---

## ⚙️ Cấu Hình

### Application Configuration

**File:** ```1:110:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/resources/application.yml```

#### Server Configuration
```yaml
server:
  port: 8080
```

#### Spring Cloud Gateway Routes
```yaml
spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: authentication-service
          uri: lb://authentication-service
          predicates:
            - Path=/api/auth/**
```

**Route Configuration:**
- `lb://` prefix: Load balancing qua Eureka
- `Path` predicate: Match request path
- Routes được định nghĩa cho từng service

#### JWT Configuration
```yaml
jwt:
  secret: ${JWT_SECRET:default-secret}
```

**⚠️ QUAN TRỌNG:** `jwt.secret` **PHẢI GIỐNG** với `authentication-service`. Nếu khác nhau, Gateway sẽ không thể validate tokens.

#### Rate Limiting Configuration
```yaml
rate-limit:
  enabled: true
  requests-per-second: 100
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

#### Logging Configuration
```yaml
logging:
  level:
    com.hcmut.lms.gateway: DEBUG
    org.springframework.cloud.gateway: INFO
```

### Environment Variables

```bash
# JWT Secret (REQUIRED - must match authentication-service)
JWT_SECRET=your-secret-key-here

# Eureka Server (optional, default: localhost:8761)
EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
```

---

## 🛣️ API Routes

### Route Mapping

| Path Pattern | Target Service | Description |
|--------------|----------------|-------------|
| `/api/auth/**` | `authentication-service` | Authentication endpoints |
| `/api/users/**` | `user-management-service` | User management endpoints |
| `/api/courses/**` | `course-management-service` | Course management endpoints |
| `/api/assessments/**` | `assessment-management-service` | Assessment management endpoints |
| `/api/delivery/**` | `course-delivery-service` | Course delivery endpoints |
| `/api/execution/**` | `assessment-execution-service` | Assessment execution endpoints |
| `/api/personalization/**` | `student-personalization-service` | Personalization endpoints |
| `/api/tracking/**` | `tracking-service` | Tracking endpoints |
| `/api/enrollment/**` | `enrollment-service` | Enrollment endpoints |
| `/api/notifications/**` | `notification-service` | Notification endpoints |
| `/api/communication/**` | `communication-service` | Communication endpoints |
| `/api/analytics/**` | `analytics-reporting-service` | Analytics endpoints |

### Public Endpoints (No Authentication Required)

- `POST /api/auth/login`
- `POST /api/auth/refresh-token`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `GET /actuator/**`
- `GET /swagger-ui/**`
- `GET /v3/api-docs/**`

### Protected Endpoints (Authentication Required)

Tất cả các endpoints khác đều yêu cầu JWT token trong header:
```
Authorization: Bearer {accessToken}
```

### Blocked Endpoints (403 Forbidden)

- `/api/*/internal/**` - Internal service-to-service endpoints

---

## 🔒 Security

### JWT Token Validation

Gateway validate JWT tokens với các checks:

1. **Signature Validation**: Verify token được sign bởi đúng secret key
2. **Expiration Check**: Verify token chưa expired
3. **Format Validation**: Verify token format đúng
4. **Token Type Check**: Block refresh tokens ở non-refresh endpoints

**Code:** ```38:57:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/util/JwtUtil.java```

### Rate Limiting

- **Limit**: 100 requests/second per client
- **Client Identification**: User ID (if authenticated) hoặc IP address
- **Response**: `429 Too Many Requests` với `Retry-After` header

**Code:** ```91:103:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/RateLimitingFilter.java```

### Internal Endpoint Protection

- Block tất cả requests đến `/api/*/internal/**`
- Chỉ cho phép service-to-service communication trong cluster
- Response: `403 Forbidden`

**Code:** ```38:54:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/filter/InternalEndpointBlockFilter.java```

### CORS Security

- Chỉ cho phép requests từ các origins được config
- Support credentials (cookies, authorization headers)
- Expose custom headers cho frontend

**Code:** ```1:62:d:/Materials/Capstone Project/Capstone_Project_LMS-HCMUT_BE/api-gateway/src/main/java/com/hcmut/lms/gateway/config/CorsConfig.java```

---

## 🐛 Troubleshooting

### Vấn Đề 1: JWT Validation Fails

**Triệu chứng:**
```
Invalid JWT signature
```

**Nguyên nhân:**
- JWT secret không khớp giữa Gateway và Authentication Service

**Giải pháp:**
1. Kiểm tra `jwt.secret` trong `application.yml` của cả Gateway và Auth Service
2. Đảm bảo cả hai dùng cùng giá trị
3. Restart cả hai services

### Vấn Đề 2: Rate Limit Exceeded

**Triệu chứng:**
```
HTTP 429 Too Many Requests
```

**Nguyên nhân:**
- Vượt quá 100 requests/second

**Giải pháp:**
1. Đợi 1 giây trước khi retry
2. Tăng `rate-limit.requests-per-second` trong `application.yml`
3. Hoặc disable rate limiting: `rate-limit.enabled: false`

### Vấn Đề 3: Service Not Found

**Triệu chứng:**
```
503 Service Unavailable
```

**Nguyên nhân:**
- Service chưa register với Eureka
- Service name không đúng

**Giải pháp:**
1. Kiểm tra service đã register với Eureka chưa
2. Kiểm tra service name trong route config (`lb://service-name`)
3. Kiểm tra Eureka server đang chạy

### Vấn Đề 4: CORS Error

**Triệu chứng:**
```
CORS policy: No 'Access-Control-Allow-Origin' header
```

**Nguyên nhân:**
- Frontend origin không được config trong CORS

**Giải pháp:**
1. Thêm frontend origin vào `CorsConfig.java`
2. Restart Gateway

### Vấn Đề 5: Internal Endpoint Blocked

**Triệu chứng:**
```
HTTP 403 Forbidden - This endpoint is not accessible
```

**Nguyên nhân:**
- Đang cố truy cập internal endpoint từ bên ngoài

**Giải pháp:**
- Internal endpoints chỉ có thể truy cập từ trong cluster
- Sử dụng service-to-service communication (không qua Gateway)

---

## 📊 Monitoring & Logging

### Request Logging

Gateway log tất cả requests với format:
```
[request-id] --> METHOD PATH from CLIENT_IP
[request-id] <-- METHOD PATH STATUS_CODE (DURATION_MS)
```

**Example:**
```
[abc123] --> GET /api/users/me from 192.168.1.1
[abc123] <-- GET /api/users/me 200 (45ms)
```

### Log Levels

- `DEBUG`: Chi tiết về filter processing
- `INFO`: Request/response logging
- `WARN`: Authentication failures, rate limit exceeded
- `ERROR`: Unexpected errors

### Actuator Endpoints

Gateway expose Spring Boot Actuator endpoints:
- `/actuator/health` - Health check
- `/actuator/info` - Application info
- `/actuator/metrics` - Metrics (nếu enabled)

---

## 🚀 Deployment

### Build

```bash
mvn clean package
```

### Run Locally

```bash
java -jar target/api-gateway-1.0.0-SNAPSHOT.jar
```

### Docker

```bash
docker build -t api-gateway .
docker run -p 8080:8080 \
  -e JWT_SECRET=your-secret \
  -e EUREKA_SERVER_URL=http://eureka:8761/eureka/ \
  api-gateway
```

### Environment Variables

```bash
# Required
JWT_SECRET=your-secret-key

# Optional
EUREKA_SERVER_URL=http://eureka:8761/eureka/
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_SECOND=100
```

---

## 📝 Best Practices

1. **JWT Secret**: Luôn dùng environment variable, không hardcode trong code
2. **Rate Limiting**: Tăng limit cho production nếu cần
3. **Logging**: Giảm log level trong production (INFO thay vì DEBUG)
4. **CORS**: Chỉ allow các origins cần thiết
5. **Monitoring**: Setup monitoring cho Gateway metrics
6. **Health Checks**: Sử dụng `/actuator/health` cho load balancer

---

## 🔗 Related Documentation

- [Architecture Overview](../ARCHITECTURE.md)
- [Authentication Service](../authentication-service/README.md)
- [User Management Service](../user-management-service/README.md)

---

## 📞 Support

Nếu có vấn đề, kiểm tra:
1. Logs trong console
2. Eureka dashboard để xem services đã register chưa
3. Network connectivity giữa Gateway và services
4. JWT secret configuration

---

**Version:** 1.0.0  
**Last Updated:** 2024

