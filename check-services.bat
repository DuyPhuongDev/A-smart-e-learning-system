@echo off
REM Script to check status of all LMS Microservices on Windows
REM Usage: check-services.bat

setlocal enabledelayedexpansion

SET RUNNING_COUNT=0
SET TOTAL_COUNT=11

echo =========================================
echo    LMS Microservices - Status Check
echo =========================================
echo.

REM Define services
SET "INFRA_PORTS=8761 8888 8080"
SET "INFRA_NAMES=Eureka-Server Config-Server API-Gateway"
SET "BUSINESS_PORTS=8081 8082 8083 8084 8086 8087 8090 8091"
SET "BUSINESS_NAMES=Authentication User-Management Course-Management Assessment Learning Personalization Notification Communication"

echo Infrastructure Services:
call :check_services_list "!INFRA_PORTS!" "!INFRA_NAMES!"

echo.
echo Business Services:
call :check_services_list "!BUSINESS_PORTS!" "!BUSINESS_NAMES!"

SET /A STOPPED_COUNT=%TOTAL_COUNT%-%RUNNING_COUNT%

echo.
echo =========================================
echo Summary:
echo   [O] Running:  %RUNNING_COUNT% / %TOTAL_COUNT%
echo   [X] Stopped:  %STOPPED_COUNT% / %TOTAL_COUNT%
echo =========================================

if %RUNNING_COUNT% gtr 0 (
    echo.
    echo Quick Access:
    call :check_port 8761 "  * Eureka Dashboard: http://localhost:8761"
    call :check_port 8080 "  * API Gateway:      http://localhost:8080"
    
    echo.
    echo Management:
    echo   * Stop all:  stop-all-services.bat
    echo   * Start all: start-all-services.bat
)

goto :end

:check_services_list
SET ports=%~1
SET names=%~2
SET idx=0

for %%p in (%ports%) do (
    SET /a idx+=1
    for /f "tokens=!idx!" %%n in ("%names%") do (
        call :check_service %%p %%n
    )
)
exit /b

:check_service
SET port=%1
SET name=%2

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%port%" ^| findstr "LISTENING"') do (
    SET pid=%%a
    echo   [O] %name% ^(port %port%^) - RUNNING [PID: !pid!]
    SET /a RUNNING_COUNT+=1
    goto :check_service_done
)

echo   [X] %name% ^(port %port%^) - STOPPED

:check_service_done
exit /b

:check_port
SET check_port=%1
SET message=%~2

for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%check_port%" ^| findstr "LISTENING"') do (
    echo %message%
    exit /b
)
exit /b

:end
endlocal

