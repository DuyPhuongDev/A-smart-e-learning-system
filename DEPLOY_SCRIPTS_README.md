# Deploy Scripts README

File này mô tả cách dùng 3 script:

- `build-all-images.sh`
- `push-all-images.sh`
- `redeploy-gke.sh`

Các script này phục vụ deploy backend lên GKE với cấu hình hiện tại của project.

## 1. Vị trí chạy

Chạy trong thư mục:

```bash
cd /home/phoenix/capstone_project/Capstone_Project_LMS-HCMUT_BE
```

## 2. Tool cần có

### Build/push image

```bash
docker
```

### Redeploy lên GKE

```bash
gcloud
kubectl
```

## 3. Giá trị mặc định

Nếu bạn không truyền biến môi trường, script sẽ dùng:

```bash
PROJECT_ID=capstone-project-495404
REGION=asia-southeast1
REPO=lms
CLUSTER=lms-cluster
NAMESPACE=lms
TAG=v1
USE_IN_CLUSTER_QDRANT=false
```

Bạn có thể override bằng env var khi chạy script.

Ví dụ:

```bash
TAG=v2 PROJECT_ID=my-project ./build-all-images.sh
```

## 4. build-all-images.sh

Script này build toàn bộ image backend.

### Cách chạy

```bash
./build-all-images.sh
```

Hoặc dùng tag khác:

```bash
TAG=v2 ./build-all-images.sh
```

### Script này làm gì

- build các service:
  - `eureka-server`
  - `api-gateway`
  - `authentication-service`
  - `user-management-service`
  - `course-management-service`
  - `learning-service`
  - `assessment-service`
  - `personalization-service`
  - `notification-service`
  - `communication-service`
  - `coaching-chatbot-service`
- gắn tag image theo format:

```text
asia-southeast1-docker.pkg.dev/<PROJECT_ID>/<REPO>/<service>:<TAG>
```

### Khi nào dùng

- khi code mới
- khi Dockerfile đổi
- trước khi push image mới

## 5. push-all-images.sh

Script này push toàn bộ image backend đã build lên Artifact Registry.

### Cách chạy

```bash
./push-all-images.sh
```

Hoặc:

```bash
TAG=v2 ./push-all-images.sh
```

### Khi nào dùng

- sau khi build image mới
- trước khi redeploy cluster bằng image tag mới

## 6. redeploy-gke.sh

Script này apply toàn bộ manifest Kubernetes lên cluster.

### Cách chạy cơ bản

```bash
./redeploy-gke.sh
```

### Nếu cluster đã bị xóa

```bash
./redeploy-gke.sh --create-cluster
```

### Script này làm gì

1. lấy credentials của cluster
2. apply:
   - `k8s/namespace.yaml`
   - `k8s/secret.yaml`
   - `k8s/configmap.yaml`
3. apply hạ tầng:
   - `k8s/infra/postgres.yaml`
   - `k8s/infra/kafka.yaml`
   - `k8s/infra/qdrant.yaml` nếu `USE_IN_CLUSTER_QDRANT=true`
4. chờ infra lên
5. apply `eureka-server`
6. apply `backend-services`
7. chờ rollout từng deployment
8. apply ingress
9. in ra trạng thái cuối

### Lưu ý

- Script yêu cầu file `k8s/secret.yaml` đã tồn tại.
- Mặc định script không deploy Qdrant trong cluster vì hiện tại hệ thống đang dùng Qdrant Cloud.

## 7. Quy trình thường dùng

### Case A: code không đổi, chỉ tạo lại cluster

```bash
./redeploy-gke.sh --create-cluster
```

### Case B: code có đổi

```bash
TAG=v2 ./build-all-images.sh
TAG=v2 ./push-all-images.sh
./redeploy-gke.sh
```

Lưu ý: nếu code đổi, bạn nên dùng tag mới như `v2`, `v3`, hoặc tag theo ngày/commit, không nên ghi đè mãi `v1`.

## 8. Kiểm tra sau deploy

### Xem deployment

```bash
kubectl get deployments -n lms
```

### Xem pod

```bash
kubectl get pods -n lms
```

### Xem ingress

```bash
kubectl get ingress -n lms
```

### Test health

```bash
curl http://<INGRESS_IP>/actuator/health
```

### Test login

```bash
curl -H 'Content-Type: application/json' \
  -d '{"email":"admin@hcmut.edu.vn","password":"admin123"}' \
  http://<INGRESS_IP>/api/auth/login
```

## 9. Ví dụ hoàn chỉnh

### Deploy bản mới `v2`

```bash
cd /home/phoenix/capstone_project/Capstone_Project_LMS-HCMUT_BE

TAG=v2 ./build-all-images.sh
TAG=v2 ./push-all-images.sh
./redeploy-gke.sh
```

### Dựng lại toàn bộ sau khi xóa cluster

```bash
cd /home/phoenix/capstone_project/Capstone_Project_LMS-HCMUT_BE

./redeploy-gke.sh --create-cluster
```

## 10. Một giới hạn hiện tại

`redeploy-gke.sh` chỉ apply manifest. Nó không tự sửa tag image trong YAML.

Nếu bạn push image mới với tag khác `v1`, bạn cần:

- hoặc sửa image tag trong file YAML trước khi redeploy
- hoặc sau này tôi có thể làm tiếp một script update tag tự động cho bạn
