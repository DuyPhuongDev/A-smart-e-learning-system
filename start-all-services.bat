@echo off
REM Script to start all microservices on Windows (Consolidated Architecture)
REM Usage: start-all-services.bat

echo =========================================
echo Starting LMS Microservices
echo Consolidated Architecture (7 Services)
echo =========================================

REM Create logs directory
if not exist logs mkdir logs

REM Step 1: Start Eureka Server (Service Discovery)
echo.
echo Step 1: Starting Eureka Server...
start "Eureka Server" cmd /c "cd eureka-server && ..\mvnw.cmd spring-boot:run > ..\logs\eureka-server.log 2>&1"
echo Waiting for Eureka Server to start (30 seconds)...
timeout /t 30 /nobreak

REM Step 2: Start Config Server
echo.
echo Step 2: Starting Config Server...
start "Config Server" cmd /c "cd config-server && ..\mvnw.cmd spring-boot:run > ..\logs\config-server.log 2>&1"
timeout /t 10 /nobreak

REM Step 3: Start API Gateway
echo.
echo Step 3: Starting API Gateway...
start "API Gateway" cmd /c "cd api-gateway && ..\mvnw.cmd spring-boot:run > ..\logs\api-gateway.log 2>&1"
timeout /t 10 /nobreak

REM Step 4: Start Business Services (Consolidated)
echo.
echo Step 4: Starting Business Services...

start "Authentication Service" cmd /c "cd authentication-service && ..\mvnw.cmd spring-boot:run > ..\logs\authentication-service.log 2>&1"
timeout /t 3 /nobreak

start "User Management Service" cmd /c "cd user-management-service && ..\mvnw.cmd spring-boot:run > ..\logs\user-management-service.log 2>&1"
timeout /t 3 /nobreak

start "Course Management Service" cmd /c "cd course-management-service && ..\mvnw.cmd spring-boot:run > ..\logs\course-management-service.log 2>&1"
timeout /t 3 /nobreak

start "Assessment Service" cmd /c "cd assessment-service && ..\mvnw.cmd spring-boot:run > ..\logs\assessment-service.log 2>&1"
timeout /t 3 /nobreak

start "Learning Service" cmd /c "cd learning-service && ..\mvnw.cmd spring-boot:run > ..\logs\learning-service.log 2>&1"
timeout /t 3 /nobreak

start "Personalization Service" cmd /c "cd personalization-service && ..\mvnw.cmd spring-boot:run > ..\logs\personalization-service.log 2>&1"
timeout /t 3 /nobreak

start "Notification Service" cmd /c "cd notification-service && ..\mvnw.cmd spring-boot:run > ..\logs\notification-service.log 2>&1"
timeout /t 3 /nobreak

start "Communication Service" cmd /c "cd communication-service && ..\mvnw.cmd spring-boot:run > ..\logs\communication-service.log 2>&1"

echo.
echo =========================================
echo All services started!
echo =========================================
echo.
echo Service URLs:
echo   - Eureka Dashboard: http://localhost:8761
echo   - API Gateway: http://localhost:8080
echo   - Config Server: http://localhost:8888
echo.
echo Business Services:
echo   - Authentication: http://localhost:8081
echo   - User Management: http://localhost:8082
echo   - Course Management: http://localhost:8083
echo   - Assessment: http://localhost:8084
echo   - Learning: http://localhost:8086
echo   - Personalization: http://localhost:8087
echo   - Notification: http://localhost:8090
echo   - Communication: http://localhost:8091
echo.
echo Database: Single PostgreSQL (localhost:5432/lms_db)
echo Logs are available in .\logs\ directory
echo.
echo To stop all services, close all command windows
echo =========================================

pause
