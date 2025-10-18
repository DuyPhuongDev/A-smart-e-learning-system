@echo off
REM Script to test all PostgreSQL database connections
REM Requires psql to be installed and in PATH

echo =========================================
echo Testing PostgreSQL Database Connections
echo =========================================

set DB_USER=lms_user
set DB_PASS=lms_password

echo.
echo Testing Authentication DB (port 5432)...
docker exec postgres-authentication psql -U %DB_USER% -d authentication_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Authentication DB) else (echo [FAILED] Authentication DB)

echo Testing User Management DB (port 5433)...
docker exec postgres-user-management psql -U %DB_USER% -d user_management_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] User Management DB) else (echo [FAILED] User Management DB)

echo Testing Course Management DB (port 5434)...
docker exec postgres-course-management psql -U %DB_USER% -d course_management_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Course Management DB) else (echo [FAILED] Course Management DB)

echo Testing Assessment Management DB (port 5435)...
docker exec postgres-assessment-management psql -U %DB_USER% -d assessment_management_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Assessment Management DB) else (echo [FAILED] Assessment Management DB)

echo Testing Assessment Execution DB (port 5436)...
docker exec postgres-assessment-execution psql -U %DB_USER% -d assessment_execution_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Assessment Execution DB) else (echo [FAILED] Assessment Execution DB)

echo Testing Course Delivery DB (port 5437)...
docker exec postgres-course-delivery psql -U %DB_USER% -d course_delivery_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Course Delivery DB) else (echo [FAILED] Course Delivery DB)

echo Testing Student Personalization DB (port 5438)...
docker exec postgres-student-personalization psql -U %DB_USER% -d student_personalization_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Student Personalization DB) else (echo [FAILED] Student Personalization DB)

echo Testing Tracking DB (port 5439)...
docker exec postgres-tracking psql -U %DB_USER% -d tracking_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Tracking DB) else (echo [FAILED] Tracking DB)

echo Testing Enrollment DB (port 5440)...
docker exec postgres-enrollment psql -U %DB_USER% -d enrollment_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Enrollment DB) else (echo [FAILED] Enrollment DB)

echo Testing Notification DB (port 5441)...
docker exec postgres-notification psql -U %DB_USER% -d notification_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Notification DB) else (echo [FAILED] Notification DB)

echo Testing Communication DB (port 5442)...
docker exec postgres-communication psql -U %DB_USER% -d communication_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Communication DB) else (echo [FAILED] Communication DB)

echo Testing Analytics DB (port 5443)...
docker exec postgres-analytics psql -U %DB_USER% -d analytics_db -c "SELECT 'OK' as status;" 2>nul
if %errorlevel% equ 0 (echo [OK] Analytics DB) else (echo [FAILED] Analytics DB)

echo.
echo =========================================
echo Connection Test Complete
echo =========================================

pause

