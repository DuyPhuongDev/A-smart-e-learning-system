# LMS System Architecture

## Tổng Quan Kiến Trúc

Hệ thống LMS được xây dựng theo kiến trúc **Microservices** với **Mono-repo**, sử dụng Spring Boot và Spring Cloud.

## Sơ Đồ Kiến Trúc Tổng Thể

```
                                    ┌─────────────────┐
                                    │   Client Apps   │
                                    │ (Web, Mobile)   │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │   API Gateway   │
                                    │   Port: 8080    │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
        ┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
        │ Eureka Server    │    │  Config Server   │    │  Business        │
        │ (Discovery)      │    │  (Configuration) │    │  Services        │
        │ Port: 8761       │    │  Port: 8888      │    │  (12 services)   │
        └──────────────────┘    └──────────────────┘    └──────────────────┘
```

## Chi Tiết Microservices

### 1. Infrastructure Layer

#### Eureka Server (Service Discovery)
- **Chức năng**: Quản lý đăng ký và khám phá services
- **Port**: 8761
- **Dependencies**: Spring Cloud Netflix Eureka Server

#### API Gateway
- **Chức năng**: Routing, load balancing, authentication
- **Port**: 8080
- **Dependencies**: Spring Cloud Gateway, Eureka Client

#### Config Server
- **Chức năng**: Centralized configuration management
- **Port**: 8888
- **Dependencies**: Spring Cloud Config Server

### 2. Core Services

#### Authentication Service (Port: 8081)
**Chức năng chính:**
- Xác thực người dùng (Login/Register)
- Quản lý JWT tokens
- Validate và refresh tokens
- Phân quyền cơ bản

**API Endpoints:**
- `POST /api/auth/login`
- `POST /api/auth/register`
- `POST /api/auth/refresh-token`
- `POST /api/auth/validate-token`

#### User Management Service (Port: 8082)
**Chức năng chính:**
- CRUD operations cho user accounts
- Quản lý roles và permissions
- Import/Export user data
- Lock/Unlock accounts

**API Endpoints:**
- `GET /api/users`
- `POST /api/users`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`
- `POST /api/users/{id}/roles`

### 3. Course Related Services

#### Course Management Service (Port: 8083)
**Chức năng chính:**
- Tạo và quản lý khóa học
- Xây dựng cấu trúc khóa học (modules, topics)
- Upload và quản lý materials
- Thiết lập grading system
- Duyệt nội dung khóa học

**Tương tác với services khác:**
- User Management Service: Lấy thông tin instructor
- Enrollment Service: Đồng bộ thông tin khóa học
- Analytics Service: Gửi dữ liệu báo cáo

#### Course Delivery Service (Port: 8086)
**Chức năng chính:**
- Phân phối nội dung học tập
- Quản lý notes của student
- AI-assisted learning support
- Tạo quiz từ AI
- Q&A chatbot

**Tương tác với services khác:**
- Course Management Service: Lấy course content
- Tracking Service: Ghi nhận tiến độ học tập
- AI Services: Tích hợp AI features

#### Enrollment Service (Port: 8089)
**Chức năng chính:**
- Đăng ký khóa học
- Tìm kiếm và discovery courses
- Đánh giá khóa học
- Xem thông tin khóa học

**Tương tác với services khác:**
- Course Management Service: Lấy course info
- User Management Service: Validate students
- Notification Service: Thông báo đăng ký thành công

### 4. Assessment Services

#### Assessment Management Service (Port: 8084)
**Chức năng chính:**
- Tạo và quản lý assignments/quizzes
- Chấm điểm và feedback
- AI-assisted grading
- Thiết lập rubrics và scoring rules
- Duyệt nội dung assessment

**Tương tác với services khác:**
- Course Management Service: Liên kết với courses
- Assessment Execution Service: Đồng bộ assessment data
- User Management Service: Validate instructors

#### Assessment Execution Service (Port: 8085)
**Chức năng chính:**
- Thực thi quizzes/assignments
- Submit bài làm (quiz, essay, code)
- Execute và test code submissions
- Lấy kết quả và scores

**Tương tác với services khác:**
- Assessment Management Service: Lấy assessment data
- Tracking Service: Ghi nhận submission
- Notification Service: Thông báo kết quả

### 5. Student Services

#### Student Personalization Service (Port: 8087)
**Chức năng chính:**
- Quản lý learning goals
- Tạo personalized learning paths với AI
- Điều chỉnh learning path
- Xem academic schedule
- Tracking academic progress

**Tương tác với services khác:**
- Enrollment Service: Course enrollment data
- Tracking Service: Progress data
- Course Management Service: Available courses
- AI Services: Path recommendations

#### Tracking Service (Port: 8088)
**Chức năng chính:**
- Theo dõi learning progress
- Lưu trữ activity logs
- Tracking grades và submissions
- Personal dashboard
- Time tracking

**Tương tác với services khác:**
- Course Delivery Service: Learning activities
- Assessment Execution Service: Submissions
- Analytics Service: Aggregate data

### 6. Communication Services

#### Notification Service (Port: 8090)
**Chức năng chính:**
- Gửi notifications (in-app, email, push)
- Tạo và gửi announcements
- Quản lý notification preferences
- Mark notifications as read

**Technology:**
- Spring Boot Mail
- WebSocket (real-time notifications)

**Tương tác với services khác:**
- Tất cả services: Nhận notification requests
- User Management Service: User contact info

#### Communication Service (Port: 8091)
**Chức năng chính:**
- Direct messaging (1-1, groups)
- Discussion forums
- Real-time chat
- Forum posts và replies

**Technology:**
- Spring WebSocket
- STOMP protocol

**Tương tác với services khác:**
- User Management Service: User profiles
- Notification Service: Message notifications
- Course Management Service: Course forums

### 7. Analytics & Reporting Service (Port: 8092)

**Chức năng chính:**
- System-wide reports
- Course analytics
- Grading statistics
- User activity reports
- System logs management
- Export reports (PDF, Excel, CSV)
- Admin/Teacher dashboards

**Technology:**
- Apache POI (Excel)
- iText PDF
- Spring Data JPA (aggregations)

**Tương tác với services khác:**
- Tracking Service: Activity data
- Assessment Services: Grading data
- Course Services: Course data
- User Management Service: User data

## Data Flow Examples

### 1. Student Enrollment Flow
```
Student → API Gateway → Enrollment Service
                            ↓
                    Course Management Service (validate course)
                            ↓
                    User Management Service (validate student)
                            ↓
                    Notification Service (send confirmation)
```

### 2. Assignment Submission Flow
```
Student → API Gateway → Assessment Execution Service
                            ↓
                    Assessment Management Service (get assignment)
                            ↓
                    Tracking Service (log activity)
                            ↓
                    Notification Service (notify instructor)
```

### 3. Personalized Learning Path Flow
```
Student → API Gateway → Student Personalization Service
                            ↓
                    Enrollment Service (enrolled courses)
                            ↓
                    Tracking Service (progress data)
                            ↓
                    AI Service (generate recommendations)
```

## Communication Patterns

### 1. Synchronous Communication
- REST API calls giữa các services
- Sử dụng cho operations cần response ngay lập tức

### 2. Asynchronous Communication (Future)
- Message Queue (RabbitMQ/Kafka)
- Event-driven architecture
- Sử dụng cho notifications, background tasks

## Database Strategy

### Current (Development)
- Mỗi service có H2 in-memory database riêng
- Database per service pattern

### Future (Production)
- PostgreSQL hoặc MySQL
- Mỗi service có database schema riêng
- Shared database infrastructure

## Security Architecture

### API Gateway Level
- JWT token validation
- Rate limiting
- CORS configuration

### Service Level
- Spring Security
- Method-level security
- Inter-service authentication

## Scalability Considerations

### Horizontal Scaling
- Mỗi service có thể scale independently
- Load balancing qua Eureka + API Gateway

### Caching Strategy
- Redis cache (future)
- Application-level caching

### Database Optimization
- Read replicas
- Database sharding (nếu cần)

## Monitoring & Observability (Future)

### Metrics
- Spring Boot Actuator
- Prometheus + Grafana

### Logging
- Centralized logging (ELK Stack)
- Distributed tracing (Zipkin/Jaeger)

### Health Checks
- Actuator health endpoints
- Liveness và readiness probes

## Deployment Strategy

### Development
- Local development: Maven spring-boot:run
- Docker Compose for full stack

### Production (Future)
- Kubernetes deployment
- CI/CD pipeline
- Blue-green deployment

## Technology Stack Summary

| Category | Technology |
|----------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.0 |
| Cloud | Spring Cloud 2025.0.0 |
| Service Discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Security | Spring Security + JWT |
| Database (Dev) | H2 |
| Database (Prod) | PostgreSQL/MySQL |
| Build Tool | Maven |
| Messaging | WebSocket, STOMP |
| Email | Spring Mail |
| Reporting | Apache POI, iText PDF |

## Future Enhancements

1. **Message Queue Integration**
   - RabbitMQ hoặc Apache Kafka
   - Event-driven architecture

2. **Caching Layer**
   - Redis cache
   - Distributed caching

3. **AI Integration**
   - OpenAI API integration
   - Custom ML models

4. **File Storage**
   - AWS S3 hoặc MinIO
   - CDN integration

5. **Real-time Features**
   - Live video streaming
   - Real-time collaboration

6. **Advanced Analytics**
   - Machine learning predictions
   - Recommendation engine

7. **Mobile Support**
   - GraphQL API
   - Optimized mobile endpoints

