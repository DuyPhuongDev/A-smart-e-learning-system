@echo off
REM Script to stop all running LMS Microservices on Windows
REM Usage:
REM   stop-all-services.bat           - Stop all services
REM   stop-all-services.bat business  - Stop only business services
REM   stop-all-services.bat infra     - Stop only infrastructure services

setlocal enabledelayedexpansion

SET MODE=%1
IF "%MODE%"=="" SET MODE=all

echo =========================================
echo    LMS Microservices - Stop Script
echo =========================================
echo.

REM Infrastructure Services
SET INFRA_PORTS=8761 8888 8080
SET INFRA_NAMES=Eureka-Server Config-Server API-Gateway

REM Business Services  
SET BUSINESS_PORTS=8081 8082 8083 8084 8085 8086 8087 8088
SET BUSINESS_NAMES=Authentication-Service User-Management-Service Course-Management-Service Notification-Service Communication-Service Assessment-Service Learning-Service Personalization-Service

SET TOTAL_STOPPED=0

IF "%MODE%"=="business" GOTO stop_business
IF "%MODE%"=="infra" GOTO stop_infra
IF "%MODE%"=="all" GOTO stop_all

echo Error: Invalid argument '%MODE%'
echo Usage: stop-all-services.bat [all^|business^|infra]
exit /b 1

:stop_all
echo Stopping All Services...
echo.
echo [1/2] Business Services:
call :stop_services_list "%BUSINESS_PORTS%" "%BUSINESS_NAMES%"
echo.
echo [2/2] Infrastructure Services:
call :stop_services_list "%INFRA_PORTS%" "%INFRA_NAMES%"
goto end

:stop_business
echo Stopping Business Services only...
echo.
call :stop_services_list "%BUSINESS_PORTS%" "%BUSINESS_NAMES%"
goto end

:stop_infra
echo Stopping Infrastructure Services only...
echo.
call :stop_services_list "%INFRA_PORTS%" "%INFRA_NAMES%"
goto end

:stop_services_list
SET ports=%~1
SET names=%~2
SET idx=0

for %%p in (%ports%) do (
    SET /a idx+=1
    for /f "tokens=!idx!" %%n in ("%names%") do (
        call :stop_service %%p %%n
    )
)
exit /b

:stop_service
SET port=%1
SET name=%2

echo Stopping %name% on port %port%...

REM Find and kill process on the port
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":%port%" ^| findstr "LISTENING"') do (
    SET pid=%%a
    taskkill /F /PID !pid! >nul 2>&1
    if !errorlevel! equ 0 (
        echo   [OK] Stopped %name% ^(PID !pid!^)
        SET /a TOTAL_STOPPED+=1
    ) else (
        echo   [WARN] Failed to stop process !pid!
    )
    goto :stop_service_done
)

echo   [INFO] No process found for %name%

:stop_service_done
exit /b

:end
echo.
echo =========================================
if %TOTAL_STOPPED% gtr 0 (
    echo Successfully stopped %TOTAL_STOPPED% service^(s^)
) else (
    echo No services were running
)
echo =========================================
echo.

REM Optional: Stop Docker containers
REM echo Stopping Docker containers...
REM docker-compose down

endlocal

