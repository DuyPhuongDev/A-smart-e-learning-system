@echo off
REM ================================================================
REM Docker Build Script with BuildKit Cache Mount
REM ================================================================
REM This script enables BuildKit to use cache mount for Maven dependencies
REM which significantly speeds up builds when dependencies change
REM ================================================================

echo.
echo ================================================================
echo Building Docker Images with BuildKit Cache Mount
echo ================================================================
echo.

REM Enable Docker BuildKit
set DOCKER_BUILDKIT=1
set COMPOSE_DOCKER_CLI_BUILD=1

echo BuildKit enabled: DOCKER_BUILDKIT=1
echo.

REM Check if specific service is provided
if "%1"=="" (
    echo Building all services...
    echo.
    docker-compose build
) else (
    echo Building service: %1
    echo.
    docker-compose build %1
)

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================================================
    echo Build completed successfully!
    echo ================================================================
    echo.
    echo Maven dependencies are now cached and will persist across builds.
    echo Next builds will be much faster when only dependencies change.
    echo.
    echo To start services: docker-compose up
    echo To rebuild a specific service: %~nx0 [service-name]
    echo.
) else (
    echo.
    echo ================================================================
    echo Build failed! Check the error messages above.
    echo ================================================================
    echo.
)

