#!/bin/bash

# Script to stop a specific LMS Microservice
# Usage: ./stop-service.sh <service-name>
# Example: ./stop-service.sh user-management-service

COLOR_RESET='\033[0m'
COLOR_GREEN='\033[0;32m'
COLOR_RED='\033[0;31m'
COLOR_BLUE='\033[0;34m'
COLOR_YELLOW='\033[1;33m'

# Service to port mapping
declare -A SERVICE_PORTS=(
    ["eureka-server"]="8761"
    ["config-server"]="8888"
    ["api-gateway"]="8080"
    ["authentication-service"]="8081"
    ["user-management-service"]="8082"
    ["course-management-service"]="8083"
    ["assessment-service"]="8084"
    ["learning-service"]="8086"
    ["personalization-service"]="8087"
    ["notification-service"]="8090"
    ["communication-service"]="8091"
)

# Show usage
show_usage() {
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    echo -e "${COLOR_BLUE}Stop LMS Microservice${COLOR_RESET}"
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    echo ""
    echo "Usage: ./stop-service.sh <service-name>"
    echo ""
    echo "Available services:"
    echo ""
    echo "Infrastructure:"
    echo "  • eureka-server"
    echo "  • config-server"
    echo "  • api-gateway"
    echo ""
    echo "Business Services:"
    echo "  • authentication-service"
    echo "  • user-management-service"
    echo "  • course-management-service"
    echo "  • assessment-service"
    echo "  • learning-service"
    echo "  • personalization-service"
    echo "  • notification-service"
    echo "  • communication-service"
    echo ""
    echo "Examples:"
    echo "  ./stop-service.sh user-management-service"
    echo "  ./stop-service.sh eureka-server"
    echo ""
    echo "To stop all services: ./stop-all-services.sh"
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
}

# Stop service by name
stop_service() {
    local service_name=$1
    local port=${SERVICE_PORTS[$service_name]}
    
    if [ -z "$port" ]; then
        echo -e "${COLOR_RED}Error: Unknown service '${service_name}'${COLOR_RESET}"
        echo ""
        show_usage
        exit 1
    fi
    
    echo -e "${COLOR_YELLOW}Stopping ${COLOR_BLUE}${service_name}${COLOR_RESET}${COLOR_YELLOW} on port ${port}...${COLOR_RESET}"
    
    pid=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pid" ]; then
        kill -9 $pid 2>/dev/null
        echo -e "${COLOR_GREEN}✓${COLOR_RESET} Stopped ${COLOR_BLUE}${service_name}${COLOR_RESET} (PID: ${pid})"
        exit 0
    else
        echo -e "${COLOR_YELLOW}⊘${COLOR_RESET} Service ${COLOR_BLUE}${service_name}${COLOR_RESET} is not running"
        exit 1
    fi
}

# Main execution
if [ $# -eq 0 ]; then
    show_usage
    exit 1
fi

SERVICE_NAME=$1

# Handle shortcuts
case $SERVICE_NAME in
    eureka)
        SERVICE_NAME="eureka-server"
        ;;
    config)
        SERVICE_NAME="config-server"
        ;;
    gateway)
        SERVICE_NAME="api-gateway"
        ;;
    auth|authentication)
        SERVICE_NAME="authentication-service"
        ;;
    user|user-management)
        SERVICE_NAME="user-management-service"
        ;;
    course|course-management)
        SERVICE_NAME="course-management-service"
        ;;
    assessment)
        SERVICE_NAME="assessment-service"
        ;;
    learning)
        SERVICE_NAME="learning-service"
        ;;
    personalization)
        SERVICE_NAME="personalization-service"
        ;;
    notification)
        SERVICE_NAME="notification-service"
        ;;
    communication)
        SERVICE_NAME="communication-service"
        ;;
esac

stop_service $SERVICE_NAME

