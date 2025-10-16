@echo off
REM Script to start all microservices on Windows
REM Usage: start-all-services.bat

echo =========================================
echo Starting LMS Microservices
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

REM Step 4: Start Business Services
echo.
echo Step 4: Starting Business Services...

start "Authentication Service" cmd /c "cd authentication-service && ..\mvnw.cmd spring-boot:run > ..\logs\authentication-service.log 2>&1"
timeout /t 3 /nobreak

start "User Management Service" cmd /c "cd user-management-service && ..\mvnw.cmd spring-boot:run > ..\logs\user-management-service.log 2>&1"
timeout /t 3 /nobreak

start "Course Management Service" cmd /c "cd course-management-service && ..\mvnw.cmd spring-boot:run > ..\logs\course-management-service.log 2>&1"
timeout /t 3 /nobreak

start "Assessment Management Service" cmd /c "cd assessment-management-service && ..\mvnw.cmd spring-boot:run > ..\logs\assessment-management-service.log 2>&1"
timeout /t 3 /nobreak

start "Assessment Execution Service" cmd /c "cd assessment-execution-service && ..\mvnw.cmd spring-boot:run > ..\logs\assessment-execution-service.log 2>&1"
timeout /t 3 /nobreak

start "Course Delivery Service" cmd /c "cd course-delivery-service && ..\mvnw.cmd spring-boot:run > ..\logs\course-delivery-service.log 2>&1"
timeout /t 3 /nobreak

start "Student Personalization Service" cmd /c "cd student-personalization-service && ..\mvnw.cmd spring-boot:run > ..\logs\student-personalization-service.log 2>&1"
timeout /t 3 /nobreak

start "Tracking Service" cmd /c "cd tracking-service && ..\mvnw.cmd spring-boot:run > ..\logs\tracking-service.log 2>&1"
timeout /t 3 /nobreak

start "Enrollment Service" cmd /c "cd enrollment-service && ..\mvnw.cmd spring-boot:run > ..\logs\enrollment-service.log 2>&1"
timeout /t 3 /nobreak

start "Notification Service" cmd /c "cd notification-service && ..\mvnw.cmd spring-boot:run > ..\logs\notification-service.log 2>&1"
timeout /t 3 /nobreak

start "Communication Service" cmd /c "cd communication-service && ..\mvnw.cmd spring-boot:run > ..\logs\communication-service.log 2>&1"
timeout /t 3 /nobreak

start "Analytics Reporting Service" cmd /c "cd analytics-reporting-service && ..\mvnw.cmd spring-boot:run > ..\logs\analytics-reporting-service.log 2>&1"

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
echo Logs are available in .\logs\ directory
echo.
echo To stop all services, close all command windows
echo =========================================

pause

