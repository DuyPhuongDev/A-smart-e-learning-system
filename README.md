# LMS Backend - HCMUT

> Learning Management System Backend - Ho Chi Minh University of Technology  
> Microservices Architecture với Spring Boot 3.5.0 + Spring Cloud 2025.0.0

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)

---

## 🏗️ Kiến Trúc Hệ Thống

**11 Services:**
- **Infrastructure (3):** Eureka Server, API Gateway, Config Server
- **Business (8):** Authentication, User Management, Course Management, Assessment, Learning, Personalization, Notification, Communication

**Database:** Shared PostgreSQL (lms_db)

---

## ⚡ Quick Start

### 1️⃣ Yêu Cầu

```bash
- Java 21+
- Maven 3.9+
- PostgreSQL 16+
```

### 2️⃣ Setup Database

**Option A: Local PostgreSQL**
```sql
CREATE USER lms_user WITH PASSWORD 'lms_password';
CREATE DATABASE lms_db OWNER lms_user;
GRANT ALL PRIVILEGES ON DATABASE lms_db TO lms_user;
```

**Option B: Docker**
```bash
docker-compose up -d postgres
```

### 3️⃣ Build Project

```bash
# Linux/Mac
./mvnw clean install

# Windows
mvnw.cmd clean install
```

### 4️⃣ Chạy Services

**Linux/Mac:**
```bash
chmod +x start-all-services.sh
./start-all-services.sh
# Services chạy ở foreground, nhấn Ctrl+C để stop tất cả
```

**Windows:**
```cmd
start-all-services.bat
```

**Docker:**
```bash
docker-compose up -d
```

### 5️⃣ Kiểm Tra

- **Eureka Dashboard:** http://localhost:8761
- **API Gateway:** http://localhost:8080

### 6️⃣ Check Status (Xem services nào đang chạy)

**Linux/Mac:**
```bash
./check-services.sh
```

**Windows:**
```cmd
check-services.bat
```

**Output ví dụ:**
```
Infrastructure Services:
● Eureka Server (port 8761) - RUNNING [PID: 12345, MEM: 450 MB]
● API Gateway (port 8080) - RUNNING [PID: 12346, MEM: 380 MB]
○ Config Server (port 8888) - STOPPED

Business Services:
● Authentication Service (port 8081) - RUNNING [PID: 12347, MEM: 420 MB]
...

Summary:
● Running: 8 / 11
○ Stopped: 3 / 11
```

---

## 🔧 Dừng Services

### Dừng tất cả:

**Nếu đang chạy start-all-services.sh:**
```bash
# Chỉ cần nhấn Ctrl+C trong terminal
```

**Hoặc dùng script:**

**Linux/Mac:**
```bash
./stop-all-services.sh         # Dừng tất cả
./stop-all-services.sh business # Chỉ dừng business services
./stop-all-services.sh infra    # Chỉ dừng infrastructure
```

**Windows:**
```cmd
stop-all-services.bat          # Dừng tất cả
stop-all-services.bat business
stop-all-services.bat infra
```

### Dừng 1 service cụ thể:

**Linux/Mac:**
```bash
./stop-service.sh user-management-service
./stop-service.sh eureka-server
./stop-service.sh auth    # Shortcut
./stop-service.sh user    # Shortcut
```

**Windows:**
```cmd
stop-service.bat user-management-service
stop-service.bat eureka-server
stop-service.bat auth
```

**Docker:**
```bash
docker-compose down
```

---

## 📊 Service Ports

| Service | Port | URL |
|---------|------|-----|
| **Infrastructure** |
| Eureka Server | 8761 | http://localhost:8761 |
| API Gateway | 8080 | http://localhost:8080 |
| Config Server | 8888 | http://localhost:8888 |
| **Business Services** |
| Authentication | 8081 | http://localhost:8081 |
| User Management | 8082 | http://localhost:8082 |
| Course Management | 8083 | http://localhost:8083 |
| Assessment | 8084 | http://localhost:8084 |
| Learning | 8086 | http://localhost:8086 |
| Personalization | 8087 | http://localhost:8087 |
| Notification | 8090 | http://localhost:8090 |
| Communication | 8091 | http://localhost:8091 |

---

## 🎯 Key Features

### 👤 User Management
- User CRUD, RBAC, Excel import/export
- Endpoint scanning & granular permissions
- Password management via Authentication Service

### 📚 Course Management
- Course creation, materials, grading system
- Material library, course approval workflow

### 📝 Assessment
- Quiz/assignment creation, AI-assisted grading
- Multiple submission types, online code execution

### 🎓 Learning
- Course enrollment, content delivery with AI
- Progress tracking, note-taking

### 🎯 Personalization
- AI-powered learning paths
- Academic schedule, analytics & reporting

### 💬 Communication
- Real-time messaging, forums
- Notifications (email, push, in-app)

---

## 🛠️ Development

### Build một service
```bash
cd user-management-service
mvn spring-boot:run
```

### Chạy tests
```bash
mvn test
```

### Xem logs
```bash
tail -f logs/user-management-service.log
```

### Health check
```bash
curl http://localhost:8082/actuator/health
```

---

## 🐛 Troubleshooting

**Kiểm tra services nào đang chạy:**
```bash
# Linux/Mac
./check-services.sh

# Windows
check-services.bat

# Hoặc check thủ công
lsof -i :8080  # Linux/Mac
netstat -ano | findstr :8080  # Windows
```

**Port đã được sử dụng:**
```bash
# Linux/Mac
lsof -ti:8080 | xargs kill -9

# Windows
netstat -ano | findstr :8080
taskkill /F /PID <PID>
```

**Không kết nối được Eureka:**
- Kiểm tra Eureka đang chạy: http://localhost:8761
- Đợi 30-60s để services đăng ký
- Restart service bị lỗi

**Build thất bại:**
```bash
./mvnw clean install -U -DskipTests
```

---

## 📁 Project Structure

```
lms-backend/
├── common/                      # Shared utilities, DTOs
├── eureka-server/               # Service discovery
├── api-gateway/                 # API gateway
├── config-server/               # Config management
├── authentication-service/      # Auth & JWT
├── user-management-service/     # Users, roles, RBAC
├── course-management-service/   # Courses, materials
├── assessment-service/          # Assessments
├── learning-service/            # Learning & tracking
├── personalization-service/     # AI learning paths
├── notification-service/        # Notifications
├── communication-service/       # Messaging, forums
├── docker-compose.yml           # Docker orchestration
├── start-all-services.sh        # Start script (Linux/Mac)
├── start-all-services.bat       # Start script (Windows)
├── stop-all-services.sh         # Stop all services (Linux/Mac)
├── stop-all-services.bat        # Stop all services (Windows)
├── stop-service.sh              # Stop one service (Linux/Mac)
├── stop-service.bat             # Stop one service (Windows)
├── check-services.sh            # Status check (Linux/Mac)
└── check-services.bat           # Status check (Windows)
```

---

## 🔒 Security

- **Authentication:** JWT-based
- **Authorization:** RBAC với endpoint-level permissions
- **Inter-Service:** Service discovery via Eureka

---

## 🚀 Tech Stack

- **Java 21** + **Spring Boot 3.5.0** + **Spring Cloud 2025.0.0**
- **PostgreSQL 16** (Shared Database)
- **Netflix Eureka** (Service Discovery)
- **Spring Cloud Gateway** (API Gateway)
- **Spring Security + JWT** (Authentication)
- **Docker & Docker Compose** (Containerization)
- **Apache POI** (Excel), **iTextPDF** (Reports)
- **WebSocket + STOMP** (Real-time communication)

---

## 👥 Team

**HCMUT - Computer Science Department**  
Capstone Project - Learning Management System

---

## 📞 Support

- Kiểm tra logs: `./logs/` directory
- Xem Eureka Dashboard: http://localhost:8761
- Health check: `http://localhost:808X/actuator/health`

---

**Built with ❤️ by HCMUT Students**
