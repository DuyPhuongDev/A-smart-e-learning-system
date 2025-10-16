# Quick Start Guide

Hướng dẫn nhanh để chạy LMS Backend Microservices

## 🚀 Khởi Động Nhanh (5 phút)

### Bước 1: Kiểm tra yêu cầu hệ thống
```bash
java -version    # Cần Java 21+

# Kiểm tra Maven Wrapper
# Linux/Mac
./mvnw -version

# Windows
mvnw.cmd -version
```

### Bước 2: Build toàn bộ project
```bash
# Linux/Mac
./mvnw clean install

# Windows
mvnw.cmd clean install
```
⏱️ Thời gian: ~3-5 phút

### Bước 3: Chạy services

#### Option A: Chạy từng service thủ công

**Terminal 1 - Eureka Server:**
```bash
cd eureka-server

# Linux/Mac
../mvnw spring-boot:run

# Windows
..\mvnw.cmd spring-boot:run
```
Đợi 30 giây cho Eureka khởi động, sau đó mở http://localhost:8761

**Terminal 2 - API Gateway:**
```bash
cd api-gateway

# Linux/Mac
../mvnw spring-boot:run

# Windows
..\mvnw.cmd spring-boot:run
```

**Terminal 3-N - Các Business Services:**
```bash
# Chọn service bạn cần, ví dụ:
cd authentication-service

# Linux/Mac
../mvnw spring-boot:run

# Windows
..\mvnw.cmd spring-boot:run
```

#### Option B: Sử dụng script
```bash
# Linux/Mac
chmod +x start-all-services.sh
./start-all-services.sh

# Windows
start-all-services.bat
```

## 🧪 Kiểm Tra Hệ Thống

### 1. Kiểm tra Eureka Dashboard
Mở trình duyệt: http://localhost:8761

Bạn sẽ thấy các services đã đăng ký.

### 2. Test API qua Gateway

**Authentication Service:**
```bash
curl -X POST http://localhost:8080/api/auth/login
```

**User Management Service:**
```bash
curl http://localhost:8080/api/users
```

**Course Management Service:**
```bash
curl http://localhost:8080/api/courses
```

## 📊 Service Ports Cheat Sheet

| Service | Port | URL |
|---------|------|-----|
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| Config Server | 8888 | http://localhost:8888 |
| Authentication | 8081 | http://localhost:8081 |
| User Management | 8082 | http://localhost:8082 |
| Course Management | 8083 | http://localhost:8083 |
| Assessment Management | 8084 | http://localhost:8084 |
| Assessment Execution | 8085 | http://localhost:8085 |
| Course Delivery | 8086 | http://localhost:8086 |
| Student Personalization | 8087 | http://localhost:8087 |
| Tracking | 8088 | http://localhost:8088 |
| Enrollment | 8089 | http://localhost:8089 |
| Notification | 8090 | http://localhost:8090 |
| Communication | 8091 | http://localhost:8091 |
| Analytics & Reporting | 8092 | http://localhost:8092 |

## 🛑 Dừng Services

### Option A: Thủ công
Nhấn `Ctrl+C` ở mỗi terminal

### Option B: Sử dụng script (Linux/Mac)
```bash
chmod +x stop-all-services.sh
./stop-all-services.sh
```

## 📝 Test Scenarios

### Scenario 1: User Registration & Login
```bash
# 1. Register new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student1",
    "email": "student1@hcmut.edu.vn",
    "password": "password123"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student1",
    "password": "password123"
  }'
```

### Scenario 2: Create Course
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "name": "Introduction to Spring Boot",
    "description": "Learn microservices with Spring Boot"
  }'
```

### Scenario 3: Enroll in Course
```bash
curl -X POST http://localhost:8080/api/enrollment/enroll \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "courseId": 1,
    "studentId": 1
  }'
```

## 🐛 Troubleshooting Quick Fixes

### Problem: "Port already in use"
```bash
# Find and kill process
lsof -ti:8080 | xargs kill -9
```

### Problem: "Cannot connect to Eureka"
1. Kiểm tra Eureka Server đang chạy tại http://localhost:8761
2. Đợi 30-60 giây cho services đăng ký
3. Restart service bị lỗi

### Problem: Build fails
```bash
# Clean and rebuild
# Linux/Mac
./mvnw clean install -U -DskipTests

# Windows
mvnw.cmd clean install -U -DskipTests
```

### Problem: Services không thấy nhau
1. Kiểm tra tất cả services đã đăng ký với Eureka
2. Kiểm tra firewall/antivirus
3. Restart Eureka Server và các services

## 💡 Tips

1. **Chỉ chạy services cần thiết**: Không cần chạy tất cả 15 services cùng lúc. Chạy Eureka + Gateway + services bạn đang develop.

2. **Sử dụng IntelliJ IDEA**: Import project và chạy services từ IDE dễ hơn terminal.

3. **Check logs**: Logs được lưu ở `logs/` directory khi dùng start script.

4. **H2 Console**: Truy cập database tại http://localhost:808X/h2-console (X là port của service)
   - JDBC URL: `jdbc:h2:mem:xxxdb`
   - Username: `sa`
   - Password: (để trống)

5. **API Documentation**: Thêm Swagger để có API docs tự động (xem DEVELOPMENT_GUIDE.md)

## 📚 Next Steps

1. Đọc [ARCHITECTURE.md](ARCHITECTURE.md) để hiểu kiến trúc hệ thống
2. Đọc [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) để bắt đầu develop
3. Xem [FEATURES_TO_SERVICES_MAPPING.md](FEATURES_TO_SERVICES_MAPPING.md) để biết feature nào thuộc service nào
4. Implement business logic cho từng service
5. Thêm database thật (PostgreSQL/MySQL)
6. Implement JWT authentication
7. Add unit tests và integration tests

## 🆘 Need Help?

- Kiểm tra logs ở terminal hoặc `logs/` directory
- Xem [ARCHITECTURE.md](ARCHITECTURE.md) cho chi tiết kiến trúc
- Xem [DEVELOPMENT_GUIDE.md](DEVELOPMENT_GUIDE.md) cho hướng dẫn develop
- Kiểm tra GitHub Issues

## ✅ Health Check URLs

Mỗi service có actuator endpoint:

```bash
# Check service health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
# ... và các services khác
```

## 🎯 Development Workflow

```
1. Start Eureka Server
2. Start services bạn cần
3. Make changes
4. Test với curl/Postman
5. Commit code
6. Repeat
```

---

**Happy Coding! 🚀**

