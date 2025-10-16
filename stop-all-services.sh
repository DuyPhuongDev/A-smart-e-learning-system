#!/bin/bash

# Script to stop all running Spring Boot services
# Make sure to run: chmod +x stop-all-services.sh

echo "========================================="
echo "Stopping LMS Microservices"
echo "========================================="

# Find and kill all Spring Boot processes
echo "Finding and stopping all Spring Boot services..."

# Kill processes by port
ports=(8761 8888 8080 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090 8091 8092)

for port in "${ports[@]}"; do
    echo "Stopping service on port $port..."
    pid=$(lsof -ti:$port)
    if [ ! -z "$pid" ]; then
        kill -9 $pid
        echo "  Stopped process $pid on port $port"
    else
        echo "  No process found on port $port"
    fi
done

# Alternative: Kill all java processes (use with caution)
# pkill -f "spring-boot:run"

echo ""
echo "========================================="
echo "All services stopped!"
echo "========================================="

