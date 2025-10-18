# 🐳 Docker Build & Deployment Guide

## 📋 Overview

This guide explains how to build and deploy all microservices using Docker and Docker Compose.

---

## 🔧 Build Strategy

### Optimizations Applied:
1. ✅ **Official Maven Image**: Uses `maven:3.9.9-eclipse-temurin-21-alpine` (no wrapper download needed)
2. ✅ **Dependency Caching**: POMs copied first → dependencies downloaded → source code copied
3. ✅ **Multi-stage Build**: Build stage (Maven + JDK) → Runtime stage (JRE only)
4. ✅ **Minimal Context**: `.dockerignore` excludes unnecessary files

### Build Layers:
```
Stage 1 (Build - ~500MB):
├── Maven 3.9.9 + JDK 21
├── Copy POM files
├── Download dependencies (CACHED)
├── Copy source code
└── Build JAR

Stage 2 (Runtime - ~200MB):
├── JRE 21 Alpine
└── Copy JAR from Stage 1
```

---

## 🚀 Quick Start

### 1. Start PostgreSQL Only (Recommended First Step)
```bash
# Linux/Mac
./start-postgres-only.sh

# Windows
start-postgres-only.bat
```

### 2. Build & Start All Services
```bash
# Build và start tất cả
docker-compose up -d --build

# Hoặc build riêng từng service
docker-compose build eureka-server
docker-compose build authentication-service
# ...

# Start sau khi build
docker-compose up -d
```

### 3. Check Status
```bash
# Xem services đang chạy
docker-compose ps

# Xem logs
docker-compose logs -f authentication-service

# Xem logs tất cả
docker-compose logs -f
```

---

## 📊 Build Time Expectations

| Stage | First Build | Subsequent Builds |
|-------|-------------|-------------------|
| Download Maven Image | 1-2 min | 0s (cached) |
| Download Dependencies | 3-5 min | 0s (cached) |
| Compile Code | 2-3 min | 30s-1min |
| **Total** | **6-10 min/service** | **30s-1min** |

**For all 15 services:**
- First build: ~30-60 minutes (parallel builds)
- Subsequent builds: ~5-10 minutes

---

## 🔍 Troubleshooting

### Issue: Network Timeout
```
wget: Failed to fetch https://repo.maven.apache.org/...
```
**Solution**: ✅ Fixed by using official Maven image instead of wrapper

### Issue: Maven Multi-Module Validation Error
```
[ERROR] Child module /workspace/app/api-gateway of /workspace/app/pom.xml does not exist
```
**Root Cause**: Parent POM declares 15 modules, but Dockerfile only copied 3 POMs

**Solution**: ✅ Fixed - All Dockerfiles now copy ALL 16 pom.xml files (parent + 15 modules)

### Issue: Build Context Too Large (1.18GB)
```
=> => transferring context: 1.18GB
```
**Solution**: 
- `.dockerignore` already excludes `target/`, `logs/`, `.git/`
- First build will be large (copying all source)
- Use `docker system prune` to clean up old images

### Issue: Out of Memory During Build
**Solution**:
```bash
# Increase Docker memory limit (Docker Desktop)
# Settings → Resources → Memory → 8GB+

# Or build one service at a time
docker-compose build eureka-server
docker-compose build config-server
# ...
```

### Issue: Service Failed to Start
**Check dependencies:**
```bash
# Ensure PostgreSQL is running first
docker-compose ps | grep postgres

# Check service logs
docker-compose logs authentication-service
```

---

## 🎯 Build Individual Services

```bash
# Build one service
docker-compose build authentication-service

# Start one service (with dependencies)
docker-compose up -d authentication-service

# Rebuild without cache
docker-compose build --no-cache authentication-service
```

---

## 🧹 Clean Up

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes database data)
docker-compose down -v

# Remove old images
docker system prune -a

# Remove build cache
docker builder prune -a
```

---

## 📦 Build Order (Recommended)

1. **Infrastructure Services** (no dependencies):
   ```bash
   docker-compose up -d postgres-eureka
   docker-compose up -d eureka-server
   docker-compose up -d config-server
   ```

2. **Core Services**:
   ```bash
   docker-compose up -d authentication-service
   docker-compose up -d user-management-service
   ```

3. **Business Services**:
   ```bash
   docker-compose up -d course-management-service
   docker-compose up -d assessment-management-service
   # ... other services
   ```

4. **Gateway** (last):
   ```bash
   docker-compose up -d api-gateway
   ```

**Or build everything at once:**
```bash
docker-compose up -d --build
```

---

## 🔐 Environment Variables

Each service uses these environment variables (defined in `docker-compose.yml`):

```yaml
DB_HOST: postgres-<service-name>
DB_PORT: 5432
DB_NAME: <service>_db
DB_USER: <service>_user
DB_PASSWORD: <service>_password
```

Modify in `docker-compose.yml` if needed.

---

## 📍 Service URLs

| Service | URL |
|---------|-----|
| Eureka Dashboard | http://localhost:8761 |
| API Gateway | http://localhost:8080 |
| Authentication | http://localhost:8081 |
| User Management | http://localhost:8082 |
| Course Management | http://localhost:8083 |
| ... | ... |

---

## 💡 Pro Tips

1. **Parallel Builds**: Docker Compose builds services in parallel by default
2. **Layer Caching**: Changing only source code won't re-download dependencies
3. **Incremental Builds**: Modify only changed services → faster rebuilds
4. **Health Checks**: PostgreSQL containers have health checks → services wait for DB ready

---

## 📞 Need Help?

- Check logs: `docker-compose logs -f <service-name>`
- Check Eureka Dashboard: http://localhost:8761
- Verify PostgreSQL: `./test-postgres-connections.sh`

---

**Happy Deploying! 🚀**

