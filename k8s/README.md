# LMS Backend GKE Deployment

This folder contains Kubernetes manifests for deploying the backend stack to GKE.

## 1. Required local tools

Install or enable these on your machine before deploying:

```bash
gcloud --version
kubectl version --client
docker --version
```

In the current WSL environment, `gcloud`, `kubectl`, and Docker are not available yet.

## 2. Set variables

```bash
export PROJECT_ID=your-gcp-project-id
export REGION=asia-southeast1
export CLUSTER=lms-cluster
export REPO=lms
export TAG=v1
```

## 3. Prepare GCP

```bash
gcloud config set project $PROJECT_ID

gcloud services enable \
  container.googleapis.com \
  artifactregistry.googleapis.com \
  compute.googleapis.com

gcloud artifacts repositories create $REPO \
  --repository-format=docker \
  --location=$REGION \
  --description="LMS backend images"

gcloud auth configure-docker $REGION-docker.pkg.dev

gcloud container clusters create-auto $CLUSTER \
  --region=$REGION

gcloud container clusters get-credentials $CLUSTER \
  --region=$REGION
```

## 4. Build and push backend images

Run from `Capstone_Project_LMS-HCMUT_BE`:

```bash
for svc in \
  eureka-server \
  api-gateway \
  authentication-service \
  user-management-service \
  course-management-service \
  learning-service \
  assessment-service \
  personalization-service \
  notification-service \
  communication-service \
  coaching-chatbot-service
do
  docker build \
    -t $REGION-docker.pkg.dev/$PROJECT_ID/$REPO/$svc:$TAG \
    -f $svc/Dockerfile .

  docker push $REGION-docker.pkg.dev/$PROJECT_ID/$REPO/$svc:$TAG
done
```

Or use the helper scripts:

```bash
TAG=v2 ./build-all-images.sh
TAG=v2 ./push-all-images.sh
```

## 5. Replace image placeholders

The manifests use image placeholders:

```text
REGION-docker.pkg.dev/PROJECT_ID/lms/<service>:TAG
```

Replace them:

```bash
find k8s/apps -type f -name '*.yaml' -print0 | xargs -0 sed -i \
  -e "s|REGION|$REGION|g" \
  -e "s|PROJECT_ID|$PROJECT_ID|g" \
  -e "s|TAG|$TAG|g"
```

If your Artifact Registry repo is not named `lms`, also replace `/lms/`.

## 6. Create real secrets

Copy the example and edit real values:

```bash
cp k8s/secret.example.yaml k8s/secret.yaml
```

Edit:

```text
DB_PASSWORD
JWT_SECRET
GEMINI_API_KEY
QDRANT_API_KEY
MAIL_USERNAME
MAIL_PASSWORD
AWS credentials
```

Do not commit `k8s/secret.yaml`.

## 7. Update frontend/domain config

Edit `k8s/configmap.yaml`:

```text
NOTIFICATION_WS_ALLOWED_ORIGINS
CORS_ALLOWED_ORIGINS
FRONT_END
```

Edit `k8s/ingress.yaml`:

```text
api.your-domain.com
```

Frontend should use:

```text
NEXT_PUBLIC_API_BASE_URL=https://api.your-domain.com
```

## 8. Apply manifests

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/configmap.yaml

kubectl apply -f k8s/infra/postgres.yaml
kubectl apply -f k8s/infra/kafka.yaml
kubectl apply -f k8s/infra/qdrant.yaml

kubectl wait --for=condition=ready pod -l app=postgres -n lms --timeout=180s
kubectl wait --for=condition=ready pod -l app=kafka -n lms --timeout=180s
kubectl wait --for=condition=ready pod -l app=qdrant -n lms --timeout=180s

kubectl apply -f k8s/apps/eureka-server.yaml
kubectl wait --for=condition=available deployment/eureka-server -n lms --timeout=180s

kubectl apply -f k8s/apps/backend-services.yaml
kubectl apply -f k8s/ingress.yaml
```

Or redeploy with one script:

```bash
./redeploy-gke.sh
```

If you deleted the cluster and need to create it again:

```bash
./redeploy-gke.sh --create-cluster
```

## 9. Check deployment

```bash
kubectl get pods -n lms
kubectl get svc -n lms
kubectl get ingress -n lms
```

Logs:

```bash
kubectl logs -n lms deploy/api-gateway
kubectl logs -n lms deploy/authentication-service
kubectl logs -n lms statefulset/postgres
kubectl logs -n lms statefulset/kafka
kubectl logs -n lms statefulset/qdrant
```

## 10. Important notes

- Only `api-gateway` is public through Ingress.
- Business services are internal ClusterIP services.
- Postgres, Kafka, and Qdrant are single-node in-cluster deployments, suitable for capstone/demo usage.
- For production, use Cloud SQL, managed Kafka, and Qdrant Cloud or a properly operated StatefulSet.
