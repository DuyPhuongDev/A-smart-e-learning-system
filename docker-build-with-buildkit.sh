#!/bin/bash

# ================================================================
# Docker Build Script with BuildKit Cache Mount
# ================================================================
# This script enables BuildKit to use cache mount for Maven dependencies
# which significantly speeds up builds when dependencies change
# ================================================================

echo ""
echo "================================================================"
echo "Building Docker Images with BuildKit Cache Mount"
echo "================================================================"
echo ""

# Enable Docker BuildKit
export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1

echo "BuildKit enabled: DOCKER_BUILDKIT=1"
echo ""

# Check if specific service is provided
if [ -z "$1" ]; then
    echo "Building all services..."
    echo ""
    docker-compose build
else
    echo "Building service: $1"
    echo ""
    docker-compose build "$1"
fi

if [ $? -eq 0 ]; then
    echo ""
    echo "================================================================"
    echo "Build completed successfully!"
    echo "================================================================"
    echo ""
    echo "Maven dependencies are now cached and will persist across builds."
    echo "Next builds will be much faster when only dependencies change."
    echo ""
    echo "To start services: docker-compose up"
    echo "To rebuild a specific service: $0 [service-name]"
    echo ""
else
    echo ""
    echo "================================================================"
    echo "Build failed! Check the error messages above."
    echo "================================================================"
    echo ""
    exit 1
fi

