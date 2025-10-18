#!/bin/bash
# Script to test all PostgreSQL database connections

echo "========================================="
echo "Testing PostgreSQL Database Connections"
echo "========================================="

DB_USER="lms_user"

echo ""
echo "Testing Authentication DB (port 5432)..."
if docker exec postgres-authentication psql -U $DB_USER -d authentication_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Authentication DB"
else
    echo "❌ [FAILED] Authentication DB"
fi

echo "Testing User Management DB (port 5433)..."
if docker exec postgres-user-management psql -U $DB_USER -d user_management_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] User Management DB"
else
    echo "❌ [FAILED] User Management DB"
fi

echo "Testing Course Management DB (port 5434)..."
if docker exec postgres-course-management psql -U $DB_USER -d course_management_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Course Management DB"
else
    echo "❌ [FAILED] Course Management DB"
fi

echo "Testing Assessment Management DB (port 5435)..."
if docker exec postgres-assessment-management psql -U $DB_USER -d assessment_management_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Assessment Management DB"
else
    echo "❌ [FAILED] Assessment Management DB"
fi

echo "Testing Assessment Execution DB (port 5436)..."
if docker exec postgres-assessment-execution psql -U $DB_USER -d assessment_execution_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Assessment Execution DB"
else
    echo "❌ [FAILED] Assessment Execution DB"
fi

echo "Testing Course Delivery DB (port 5437)..."
if docker exec postgres-course-delivery psql -U $DB_USER -d course_delivery_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Course Delivery DB"
else
    echo "❌ [FAILED] Course Delivery DB"
fi

echo "Testing Student Personalization DB (port 5438)..."
if docker exec postgres-student-personalization psql -U $DB_USER -d student_personalization_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Student Personalization DB"
else
    echo "❌ [FAILED] Student Personalization DB"
fi

echo "Testing Tracking DB (port 5439)..."
if docker exec postgres-tracking psql -U $DB_USER -d tracking_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Tracking DB"
else
    echo "❌ [FAILED] Tracking DB"
fi

echo "Testing Enrollment DB (port 5440)..."
if docker exec postgres-enrollment psql -U $DB_USER -d enrollment_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Enrollment DB"
else
    echo "❌ [FAILED] Enrollment DB"
fi

echo "Testing Notification DB (port 5441)..."
if docker exec postgres-notification psql -U $DB_USER -d notification_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Notification DB"
else
    echo "❌ [FAILED] Notification DB"
fi

echo "Testing Communication DB (port 5442)..."
if docker exec postgres-communication psql -U $DB_USER -d communication_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Communication DB"
else
    echo "❌ [FAILED] Communication DB"
fi

echo "Testing Analytics DB (port 5443)..."
if docker exec postgres-analytics psql -U $DB_USER -d analytics_db -c "SELECT 'OK' as status;" > /dev/null 2>&1; then
    echo "✅ [OK] Analytics DB"
else
    echo "❌ [FAILED] Analytics DB"
fi

echo ""
echo "========================================="
echo "Connection Test Complete"
echo "========================================="

