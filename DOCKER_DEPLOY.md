# 🐳 Docker Deployment Guide

Hướng dẫn deploy toàn bộ hệ thống LMS Backend với Docker Compose.

---

## ✅ Setup Hoàn Tất

**15 Dockerfiles** đã được tạo cho:
- ✅ eureka-server
- ✅ config-server
- ✅ api-gateway
- ✅ authentication-service
- ✅ user-management-service
- ✅ course-management-service
- ✅ assessment-management-service
- ✅ assessment-execution-service
- ✅ course-delivery-service
- ✅ student-personalization-service
- ✅ tracking-service
- ✅ enrollment-service
- ✅ notification-service
- ✅ communication-service
- ✅ analytics-reporting-service

**12 PostgreSQL databases** đã được cấu hình trong `docker-compose.yml`

---

## 🚀 Quick Start

### **Deploy Full Stack:**
```bash
# Build và start tất cả (PostgreSQL + 15 services)
docker-compose up -d

# Build lại nếu có thay đổi code
docker-compose up -d --build

# Xem logs
docker-compose logs -f

# Xem logs của service cụ thể
docker-compose logs -f user-management-service
```

### **Stop Everything:**
```bash
# Stop containers (giữ data)
docker-compose down

# Stop và XÓA DATA
docker-compose down -v
```

---

## 🔍 Monitoring

### **Check Status:**
```bash
# Xem tất cả containers
docker-compose ps

# Xem resource usage
docker stats

# Check logs real-time
docker-compose logs -f user-management-service api-gateway
```

### **Access Services:**
```
Eureka Dashboard:  http://localhost:8761
API Gateway:       http://localhost:8080
```

---

## 🏗️ Build Process

Mỗi Dockerfile sử dụng **multi-stage build**:

**Stage 1: Build**
- Base image: `eclipse-temurin:21-jdk-alpine`
- Copy source code và dependencies
- Build với Maven: `./mvnw clean package -DskipTests`

**Stage 2: Runtime**
- Base image: `eclipse-temurin:21-jre-alpine` (nhẹ hơn)
- Copy chỉ JAR file từ build stage
- Expose port
- Run application

**Ưu điểm:**
- Image nhỏ hơn (chỉ chứa JRE + JAR)
- Build consistent
- Layer caching tối ưu

---

## 🔧 Advanced Usage

### **Build từng service:**
```bash
# Build một service
docker-compose build user-management-service

# Start một service
docker-compose up -d user-management-service

# Restart service
docker-compose restart user-management-service
```

### **Scale services:**
```bash
# Scale user-management-service thành 3 instances
docker-compose up -d --scale user-management-service=3
```

### **Rebuild sau khi thay đổi code:**
```bash
# Rebuild service đã thay đổi
docker-compose build user-management-service

# Restart với image mới
docker-compose up -d user-management-service
```

---

## 📊 Service Dependencies

Services start theo thứ tự:

```
1. PostgreSQL Databases (12 containers)
   ↓
2. Eureka Server (Service Discovery)
   ↓
3. Config Server (Optional)
   ↓
4. API Gateway
   ↓
5. Business Services (12 services)
```

Docker Compose tự động quản lý dependencies với `depends_on`.

---

## 🐛 Troubleshooting

### **Service không start:**
```bash
# Check logs
docker-compose logs user-management-service

# Check container status
docker ps -a | grep user-management

# Restart service
docker-compose restart user-management-service
```

### **Database connection issues:**
```bash
# Check PostgreSQL logs
docker logs postgres-user-management

# Test connection
docker exec postgres-user-management pg_isready -U lms_user
```

### **Build fails:**
```bash
# Clean build
docker-compose build --no-cache user-management-service

# Remove old images
docker image prune -a
```

### **Out of memory:**
```bash
# Tăng memory limit trong Docker Desktop
# Settings -> Resources -> Memory -> 8GB+

# Clean up
docker system prune -a --volumes
```

### **Port conflicts:**
```bash
# Check port usage
netstat -ano | findstr :8082  # Windows
lsof -i :8082                 # Linux/Mac

# Stop conflicting service hoặc thay đổi port trong docker-compose.yml
```

---

## 🔐 Environment Variables

Services sử dụng environment variables từ docker-compose.yml:

```yaml
environment:
  - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
  - DB_HOST=postgres-user-management
  - DB_PORT=5432
  - DB_NAME=user_management_db
  - DB_USER=lms_user
  - DB_PASSWORD=lms_password
```

**Override trong production:**
```bash
# Tạo file .env
DB_PASSWORD=strong_production_password

# Docker Compose sẽ tự động đọc .env file
```

---

## 📦 Image Management

### **List images:**
```bash
docker images | grep lms
```

### **Remove old images:**
```bash
# Remove unused images
docker image prune

# Remove all images
docker image prune -a
```

### **Tag and push to registry:**
```bash
# Tag image
docker tag capstone_project_lms-hcmut_be-user-management-service:latest your-registry/lms/user-management:v1.0

# Push to registry
docker push your-registry/lms/user-management:v1.0
```

---

## 🚀 Production Deployment

### **Option 1: Docker Compose (Single Server)**
```bash
# Production docker-compose.yml với:
# - Resource limits
# - Health checks
# - Restart policies
# - Secrets management

docker-compose -f docker-compose.prod.yml up -d
```

### **Option 2: Kubernetes**
```bash
# Convert docker-compose to k8s manifests
kompose convert -f docker-compose.yml

# Deploy to k8s
kubectl apply -f .
```

### **Option 3: Docker Swarm**
```bash
# Initialize swarm
docker swarm init

# Deploy stack
docker stack deploy -c docker-compose.yml lms
```

---

## 📋 Best Practices

### **Development:**
- ✅ Dùng `./start-postgres-only.sh` + Maven local
- ✅ Faster rebuild và debug
- ✅ Direct log access

### **Staging:**
- ✅ Full Docker Compose setup
- ✅ Giống production environment
- ✅ Test trước khi deploy

### **Production:**
- ✅ Use managed PostgreSQL (RDS, Cloud SQL)
- ✅ Container orchestration (Kubernetes)
- ✅ CI/CD pipeline
- ✅ Monitoring và alerting
- ✅ Auto-scaling
- ✅ Load balancing

---

## ✅ Verification Checklist

Sau khi `docker-compose up -d`:

- [ ] All containers running: `docker-compose ps`
- [ ] PostgreSQL healthy: `docker logs postgres-user-management`
- [ ] Eureka accessible: http://localhost:8761
- [ ] Services registered in Eureka
- [ ] API Gateway working: http://localhost:8080
- [ ] Database connections working
- [ ] Health endpoints responding: `curl http://localhost:8082/actuator/health`

---

## 🆘 Quick Commands Reference

```bash
# Start everything
docker-compose up -d

# Stop everything
docker-compose down

# View logs
docker-compose logs -f

# Rebuild service
docker-compose build user-management-service

# Restart service
docker-compose restart user-management-service

# Check status
docker-compose ps

# Clean up
docker-compose down -v
docker system prune -a

# Scale service
docker-compose up -d --scale user-management-service=3
```

---

**🎉 Dockerfiles đã sẵn sàng! Chạy `docker-compose up -d` để deploy full stack!**



