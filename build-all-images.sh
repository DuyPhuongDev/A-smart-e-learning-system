#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PROJECT_ID="${PROJECT_ID:-capstone-project-495404}"
REGION="${REGION:-asia-southeast1}"
REPO="${REPO:-lms}"
TAG="${TAG:-v1}"

SERVICES=(
  "eureka-server"
  "api-gateway"
  "authentication-service"
  "user-management-service"
  "course-management-service"
  "learning-service"
  "assessment-service"
  "personalization-service"
  "notification-service"
  "communication-service"
  "coaching-chatbot-service"
)

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required." >&2
  exit 1
fi

echo "Building backend images"
echo "PROJECT_ID=$PROJECT_ID"
echo "REGION=$REGION"
echo "REPO=$REPO"
echo "TAG=$TAG"

cd "$ROOT_DIR"

for service in "${SERVICES[@]}"; do
  image="$REGION-docker.pkg.dev/$PROJECT_ID/$REPO/$service:$TAG"
  dockerfile="$service/Dockerfile"

  if [[ ! -f "$dockerfile" ]]; then
    echo "Missing Dockerfile: $dockerfile" >&2
    exit 1
  fi

  echo
  echo "==> Building $service"
  docker build -t "$image" -f "$dockerfile" .
done

echo
echo "Build complete."
