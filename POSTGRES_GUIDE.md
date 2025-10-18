# 🐘 PostgreSQL Setup Guide - LMS Backend

Hướng dẫn sử dụng PostgreSQL cho 12 microservices (Database per Service pattern).

---

## 🚀 Quick Start

### **1. Start PostgreSQL Databases:**
```bash
# Windows
start-postgres-only.bat

# Linux/Mac
./start-postgres-only.sh
```

### **2. Stop PostgreSQL Databases:**
```bash
# Windows
stop-postgres-only.bat

# Linux/Mac
./stop-postgres-only.sh
```

### **3. Test Connections:**
```bash
# Windows
test-postgres-connections.bat

# Linux/Mac
./test-postgres-connections.sh
```

---

## 📊 Database Configuration

| Service | Database | Port | Container Name |
|---------|----------|------|----------------|
| Authentication | authentication_db | 5432 | postgres-authentication |
| User Management | user_management_db | 5433 | postgres-user-management |
| Course Management | course_management_db | 5434 | postgres-course-management |
| Assessment Management | assessment_management_db | 5435 | postgres-assessment-management |
| Assessment Execution | assessment_execution_db | 5436 | postgres-assessment-execution |
| Course Delivery | course_delivery_db | 5437 | postgres-course-delivery |
| Student Personalization | student_personalization_db | 5438 | postgres-student-personalization |
| Tracking | tracking_db | 5439 | postgres-tracking |
| Enrollment | enrollment_db | 5440 | postgres-enrollment |
| Notification | notification_db | 5441 | postgres-notification |
| Communication | communication_db | 5442 | postgres-communication |
| Analytics | analytics_db | 5443 | postgres-analytics |

**Credentials (tất cả databases):**
- Username: `lms_user`
- Password: `lms_password`

---

## 🔧 Docker Commands

### **Start/Stop:**
```bash
# Start tất cả (databases + services)
docker-compose up -d

# Start chỉ databases
./start-postgres-only.sh

# Stop databases (giữ data)
./stop-postgres-only.sh

# Stop và xóa containers (giữ data)
docker-compose down

# Stop và XÓA DATA
docker-compose down -v
```

### **Monitor:**
```bash
# Xem status
docker-compose ps

# Xem logs
docker logs postgres-user-management
docker logs -f postgres-user-management  # follow mode

# Check resource usage
docker stats
```

---

## 💻 Database Access

### **Từ Docker Container:**
```bash
# Connect vào database
docker exec -it postgres-user-management psql -U lms_user -d user_management_db

# Chạy SQL query
docker exec postgres-user-management psql -U lms_user -d user_management_db -c "SELECT version();"
```

### **Từ Host Machine (cần cài psql):**
```bash
# Connect
psql -h localhost -p 5433 -U lms_user -d user_management_db

# Windows (nếu cài PostgreSQL)
"C:\Program Files\PostgreSQL\16\bin\psql.exe" -h localhost -p 5433 -U lms_user -d user_management_db
```

### **Sử dụng GUI Tools (DBeaver, pgAdmin, etc.):**
- Host: `localhost`
- Port: `5432-5443` (tùy service)
- Database: Xem bảng trên
- Username: `lms_user`
- Password: `lms_password`

---

## 🗄️ Backup & Restore

### **Backup Database:**
```bash
# Backup một database
docker exec postgres-user-management pg_dump -U lms_user user_management_db > backup_user_management.sql

# Backup tất cả databases
docker exec postgres-user-management pg_dumpall -U lms_user > backup_all.sql

# Backup với compression
docker exec postgres-user-management pg_dump -U lms_user -Fc user_management_db > backup.dump
```

### **Restore Database:**
```bash
# Restore từ SQL file
docker exec -i postgres-user-management psql -U lms_user -d user_management_db < backup_user_management.sql

# Restore từ compressed dump
docker exec -i postgres-user-management pg_restore -U lms_user -d user_management_db backup.dump
```

---

## 🔨 Development Workflow

### **Option 1: PostgreSQL Docker + Services Maven (Khuyến nghị)**

```bash
# 1. Start PostgreSQL
./start-postgres-only.sh

# 2. Start Eureka
cd eureka-server && ../mvnw spring-boot:run

# 3. Start services (terminal mới cho mỗi service)
cd user-management-service && ../mvnw spring-boot:run
cd course-management-service && ../mvnw spring-boot:run
```

**Advantages:**
- Dễ debug services
- Rebuild nhanh
- Xem logs trực tiếp

### **Option 2: Docker Compose (Full Stack)**

```bash
# Start tất cả
docker-compose up -d

# Xem logs
docker-compose logs -f user-management-service
```

**Advantages:**
- Giống production environment
- Start tất cả cùng lúc
- Dễ deploy

---

## 🧪 Testing & Verification

### **1. Check PostgreSQL Containers:**
```bash
docker ps | grep postgres
# Should see 12 containers running
```

### **2. Test Database Connections:**
```bash
./test-postgres-connections.sh
# All 12 databases should show ✅ [OK]
```

### **3. Test Service Connection:**
```bash
# Start một service
cd user-management-service
../mvnw spring-boot:run

# Check logs - phải thấy:
# ✅ HikariPool-1 - Start completed
# ✅ Initialized JPA EntityManagerFactory
# ✅ Started UserManagementServiceApplication
```

### **4. Verify Eureka Registration:**
```
http://localhost:8761/
# Services phải xuất hiện trong danh sách
```

### **5. Test API Endpoints:**
```bash
curl http://localhost:8082/actuator/health
curl http://localhost:8080/api/users
```

---

## 🐛 Troubleshooting

### **Problem: Container không start**
```bash
# Check logs
docker logs postgres-user-management

# Restart container
docker restart postgres-user-management

# Remove và recreate
docker-compose down
docker-compose up -d postgres-user-management
```

### **Problem: Port already in use**
```bash
# Windows - Find và kill process
netstat -ano | findstr :5433
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:5433 | xargs kill -9
```

### **Problem: Connection refused**
**Causes:**
- PostgreSQL container chưa start
- Service đang dùng wrong port
- Firewall blocking

**Solutions:**
```bash
# 1. Check container status
docker ps | grep postgres

# 2. Check logs
docker logs postgres-user-management

# 3. Verify port mapping
docker port postgres-user-management

# 4. Test connection
docker exec postgres-user-management pg_isready -U lms_user
```

### **Problem: Authentication failed**
```bash
# Reset password
docker exec -it postgres-user-management psql -U postgres -c "ALTER USER lms_user WITH PASSWORD 'lms_password';"
```

### **Problem: Out of memory**
```bash
# Check Docker memory
docker stats

# Tăng memory limit trong Docker Desktop
# Settings -> Resources -> Memory -> 8GB+
```

### **Problem: Slow query/performance**
```bash
# Check connections
docker exec postgres-user-management psql -U lms_user -d user_management_db -c "SELECT count(*) FROM pg_stat_activity;"

# Check slow queries
docker exec postgres-user-management psql -U lms_user -d user_management_db -c "SELECT query, mean_exec_time FROM pg_stat_statements ORDER BY mean_exec_time DESC LIMIT 5;"
```

---

## 🔐 Production Best Practices

### **1. Security:**
- ❌ Không dùng default password `lms_password`
- ✅ Sử dụng strong passwords
- ✅ Enable SSL/TLS connections
- ✅ Use Docker secrets hoặc Kubernetes secrets
- ✅ Restrict network access

### **2. Backup Strategy:**
```bash
# Automated daily backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker exec postgres-user-management pg_dump -U lms_user user_management_db > backup_$DATE.sql
# Upload to S3 or backup storage
```

### **3. Monitoring:**
- Setup Prometheus + Grafana
- Monitor database size, connections, query performance
- Alert on high CPU/memory usage

### **4. Performance Tuning:**
```yaml
# docker-compose.yml
postgres-user-management:
  # ... other config
  command:
    - "postgres"
    - "-c"
    - "max_connections=200"
    - "-c"
    - "shared_buffers=256MB"
    - "-c"
    - "effective_cache_size=1GB"
```

### **5. High Availability:**
- Setup read replicas
- Use connection pooling (PgBouncer)
- Configure automatic failover

---

## 📋 Useful SQL Commands

### **Database Information:**
```sql
-- List all databases
\l

-- List all tables
\dt

-- Describe table
\d table_name

-- Database size
SELECT pg_database.datname, pg_size_pretty(pg_database_size(pg_database.datname)) AS size
FROM pg_database;

-- Table sizes
SELECT relname AS table_name, pg_size_pretty(pg_total_relation_size(relid)) AS size
FROM pg_catalog.pg_statio_user_tables
ORDER BY pg_total_relation_size(relid) DESC;
```

### **Connection Management:**
```sql
-- Current connections
SELECT count(*) FROM pg_stat_activity;

-- Active queries
SELECT pid, usename, application_name, client_addr, state, query
FROM pg_stat_activity
WHERE state = 'active';

-- Kill connection
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE pid = <pid>;
```

### **Maintenance:**
```sql
-- Vacuum (cleanup)
VACUUM ANALYZE;

-- Reindex
REINDEX DATABASE user_management_db;

-- Check table bloat
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

## 🎯 Environment Variables

Services sử dụng các environment variables sau:

```bash
# Database connection
DB_HOST=localhost          # Default: localhost
DB_PORT=5433              # Port tương ứng với service
DB_NAME=user_management_db # Database name
DB_USER=lms_user          # Default: lms_user
DB_PASSWORD=lms_password  # Default: lms_password
```

**Cách set environment variables:**

### Windows (CMD):
```cmd
set DB_HOST=localhost
set DB_PORT=5433
cd user-management-service
..\mvnw.cmd spring-boot:run
```

### Windows (PowerShell):
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="5433"
cd user-management-service
..\mvnw.cmd spring-boot:run
```

### Linux/Mac:
```bash
export DB_HOST=localhost
export DB_PORT=5433
cd user-management-service
../mvnw spring-boot:run
```

---

## 📁 File Structure

```
project-root/
├── docker-compose.yml                    # PostgreSQL + Services config
├── start-postgres-only.bat/.sh          # Start databases only
├── stop-postgres-only.bat/.sh           # Stop databases
├── test-postgres-connections.bat/.sh    # Test all connections
├── application-postgres.yml.template    # Config templates
└── POSTGRES_GUIDE.md                    # This file
```

---

## 🔄 Migration from H2 to PostgreSQL

**Đã hoàn thành:**
- ✅ Updated 12 `pom.xml` files (added PostgreSQL driver)
- ✅ Updated 12 `application.yml` files (PostgreSQL config)
- ✅ Docker Compose with 12 PostgreSQL containers
- ✅ Scripts for start/stop/test
- ✅ HikariCP connection pooling configured

**Thay đổi chính:**
```yaml
# Before (H2)
spring:
  datasource:
    url: jdbc:h2:mem:userdb
    driver-class-name: org.h2.Driver

# After (PostgreSQL)
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5433}/${DB_NAME:user_management_db}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

---

## ❓ FAQ

**Q: Làm sao để reset tất cả data?**
```bash
docker-compose down -v
docker-compose up -d
```

**Q: Làm sao để backup trước khi update?**
```bash
./backup-all-databases.sh  # Tạo script này nếu cần
```

**Q: Service không connect được database?**
- Check PostgreSQL container đang chạy: `docker ps`
- Check port đúng chưa: Xem bảng configuration
- Check logs: `docker logs postgres-<service-name>`

**Q: Muốn dùng lại H2 thay vì PostgreSQL?**
- Uncomment H2 dependency trong `pom.xml`
- Comment PostgreSQL dependency
- Revert `application.yml` về H2 config

**Q: Làm sao deploy lên production?**
- Use managed PostgreSQL (AWS RDS, Azure Database, Google Cloud SQL)
- Hoặc setup PostgreSQL cluster với replication
- Update connection strings trong environment variables

---

## 🆘 Support & Resources

**Documentation:**
- PostgreSQL Official Docs: https://www.postgresql.org/docs/
- Spring Boot + PostgreSQL: https://spring.io/guides/gs/accessing-data-postgresql/
- Docker Compose: https://docs.docker.com/compose/

**Tools:**
- pgAdmin: https://www.pgadmin.org/
- DBeaver: https://dbeaver.io/
- Flyway (migrations): https://flywaydb.org/

**Monitoring:**
- Prometheus PostgreSQL Exporter
- Grafana Dashboards
- pgBadger (log analyzer)

---

**🎉 Setup hoàn tất! Hệ thống sẵn sàng với PostgreSQL!**

Để bắt đầu: `./start-postgres-only.sh` → Rebuild project → Start services → Test!

