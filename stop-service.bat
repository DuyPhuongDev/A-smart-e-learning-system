@echo off
REM Script to stop a specific LMS Microservice on Windows
REM Usage: stop-service.bat <service-name>
REM Example: stop-service.bat user-management-service

setlocal enabledelayedexpansion

SET SERVICE_NAME=%1

if "%SERVICE_NAME%"=="" (
    call :show_usage
    exit /b 1
)

REM Handle shortcuts
if "%SERVICE_NAME%"=="eureka" SET SERVICE_NAME=eureka-server
if "%SERVICE_NAME%"=="config" SET SERVICE_NAME=config-server
if "%SERVICE_NAME%"=="gateway" SET SERVICE_NAME=api-gateway
if "%SERVICE_NAME%"=="auth" SET SERVICE_NAME=authentication-service
if "%SERVICE_NAME%"=="authentication" SET SERVICE_NAME=authentication-service
if "%SERVICE_NAME%"=="user" SET SERVICE_NAME=user-management-service
if "%SERVICE_NAME%"=="user-management" SET SERVICE_NAME=user-management-service
if "%SERVICE_NAME%"=="course" SET SERVICE_NAME=course-management-service
if "%SERVICE_NAME%"=="course-management" SET SERVICE_NAME=course-management-service

REM Service to port mapping
if "%SERVICE_NAME%"=="eureka-server" SET PORT=8761
if "%SERVICE_NAME%"=="config-server" SET PORT=8888
if "%SERVICE_NAME%"=="api-gateway" SET PORT=8080
if "%SERVICE_NAME%"=="authentication-service" SET PORT=8081
if "%SERVICE_NAME%"=="user-management-service" SET PORT=8082
if "%SERVICE_NAME%"=="course-management-service" SET PORT=8083
if "%SERVICE_NAME%"=="assessment-service" SET PORT=8084
if "%SERVICE_NAME%"=="learning-service" SET PORT=8086
if "%SERVICE_NAME%"=="personalization-service" SET PORT=8087
if "%SERVICE_NAME%"=="notification-service" SET PORT=8090
if "%SERVICE_NAME%"=="communication-service" SET PORT=8091

if "%PORT%"=="" (
    echo [ERROR] Unknown service '%SERVICE_NAME%'
    echo.
    call :show_usage
    exit /b 1
)

echo Stopping %SERVICE_NAME% on port %PORT%...

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%PORT%" ^| findstr "LISTENING"') do (
    SET pid=%%a
    taskkill /F /PID !pid! >nul 2>&1
    if !errorlevel! equ 0 (
        echo [OK] Stopped %SERVICE_NAME% ^(PID !pid!^)
        exit /b 0
    ) else (
        echo [WARN] Failed to stop process !pid!
        exit /b 1
    )
)

echo [INFO] Service %SERVICE_NAME% is not running
exit /b 1

:show_usage
echo =========================================
echo Stop LMS Microservice
echo =========================================
echo.
echo Usage: stop-service.bat ^<service-name^>
echo.
echo Available services:
echo.
echo Infrastructure:
echo   * eureka-server
echo   * config-server
echo   * api-gateway
echo.
echo Business Services:
echo   * authentication-service
echo   * user-management-service
echo   * course-management-service
echo   * assessment-service
echo   * learning-service
echo   * personalization-service
echo   * notification-service
echo   * communication-service
echo.
echo Examples:
echo   stop-service.bat user-management-service
echo   stop-service.bat eureka-server
echo.
echo Shortcuts:
echo   stop-service.bat user
echo   stop-service.bat auth
echo.
echo To stop all services: stop-all-services.bat
echo =========================================
exit /b

