#!/bin/bash

# Script to start all microservices in order
# Make sure to run: chmod +x start-all-services.sh

echo "========================================="
echo "Starting LMS Microservices"
echo "========================================="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    
    echo -e "${BLUE}Starting ${service_name} on port ${port}...${NC}"
    cd ${service_name}
    ../mvnw spring-boot:run > ../logs/${service_name}.log 2>&1 &
    cd ..
    sleep 3
}

# Create logs directory
mkdir -p logs

# Step 1: Start Eureka Server (Service Discovery)
echo -e "${GREEN}Step 1: Starting Eureka Server...${NC}"
start_service "eureka-server" "8761"
echo "Waiting for Eureka Server to start (30 seconds)..."
sleep 30

# Step 2: Start Config Server
echo -e "${GREEN}Step 2: Starting Config Server...${NC}"
start_service "config-server" "8888"
sleep 10

# Step 3: Start API Gateway
echo -e "${GREEN}Step 3: Starting API Gateway...${NC}"
start_service "api-gateway" "8080"
sleep 10

# Step 4: Start Business Services
echo -e "${GREEN}Step 4: Starting Business Services...${NC}"

start_service "authentication-service" "8081"
start_service "user-management-service" "8082"
start_service "course-management-service" "8083"
start_service "assessment-management-service" "8084"
start_service "assessment-execution-service" "8085"
start_service "course-delivery-service" "8086"
start_service "student-personalization-service" "8087"
start_service "tracking-service" "8088"
start_service "enrollment-service" "8089"
start_service "notification-service" "8090"
start_service "communication-service" "8091"
start_service "analytics-reporting-service" "8092"

echo ""
echo "========================================="
echo -e "${GREEN}All services started!${NC}"
echo "========================================="
echo ""
echo "Service URLs:"
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - API Gateway: http://localhost:8080"
echo "  - Config Server: http://localhost:8888"
echo ""
echo "Logs are available in ./logs/ directory"
echo ""
echo "To stop all services, run: ./stop-all-services.sh"
echo "========================================="

