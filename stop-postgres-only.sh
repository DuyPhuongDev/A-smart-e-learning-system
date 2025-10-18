#!/bin/bash
# Script to stop PostgreSQL databases on Linux/Mac
# Usage: ./stop-postgres-only.sh

echo "========================================="
echo "Stopping PostgreSQL Databases"
echo "========================================="

echo ""
echo "Stopping all PostgreSQL containers..."
docker-compose stop postgres-authentication postgres-user-management postgres-course-management postgres-assessment-management postgres-assessment-execution postgres-course-delivery postgres-student-personalization postgres-tracking postgres-enrollment postgres-notification postgres-communication postgres-analytics

echo ""
echo "========================================="
echo "PostgreSQL Databases Stopped!"
echo "========================================="
echo ""
echo "To completely remove containers (keep data):"
echo "  docker-compose down"
echo ""
echo "To remove containers AND delete all data:"
echo "  docker-compose down -v"
echo ""
echo "To restart databases:"
echo "  ./start-postgres-only.sh"
echo ""
echo "========================================="

