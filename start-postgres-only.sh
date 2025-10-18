#!/bin/bash
# Script to start only PostgreSQL databases on Linux/Mac
# Usage: ./start-postgres-only.sh

echo "========================================="
echo "Starting PostgreSQL Databases Only"
echo "========================================="

echo ""
echo "Starting all PostgreSQL containers..."
docker-compose up -d postgres-authentication postgres-user-management postgres-course-management postgres-assessment-management postgres-assessment-execution postgres-course-delivery postgres-student-personalization postgres-tracking postgres-enrollment postgres-notification postgres-communication postgres-analytics

echo ""
echo "Waiting for databases to be ready (30 seconds)..."
sleep 30

echo ""
echo "========================================="
echo "Database Status:"
echo "========================================="
docker-compose ps | grep postgres

echo ""
echo "========================================="
echo "PostgreSQL Databases Started!"
echo "========================================="
echo ""
echo "Database Connection Details:"
echo "  - Authentication:          localhost:5432 / authentication_db"
echo "  - User Management:         localhost:5433 / user_management_db"
echo "  - Course Management:       localhost:5434 / course_management_db"
echo "  - Assessment Management:   localhost:5435 / assessment_management_db"
echo "  - Assessment Execution:    localhost:5436 / assessment_execution_db"
echo "  - Course Delivery:         localhost:5437 / course_delivery_db"
echo "  - Student Personalization: localhost:5438 / student_personalization_db"
echo "  - Tracking:                localhost:5439 / tracking_db"
echo "  - Enrollment:              localhost:5440 / enrollment_db"
echo "  - Notification:            localhost:5441 / notification_db"
echo "  - Communication:           localhost:5442 / communication_db"
echo "  - Analytics:               localhost:5443 / analytics_db"
echo ""
echo "  Username: lms_user"
echo "  Password: lms_password"
echo ""
echo "You can now start services manually with Maven:"
echo "  cd [service-name]"
echo "  ../mvnw spring-boot:run"
echo ""
echo "========================================="

