#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

PROJECT_ID="${PROJECT_ID:-capstone-project-495404}"
REGION="${REGION:-asia-southeast1}"
CLUSTER="${CLUSTER:-lms-cluster}"
NAMESPACE="${NAMESPACE:-lms}"
USE_IN_CLUSTER_QDRANT="${USE_IN_CLUSTER_QDRANT:-false}"

REQUIRED_FILES=(
  "$ROOT_DIR/k8s/namespace.yaml"
  "$ROOT_DIR/k8s/secret.yaml"
  "$ROOT_DIR/k8s/configmap.yaml"
  "$ROOT_DIR/k8s/infra/postgres.yaml"
  "$ROOT_DIR/k8s/infra/kafka.yaml"
  "$ROOT_DIR/k8s/apps/eureka-server.yaml"
  "$ROOT_DIR/k8s/apps/backend-services.yaml"
  "$ROOT_DIR/k8s/ingress.yaml"
)

DEPLOYMENTS=(
  "eureka-server"
  "api-gateway"
  "authentication-service"
  "user-management-service"
  "course-management-service"
  "assessment-service"
  "learning-service"
  "personalization-service"
  "notification-service"
  "communication-service"
  "coaching-chatbot-service"
)

usage() {
  cat <<EOF
Usage:
  ./redeploy-gke.sh [--create-cluster]

Environment overrides:
  PROJECT_ID=$PROJECT_ID
  REGION=$REGION
  CLUSTER=$CLUSTER
  NAMESPACE=$NAMESPACE
  USE_IN_CLUSTER_QDRANT=$USE_IN_CLUSTER_QDRANT

Examples:
  ./redeploy-gke.sh
  TAG=v2 ./build-all-images.sh
  TAG=v2 ./push-all-images.sh
  ./redeploy-gke.sh --create-cluster
EOF
}

CREATE_CLUSTER="false"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --create-cluster)
      CREATE_CLUSTER="true"
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

for cmd in gcloud kubectl; do
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "$cmd is required." >&2
    exit 1
  fi
done

for file in "${REQUIRED_FILES[@]}"; do
  if [[ ! -f "$file" ]]; then
    echo "Missing required file: $file" >&2
    exit 1
  fi
done

echo "PROJECT_ID=$PROJECT_ID"
echo "REGION=$REGION"
echo "CLUSTER=$CLUSTER"
echo "NAMESPACE=$NAMESPACE"
echo "USE_IN_CLUSTER_QDRANT=$USE_IN_CLUSTER_QDRANT"

if [[ "$CREATE_CLUSTER" == "true" ]]; then
  if ! gcloud container clusters describe "$CLUSTER" --region "$REGION" --project "$PROJECT_ID" >/dev/null 2>&1; then
    echo
    echo "==> Creating cluster $CLUSTER"
    gcloud container clusters create-auto "$CLUSTER" \
      --region "$REGION" \
      --project "$PROJECT_ID"
  else
    echo
    echo "==> Cluster $CLUSTER already exists"
  fi
fi

echo
echo "==> Getting cluster credentials"
gcloud container clusters get-credentials "$CLUSTER" \
  --region "$REGION" \
  --project "$PROJECT_ID"

echo
echo "==> Applying namespace, secret, config"
kubectl apply -f "$ROOT_DIR/k8s/namespace.yaml"
kubectl apply -f "$ROOT_DIR/k8s/secret.yaml"
kubectl apply -f "$ROOT_DIR/k8s/configmap.yaml"

echo
echo "==> Applying infra"
kubectl apply -f "$ROOT_DIR/k8s/infra/postgres.yaml"
kubectl apply -f "$ROOT_DIR/k8s/infra/kafka.yaml"
if [[ "$USE_IN_CLUSTER_QDRANT" == "true" ]]; then
  kubectl apply -f "$ROOT_DIR/k8s/infra/qdrant.yaml"
fi

echo
echo "==> Waiting for infra"
kubectl rollout status statefulset/postgres -n "$NAMESPACE" --timeout=300s
kubectl rollout status statefulset/kafka -n "$NAMESPACE" --timeout=300s
if [[ "$USE_IN_CLUSTER_QDRANT" == "true" ]]; then
  kubectl rollout status statefulset/qdrant -n "$NAMESPACE" --timeout=300s
fi

echo
echo "==> Applying Eureka"
kubectl apply -f "$ROOT_DIR/k8s/apps/eureka-server.yaml"
kubectl rollout status deployment/eureka-server -n "$NAMESPACE" --timeout=300s

echo
echo "==> Applying backend services"
kubectl apply -f "$ROOT_DIR/k8s/apps/backend-services.yaml"

for deployment in "${DEPLOYMENTS[@]}"; do
  echo
  echo "==> Waiting for $deployment"
  kubectl rollout status "deployment/$deployment" -n "$NAMESPACE" --timeout=300s
done

echo
echo "==> Applying ingress"
kubectl apply -f "$ROOT_DIR/k8s/ingress.yaml"

echo
echo "==> Final status"
kubectl get deployments -n "$NAMESPACE"
echo
kubectl get pods -n "$NAMESPACE"
echo
kubectl get ingress -n "$NAMESPACE"
