#!/bin/bash

# Script to check status of all LMS Microservices
# Usage: ./check-services.sh

COLOR_RESET='\033[0m'
COLOR_GREEN='\033[0;32m'
COLOR_RED='\033[0;31m'
COLOR_BLUE='\033[0;34m'
COLOR_YELLOW='\033[1;33m'

# Service definitions with ports and names
declare -A ALL_SERVICES=(
    [8761]="Eureka Server"
    [8888]="Config Server"
    [8080]="API Gateway"
    [8081]="Authentication Service"
    [8082]="User Management Service"
    [8083]="Course Management Service"
    [8084]="Assessment Service"
    [8086]="Learning Service"
    [8087]="Personalization Service"
    [8090]="Notification Service"
    [8091]="Communication Service"
)

check_service() {
    local port=$1
    local name=$2
    
    pid=$(lsof -ti:$port 2>/dev/null)
    if [ ! -z "$pid" ]; then
        # Get memory usage
        mem=$(ps -p $pid -o rss= 2>/dev/null | awk '{printf "%.0f MB", $1/1024}')
        echo -e "${COLOR_GREEN}●${COLOR_RESET} ${COLOR_BLUE}${name}${COLOR_RESET} (port ${port}) - ${COLOR_GREEN}RUNNING${COLOR_RESET} [PID: ${pid}, MEM: ${mem}]"
        return 0
    else
        echo -e "${COLOR_RED}○${COLOR_RESET} ${COLOR_BLUE}${name}${COLOR_RESET} (port ${port}) - ${COLOR_RED}STOPPED${COLOR_RESET}"
        return 1
    fi
}

print_header() {
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    echo -e "${COLOR_BLUE}   LMS Microservices - Status Check${COLOR_RESET}"
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    echo ""
}

print_summary() {
    local running=$1
    local total=$2
    local stopped=$((total - running))
    
    echo ""
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
    echo -e "${COLOR_BLUE}Summary:${COLOR_RESET}"
    echo -e "  ${COLOR_GREEN}●${COLOR_RESET} Running:  ${COLOR_GREEN}${running}${COLOR_RESET} / ${total}"
    echo -e "  ${COLOR_RED}○${COLOR_RESET} Stopped:  ${COLOR_RED}${stopped}${COLOR_RESET} / ${total}"
    echo -e "${COLOR_BLUE}=========================================${COLOR_RESET}"
}

# Main execution
print_header

running_count=0
total_count=${#ALL_SERVICES[@]}

echo -e "${COLOR_YELLOW}Infrastructure Services:${COLOR_RESET}"
for port in 8761 8888 8080; do
    check_service $port "${ALL_SERVICES[$port]}"
    if [ $? -eq 0 ]; then
        ((running_count++))
    fi
done

echo ""
echo -e "${COLOR_YELLOW}Business Services:${COLOR_RESET}"
for port in 8081 8082 8083 8084 8086 8087 8090 8091; do
    check_service $port "${ALL_SERVICES[$port]}"
    if [ $? -eq 0 ]; then
        ((running_count++))
    fi
done

print_summary $running_count $total_count

# Show URLs for running services
if [ $running_count -gt 0 ]; then
    echo ""
    echo -e "${COLOR_YELLOW}Quick Access:${COLOR_RESET}"
    
    if lsof -ti:8761 >/dev/null 2>&1; then
        echo "  • Eureka Dashboard: http://localhost:8761"
    fi
    
    if lsof -ti:8080 >/dev/null 2>&1; then
        echo "  • API Gateway:      http://localhost:8080"
    fi
    
    echo ""
    echo -e "${COLOR_YELLOW}Management:${COLOR_RESET}"
    echo "  • Stop all:      ./stop-all-services.sh"
    echo "  • Start all:     ./start-all-services.sh"
fi

# Exit code based on status
if [ $running_count -eq $total_count ]; then
    exit 0  # All services running
elif [ $running_count -eq 0 ]; then
    exit 2  # No services running
else
    exit 1  # Some services running
fi

