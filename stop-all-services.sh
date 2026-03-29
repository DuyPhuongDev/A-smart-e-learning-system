#!/bin/bash

# Script to stop all running LMS Microservices
# Usage: 
#   ./stop-all-services.sh           # Stop all services
#   ./stop-all-services.sh business  # Stop only business services
#   ./stop-all-services.sh infra     # Stop only infrastructure services
#
# Make sure to run: chmod +x stop-all-services.sh

COLOR_RESET='\033[0m'
COLOR_GREEN='\033[0;32m'
COLOR_YELLOW='\033[1;33m'
COLOR_RED='\033[0;31m'
COLOR_BLUE='\033[0;34m'

# Service definitions with ports and names
declare -A INFRA_SERVICES=(
    [8761]="Eureka Server"
    [8888]="Config Server"
    [8080]="API Gateway"
)

declare -A BUSINESS_SERVICES=(
    [8081]="Authentication Service"
    [8082]="User Management Service"
    [8083]="Course Management Service"
    [8084]="Notification Service"
    [8085]="Communication Service"
    [8086]="Assessment Service"
    [8087]="Learning Service"
    [8088]="Personalization Service"
)

stop_service() {
    local port=$1
    local name=$2
    
    pid=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pid" ]; then
        kill -9 $pid 2>/dev/null
        echo -e "${COLOR_GREEN}✓${COLOR_RESET} Stopped ${COLOR_BLUE}${name}${COLOR_RESET} (port ${port}, PID ${pid})"
        return 0
    else
        echo -e "${COLOR_YELLOW}⊘${COLOR_RESET} No process found for ${COLOR_BLUE}${name}${COLOR_RESET} (port ${port})"
        return 1
    fi
}

stop_services() {
    local -n services=$1
    local stopped=0
    
    for port in "${!services[@]}"; do
        stop_service $port "${services[$port]}"
        if [ $? -eq 0 ]; then
            ((stopped++))
        fi
    done
    
    return $stopped
}

print_header() {
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    echo -e "${COLOR_BLUE}   LMS Microservices - Stop Script${COLOR_RESET}"
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    echo ""
}

print_footer() {
    local total=$1
    echo ""
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    if [ $total -gt 0 ]; then
        echo -e "${COLOR_GREEN}✓ Successfully stopped ${total} service(s)${COLOR_RESET}"
    else
        echo -e "${COLOR_YELLOW}⊘ No services were running${COLOR_RESET}"
    fi
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
}

# Main execution
print_header

MODE=${1:-all}
TOTAL_STOPPED=0

case $MODE in
    business)
        echo -e "${COLOR_YELLOW}Stopping Business Services only...${COLOR_RESET}"
        echo ""
        stop_services BUSINESS_SERVICES
        TOTAL_STOPPED=$?
        ;;
    
    infra)
        echo -e "${COLOR_YELLOW}Stopping Infrastructure Services only...${COLOR_RESET}"
        echo ""
        stop_services INFRA_SERVICES
        TOTAL_STOPPED=$?
        ;;
    
    all)
        echo -e "${COLOR_YELLOW}Stopping All Services...${COLOR_RESET}"
        echo ""
        
        echo -e "${COLOR_BLUE}[1/2]${COLOR_RESET} Business Services:"
        stop_services BUSINESS_SERVICES
        business_stopped=$?
        
        echo ""
        echo -e "${COLOR_BLUE}[2/2]${COLOR_RESET} Infrastructure Services:"
        stop_services INFRA_SERVICES
        infra_stopped=$?
        
        TOTAL_STOPPED=$((business_stopped + infra_stopped))
        ;;
    
    *)
        echo -e "${COLOR_RED}Error: Invalid argument '$MODE'${COLOR_RESET}"
        echo "Usage: $0 [all|business|infra]"
        exit 1
        ;;
esac

print_footer $TOTAL_STOPPED

# Optional: Stop Docker containers if running
# echo ""
# echo -e "${COLOR_YELLOW}Stopping Docker containers...${COLOR_RESET}"
# docker-compose down

