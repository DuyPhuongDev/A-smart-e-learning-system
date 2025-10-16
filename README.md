# LMS Backend - HCMUT
## Learning Management System - Microservices Architecture

Dự án backend cho hệ thống quản lý học tập (LMS) của trường ĐHBK TPHCM, được xây dựng theo kiến trúc microservices với mono-repo.

## 🏗️ Kiến Trúc Hệ Thống

### Infrastructure Services
- **Eureka Server** (Port: 8761) - Service Discovery
- **API Gateway** (Port: 8080) - API Gateway & Routing
- **Config Server** (Port: 8888) - Centralized Configuration

### Business Services

#### Core Services
- **Authentication Service** (Port: 8081) - Xác thực và phân quyền
- **User Management Service** (Port: 8082) - Quản lý tài khoản người dùng

#### Course Services
- **Course Management Service** (Port: 8083) - Quản lý khóa học
- **Course Delivery Service** (Port: 8086) - Phân phối nội dung học tập
- **Enrollment Service** (Port: 8089) - Quản lý đăng ký khóa học

#### Assessment Services
- **Assessment Management Service** (Port: 8084) - Quản lý bài tập/kiểm tra
- **Assessment Execution Service** (Port: 8085) - Thực thi bài tập/kiểm tra

#### Student Services
- **Student Personalization Service** (Port: 8087) - Cá nhân hóa lộ trình học tập
- **Tracking Service** (Port: 8088) - Theo dõi tiến độ học tập

#### Communication Services
- **Notification Service** (Port: 8090) - Gửi thông báo
- **Communication Service** (Port: 8091) - Nhắn tin & thảo luận

#### Analytics Service
- **Analytics & Reporting Service** (Port: 8092) - Báo cáo & phân tích

### Common Module
Module chứa các utilities, DTOs, và configurations dùng chung cho tất cả microservices.

## 🛠️ Công Nghệ Sử Dụng

- **Java**: 21
- **Spring Boot**: 3.5.0
- **Spring Cloud**: 2025.0.0
- **Maven**: 3.9.9
- **Database**: H2 (development), PostgreSQL/MySQL (production)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Security**: Spring Security + JWT

## 📋 Yêu Cầu Hệ Thống

- Java JDK 21 hoặc cao hơn
- Maven 3.9.9
- RAM tối thiểu: 8GB (khuyến nghị 16GB để chạy tất cả services)

## 🚀 Hướng Dẫn Chạy Dự Án

### 1. Build toàn bộ project
```bash
# Make wrapper executable (Linux/Mac only, chỉ cần chạy 1 lần)
chmod +x mvnw

# Build project
# Linux/Mac
./mvnw clean install

# Windows
mvnw.cmd clean install
```

### 2. Chạy từng service theo thứ tự

#### Bước 1: Khởi động Eureka Server (Service Discovery)
```bash
cd eureka-server

# Linux/Mac
../mvnw spring-boot:run

# Windows
..\mvnw.cmd spring-boot:run
```
Truy cập: http://localhost:8761

#### Bước 2: Khởi động Config Server (tùy chọn)
```bash
cd config-server

# Linux/Mac
../mvnw spring-boot:run

# Windows
..\mvnw.cmd spring-boot:run
```

#### Bước 3: Khởi động API Gateway
```bash
cd api-gateway

# Linux/Mac
../mvnw spring-boot:run

# Windows
..\mvnw.cmd spring-boot:run
```
API Gateway: http://localhost:8080

#### Bước 4: Khởi động các Business Services
Mở terminal riêng cho mỗi service:

```bash
# Linux/Mac
cd authentication-service && ../mvnw spring-boot:run
cd user-management-service && ../mvnw spring-boot:run
cd course-management-service && ../mvnw spring-boot:run
cd assessment-management-service && ../mvnw spring-boot:run
cd assessment-execution-service && ../mvnw spring-boot:run
cd course-delivery-service && ../mvnw spring-boot:run
cd student-personalization-service && ../mvnw spring-boot:run
cd tracking-service && ../mvnw spring-boot:run
cd enrollment-service && ../mvnw spring-boot:run
cd notification-service && ../mvnw spring-boot:run
cd communication-service && ../mvnw spring-boot:run
cd analytics-reporting-service && ../mvnw spring-boot:run

# Windows
cd authentication-service && ..\mvnw.cmd spring-boot:run
# (tương tự cho các services khác)
```

## 📁 Cấu Trúc Thư Mục

```
Capstone_Project_LMS-HCMUT_BE/
├── pom.xml (Root POM - Parent)
├── README.md
├── .gitignore
│
├── eureka-server/                          # Service Discovery
├── api-gateway/                            # API Gateway
├── config-server/                          # Config Server
├── common/                                 # Shared Module
│
├── authentication-service/                 # Core Services
├── user-management-service/
│
├── course-management-service/              # Course Services
├── course-delivery-service/
├── enrollment-service/
│
├── assessment-management-service/          # Assessment Services
├── assessment-execution-service/
│
├── student-personalization-service/        # Student Services
├── tracking-service/
│
├── notification-service/                   # Communication Services
├── communication-service/
│
└── analytics-reporting-service/            # Analytics Service
```

## 🔗 API Endpoints

Tất cả requests đều đi qua API Gateway tại `http://localhost:8080`

### Authentication Service
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/refresh-token` - Làm mới token
- `POST /api/auth/validate-token` - Xác thực token

### User Management Service
- `GET /api/users` - Lấy danh sách users
- `POST /api/users` - Tạo user mới
- `GET /api/users/{id}` - Lấy thông tin user
- `PUT /api/users/{id}` - Cập nhật user
- `DELETE /api/users/{id}` - Xóa user

### Course Management Service
- `GET /api/courses` - Lấy danh sách khóa học
- `POST /api/courses` - Tạo khóa học mới
- `GET /api/courses/{id}` - Xem chi tiết khóa học
- `PUT /api/courses/{id}` - Cập nhật khóa học

### Assessment Services
- `POST /api/assessments` - Tạo bài tập/kiểm tra
- `POST /api/execution/quiz/{id}/submit` - Nộp bài quiz
- `POST /api/execution/assignment/{id}/submit` - Nộp bài tập

### Student Services
- `POST /api/personalization/goals` - Thiết lập mục tiêu học tập
- `GET /api/personalization/learning-path` - Xem lộ trình học tập
- `GET /api/tracking/progress/{studentId}` - Xem tiến độ học tập

### Enrollment Service
- `POST /api/enrollment/enroll` - Đăng ký khóa học
- `GET /api/enrollment/search` - Tìm kiếm khóa học
- `POST /api/enrollment/course/{id}/rate` - Đánh giá khóa học

### Communication Services
- `POST /api/notifications/send` - Gửi thông báo
- `POST /api/communication/message` - Gửi tin nhắn
- `POST /api/communication/forum` - Tạo diễn đàn thảo luận

### Analytics Service
- `GET /api/analytics/reports/system` - Xem báo cáo hệ thống
- `GET /api/analytics/logs/system` - Xem logs hệ thống
- `POST /api/analytics/export/report/{id}` - Xuất báo cáo

## 📝 Ghi Chú

- Đây là phiên bản khởi tạo cơ bản, chưa implement chi tiết
- Tất cả services đang sử dụng H2 in-memory database cho development
- Cần cấu hình database thật (PostgreSQL/MySQL) cho production
- Cần implement các business logic cho từng service
- Cần implement security với JWT
- Cần thêm API documentation với Swagger/OpenAPI
- Cần implement integration tests

## 🔐 Security

- Hiện tại project chưa implement authentication/authorization đầy đủ
- Cần implement JWT token validation tại API Gateway
- Cần secure inter-service communication

## 📧 Liên Hệ

Project được phát triển bởi team Wecancode

## 📄 License

Copyright © 2025 HCMUT
