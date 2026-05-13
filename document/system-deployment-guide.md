# Mo ta trien khai he thong LMS (BE + FE Next.js)

Tai lieu nay mo ta cach trien khai tong the he thong LMS gom Backend microservices va Frontend Next.js, phu hop cho ca local/dev va production.

## 1) Thanh phan he thong

### Backend (Spring Boot microservices)
- `api-gateway`: diem vao duy nhat cho client.
- `eureka-server`: service discovery.
- `authentication-service`
- `user-management-service`
- `course-management-service`
- `assessment-service`
- `learning-service`
- `personalization-service`
- `notification-service`
- `coaching-chatbot-service`

### Ha tang dung chung
- `postgres` (co `pgvector`) cho du lieu quan he.
- `kafka-1` cho event streaming.
- `kafka-ui` de quan sat topic/message.
- `cloud-flared-tunnel` (neu dung Cloudflare Tunnel de expose ra Internet).

### Frontend
- `nextjs-frontend` (Next.js): giao dien nguoi dung (student, instructor, admin).

### Dich vu ngoai
- AWS S3 (luu file/model)
- AWS SQS (queue training jobs)
- Gemini API
- Qdrant
- SMTP provider

## 2) Luong truy cap tong quan

1. Nguoi dung truy cap FE Next.js bang HTTPS.
2. FE goi API qua `api-gateway`.
3. `api-gateway` route request den tung microservice.
4. Cac service dang ky/tra cuu qua `eureka-server`.
5. Du lieu nghiep vu luu trong Postgres; su kien bat dong bo di qua Kafka.

## 3) Trien khai bang Docker Compose (hien tai)

### 3.1 Kien truc deploy
- Tat ca service chay trong cung 1 docker network (`lms-network`).
- Cac port BE map ra host de debug/dev.
- FE Next.js co the chay:
  - cung `docker-compose.yml`, hoac
  - chay rieng (Vercel/Node server) va tro ve domain gateway.

### 3.2 Goi y them service FE vao compose

```yaml
nextjs-frontend:
  build:
    context: ../LMS-HCMUT_FE
    dockerfile: Dockerfile
  container_name: nextjs-frontend
  ports:
    - "3000:3000"
  environment:
    - NEXT_PUBLIC_API_BASE_URL=https://lms-api.your-domain.com
  networks:
    - lms-network
```

Luu y:
- `NEXT_PUBLIC_API_BASE_URL` phai tro den domain/public endpoint cua `api-gateway`.
- Neu FE va BE khac domain, can cau hinh CORS tai `api-gateway`.

## 4) Bien moi truong quan trong

### Backend
- DB: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- Service discovery: `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`
- JWT: `JWT_SECRET`
- Kafka: `KAFKA_BOOTSTRAP_SERVERS`
- Learning model cache: `MODEL_CACHE_DIR` (vi du `/app/models`)
- External APIs: `GEMINI_API_KEY`, `QDRANT_HOST`, `QDRANT_API_KEY`, `MAIL_*`

### Frontend Next.js
- `NEXT_PUBLIC_API_BASE_URL`: base URL cho goi API
- Cac bien public khac (`NEXT_PUBLIC_*`) cho thong tin can expose len browser
- Secret phia server-side FE (khong `NEXT_PUBLIC_`) de trong env runtime/an toan

## 5) Persistent data va storage

- Postgres: can volume/PV rieng, backup dinh ky.
- Kafka: can volume cho broker data.
- Learning model cache:
  - trong container: `/app/models`
  - nen mount volume de khong mat khi recreate container.

Vi du volume cho `learning-service`:

```yaml
learning-service:
  volumes:
    - ./learning-service/models:/app/models
```

## 6) Quy trinh deploy production (de xuat)

1. Build image cho tung service (BE + FE) va push registry.
2. Deploy ha tang truoc: Postgres/Kafka (hoac dung managed services).
3. Deploy `eureka-server` -> `api-gateway` -> business services.
4. Deploy FE Next.js, tro API ve gateway public URL.
5. Cau hinh domain + TLS (Cloudflare/Nginx/Ingress).
6. Kiem tra health endpoints, log, metrics va alert.

## 7) Monitoring, logging, reliability

- Bat `actuator/health` cho tung service.
- Them readiness/liveness check (neu len K8s).
- Tap trung log (ELK/Loki) va metrics (Prometheus + Grafana).
- Dat resource limits de tranh 1 service chiem het tai nguyen.
- Co chinh sach restart va retry cho call lien service.

## 8) Bao mat

- Khong hard-code secrets trong repo.
- Dung Secret Manager hoac bien moi truong tu CI/CD.
- Gioi han network access giua cac thanh phan.
- Bat HTTPS end-to-end cho traffic public.
- Rotate key/dong bo credential dinh ky.

## 9) Dinh huong khi chuyen len Kubernetes

- FE Next.js: `Deployment` + `Service` + `Ingress`
- Moi backend service: `Deployment` + `Service`
- Postgres/Kafka: uu tien managed, neu tu quan ly thi `StatefulSet` + PVC
- ConfigMap/Secret thay cho env hard-coded
- HPA de auto scale cho `api-gateway`, FE va cac service tai cao

---

Tai lieu lien quan:
- `document/deployment-diagram.puml`
- `docker-compose.yml`
