#!/usr/bin/env bash

set -euo pipefail

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

echo "Pushing backend images"
echo "PROJECT_ID=$PROJECT_ID"
echo "REGION=$REGION"
echo "REPO=$REPO"
echo "TAG=$TAG"

for service in "${SERVICES[@]}"; do
  image="$REGION-docker.pkg.dev/$PROJECT_ID/$REPO/$service:$TAG"
  echo
  echo "==> Pushing $service"
  docker push "$image"
done

echo
echo "Push complete."
