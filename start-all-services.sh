#!/bin/bash

# Script to start all microservices in order (Foreground mode)
# Press Ctrl+C to stop all services
# Make sure to run: chmod +x start-all-services.sh

COLOR_RESET='\033[0m'
COLOR_GREEN='\033[0;32m'
COLOR_BLUE='\033[0;34m'
COLOR_YELLOW='\033[1;33m'
COLOR_RED='\033[0;31m'

# Array to store all PIDs
declare -a PIDS=()

# Function to cleanup on exit
cleanup() {
    echo ""
    echo -e "${COLOR_YELLOW}=========================================${COLOR_RESET}"
    echo -e "${COLOR_YELLOW}Stopping all services...${COLOR_RESET}"
    echo -e "${COLOR_YELLOW}=========================================${COLOR_RESET}"
    
    for pid in "${PIDS[@]}"; do
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "${COLOR_RED}Killing process ${pid}...${COLOR_RESET}"
            kill -9 $pid 2>/dev/null
        fi
    done
    
    echo -e "${COLOR_GREEN}All services stopped!${COLOR_RESET}"
    exit 0
}

# Trap Ctrl+C (SIGINT) and SIGTERM
trap cleanup SIGINT SIGTERM

# Function to start a service
start_service() {
    local service_name=$1
    local port=$2
    
    echo -e "${COLOR_BLUE}Starting ${service_name} on port ${port}...${COLOR_RESET}"
    cd ${service_name}
    ../mvnw spring-boot:run > ../logs/${service_name}.log 2>&1 &
    local pid=$!
    PIDS+=($pid)
    echo -e "  PID: ${pid}"
    cd ..
    sleep 3
}

echo "========================================="
echo "Starting LMS Microservices (Foreground)"
echo "Press Ctrl+C to stop all services"
echo "========================================="

# Create logs directory
mkdir -p logs

# Step 1: Start Eureka Server (Service Discovery)
echo -e "${COLOR_GREEN}[1/4] Starting Eureka Server...${COLOR_RESET}"
start_service "eureka-server" "8761"
echo "Waiting for Eureka Server to start (30 seconds)..."
sleep 30

# Step 2: Start Config Server
echo -e "${COLOR_GREEN}[2/4] Starting Config Server...${COLOR_RESET}"
start_service "config-server" "8888"
sleep 10

# Step 3: Start API Gateway
echo -e "${COLOR_GREEN}[3/4] Starting API Gateway...${COLOR_RESET}"
start_service "api-gateway" "8080"
sleep 10

# Step 4: Start Business Services
echo -e "${COLOR_GREEN}[4/4] Starting Business Services...${COLOR_RESET}"

start_service "authentication-service" "8081"
start_service "user-management-service" "8082"
start_service "course-management-service" "8083"
start_service "assessment-service" "8084"
start_service "learning-service" "8086"
start_service "personalization-service" "8087"
start_service "notification-service" "8090"
start_service "communication-service" "8091"

echo ""
echo "========================================="
echo -e "${COLOR_GREEN}✓ All services started!${COLOR_RESET}"
echo "========================================="
echo ""
echo "Quick Access:"
echo "  • Eureka Dashboard: http://localhost:8761"
echo "  • API Gateway:      http://localhost:8080"
echo ""
echo "Management:"
echo "  • Check status:     ./check-services.sh"
echo "  • Stop one service: ./stop-service.sh <service-name>"
echo "  • Stop all:         Press Ctrl+C"
echo ""
echo "Logs: ./logs/"
echo ""
echo -e "${COLOR_YELLOW}Services running in foreground...${COLOR_RESET}"
echo -e "${COLOR_YELLOW}Press Ctrl+C to stop all services${COLOR_RESET}"
echo "========================================="

# Wait for all background processes
wait
