# ECS Worker - Video to Transcript

Worker chạy trên AWS Fargate/ECS để xử lý video và tạo transcript sử dụng AWS Transcribe với tính năng tự động phát hiện ngôn ngữ.

## 🏗️ Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Backend API   │────▶│   SQS Queue     │────▶│  ECS/Fargate    │
│ (Send Message)  │     │                 │     │    Worker       │
└─────────────────┘     └─────────────────┘     └────────┬────────┘
                                                         │
                        ┌────────────────────────────────┼────────────────────────────────┐
                        │                                │                                │
                        ▼                                ▼                                ▼
               ┌─────────────────┐             ┌─────────────────┐             ┌─────────────────┐
               │  Download Video │             │  AWS Transcribe │             │ Callback to API │
               │  (S3/YouTube)   │────────────▶│  (Auto Language)│────────────▶│ (Save Transcript)│
               └─────────────────┘             └─────────────────┘             └─────────────────┘
```

## 📋 Features

- **Poll SQS Queue**: Long polling với 20s wait time
- **Auto Exit**: Tự động thoát sau 5 lần poll trống (tiết kiệm chi phí Fargate)
- **Video Sources**: Hỗ trợ S3 (presigned URL) và YouTube
- **Audio Extraction**: Sử dụng yt-dlp (YouTube) và ffmpeg (S3 video)
- **AWS Transcribe**: Tự động phát hiện ngôn ngữ (en-US, vi-VN, ja-JP, ko-KR, zh-CN, etc.)
- **Error Handling**: Retry qua SQS, message thất bại chuyển vào DLQ

## 📦 SQS Message Format

Backend cần gửi message với format sau:

```json
{
  "lectureId": "550e8400-e29b-41d4-a716-446655440000",
  "videoUrl": "https://presigned-s3-url... hoặc https://youtube.com/watch?v=xxx",
  "sourceType": "s3",
  "callbackUrl": "https://api.example.com/api/coachingchatbot/v1/transcription-callback",
  "lectureTitle": "Bài giảng số 1"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `lectureId` | UUID string | ✅ | ID của video lecture |
| `videoUrl` | string | ✅ | Pre-signed S3 URL hoặc YouTube URL |
| `sourceType` | string | ✅ | `s3` hoặc `youtube` |
| `callbackUrl` | string | ✅ | URL để POST kết quả transcript |
| `lectureTitle` | string | ❌ | Tên bài giảng (optional) |

## 📤 Callback Payload

Worker sẽ POST kết quả về `callbackUrl` với payload:

```json
{
  "videoLectureId": "550e8400-e29b-41d4-a716-446655440000",
  "transcriptText": "Full transcript text here...",
  "languageCode": "en-US",
  "audioDuration": 300,
  "wordCount": 1500,
  "startTimeSeconds": 0,
  "endTimeSeconds": 300,
  "segmentIndex": 0
}
```

## 🔧 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `AWS_REGION` | `us-east-1` | AWS Region |
| `SQS_QUEUE_URL` | (required) | SQS Queue URL |
| `S3_BUCKET` | `lms-wecancode` | S3 bucket cho audio temp |
| `S3_AUDIO_PREFIX` | `transcript-videos/` | S3 prefix cho audio files |
| `MAX_EMPTY_POLLS` | `5` | Số lần poll trống trước khi exit |
| `TEMP_DIR` | `/tmp/worker` | Thư mục temp cho downloads |
| `CALLBACK_TIMEOUT` | `30` | Timeout cho callback (seconds) |
| `CALLBACK_RETRIES` | `3` | Số lần retry callback |

## 🐳 Build & Push Docker Image

### 1. Authenticate với ECR

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 211125750777.dkr.ecr.us-east-1.amazonaws.com
```

### 2. Build Docker Image

```bash
cd aws-fargate-worker/ecs-worker-process-video-to-transcript

docker build -t lms/ecs_worker_process_video_to_transcript .
```

### 3. Tag Image

```bash
docker tag lms/ecs_worker_process_video_to_transcript:latest 211125750777.dkr.ecr.us-east-1.amazonaws.com/lms/ecs_worker_process_video_to_transcript:latest
```

### 4. Push to ECR

```bash
docker push 211125750777.dkr.ecr.us-east-1.amazonaws.com/lms/ecs_worker_process_video_to_transcript:latest
```

### One-liner (Build & Push)

```bash
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 211125750777.dkr.ecr.us-east-1.amazonaws.com && \
docker build -t lms/ecs_worker_process_video_to_transcript . && \
docker tag lms/ecs_worker_process_video_to_transcript:latest 211125750777.dkr.ecr.us-east-1.amazonaws.com/lms/ecs_worker_process_video_to_transcript:latest && \
docker push 211125750777.dkr.ecr.us-east-1.amazonaws.com/lms/ecs_worker_process_video_to_transcript:latest
```

## 🔐 IAM Permissions

Task role `SERVICE_ROLE_FARGATE_TASK` cần các permissions sau:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "SQSPermissions",
      "Effect": "Allow",
      "Action": [
        "sqs:ReceiveMessage",
        "sqs:DeleteMessage",
        "sqs:GetQueueAttributes"
      ],
      "Resource": "arn:aws:sqs:us-east-1:211125750777:lms-prod-video-transcription-queue"
    },
    {
      "Sid": "S3Permissions",
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::lms-wecancode/transcript-videos/*"
    },
    {
      "Sid": "TranscribePermissions",
      "Effect": "Allow",
      "Action": [
        "transcribe:StartTranscriptionJob",
        "transcribe:GetTranscriptionJob",
        "transcribe:DeleteTranscriptionJob"
      ],
      "Resource": "*"
    }
  ]
}
```

## 📊 ECS Task Definition

Recommended configuration:

| Setting | Value |
|---------|-------|
| CPU | 1 vCPU |
| Memory | 2 GB |
| Launch Type | FARGATE |
| Platform Version | LATEST |

## 🧪 Local Testing

### Run locally with Docker

```bash
# Build
docker build -t worker-test .

# Run with AWS credentials
docker run --rm \
  -e AWS_ACCESS_KEY_ID=xxx \
  -e AWS_SECRET_ACCESS_KEY=xxx \
  -e AWS_REGION=us-east-1 \
  -e SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/211125750777/lms-prod-video-transcription-queue \
  worker-test
```

### Run locally with Python

```bash
# Create virtual environment
python -m venv venv
source venv/bin/activate  # Linux/Mac
# or: venv\Scripts\activate  # Windows

# Install dependencies
pip install -r requirements.txt
pip install yt-dlp

# Set environment variables
export AWS_REGION=us-east-1
export SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/211125750777/lms-prod-video-transcription-queue

# Run
python -m src.main
```

## 📝 AWS Resources

| Resource | ARN/URL |
|----------|---------|
| ECR Repository | `211125750777.dkr.ecr.us-east-1.amazonaws.com/lms/ecs_worker_process_video_to_transcript` |
| SQS Queue | `arn:aws:sqs:us-east-1:211125750777:lms-prod-video-transcription-queue` |
| SQS DLQ | `arn:aws:sqs:us-east-1:211125750777:lms-prod-video-transcription-dead_letter_queue` |
| S3 Audio Path | `s3://lms-wecancode/transcript-videos/` |
| IAM Role | `SERVICE_ROLE_FARGATE_TASK` |

## 🔄 Flow Diagram

```
1. Backend sends message to SQS
   └── Message contains: lectureId, videoUrl, sourceType, callbackUrl

2. Worker polls SQS (long polling 20s)
   └── If no messages for 5 polls → Exit (Fargate stops, saves cost)

3. Worker downloads video/audio
   ├── YouTube → yt-dlp (downloads audio only, ~5-10MB)
   └── S3 → Download file, extract audio with ffmpeg

4. Worker uploads audio to S3
   └── s3://lms-wecancode/transcript-videos/{lectureId}/{timestamp}.mp3

5. Worker starts AWS Transcribe job
   └── IdentifyLanguage=True (auto-detect: en-US, vi-VN, ja-JP, etc.)

6. Worker polls Transcribe until complete
   └── Exponential backoff: 5s → 10s → 20s → 30s

7. Worker sends results to backend
   └── POST to callbackUrl with VideoTranscriptRequest payload

8. Worker deletes message from SQS
   └── If callback fails → message stays → SQS retries → DLQ after max retries

9. Worker cleans up
   └── Delete audio from S3, delete local temp files
```

## ❗ Error Handling

| Error | Behavior |
|-------|----------|
| Download failed | Message stays in SQS → retry |
| Transcribe failed | Message stays in SQS → retry |
| Callback failed | Message stays in SQS → retry |
| Max retries exceeded | Message moves to DLQ |
| Worker crash | ECS restarts task |

## 📈 Monitoring

- **CloudWatch Logs**: Task logs are sent to CloudWatch
- **SQS Metrics**: Monitor `ApproximateNumberOfMessages`, `ApproximateNumberOfMessagesNotVisible`
- **DLQ**: Monitor `lms-prod-video-transcription-dead_letter_queue` for failed jobs
