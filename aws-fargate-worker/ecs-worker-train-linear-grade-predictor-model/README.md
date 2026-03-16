# ECS Worker: Linear Grade Predictor Model Training

Fargate worker that trains Ridge regression models for grade prediction.

## Purpose

Polls SQS queue for training jobs, downloads datasets from S3, trains sklearn Ridge models, uploads trained models back to S3, and sends metrics to backend via callback.

## SQS Message Structure

```json
{
  "job_id": "TRAIN_001",
  "s3_dataset_url": "s3://lms-wecancode/linear-grade-predictor-model/training-dataset/raw_data_v1.csv",
  "model_name": "demand_forecasting_v2",
  "timestamp": "2026-03-01T23:35:00Z"
}
```

## S3 Paths

- **Input datasets:** `s3://lms-wecancode/linear-grade-predictor-model/training-dataset/`
- **Output models:** `s3://lms-wecancode/linear-grade-predictor-model/models/{model_name}_{timestamp}.joblib`

## Environment Variables

| Variable                 | Default                                          | Description                                 |
| ------------------------ | ------------------------------------------------ | ------------------------------------------- |
| `AWS_ACCESS_KEY_ID`      | -                                                | AWS credentials (local testing only)        |
| `AWS_SECRET_ACCESS_KEY`  | -                                                | AWS credentials (local testing only)        |
| `AWS_REGION`             | `ap-southeast-1`                                 | AWS region                                  |
| `S3_BUCKET`              | `lms-wecancode`                                  | S3 bucket for models                        |
| `S3_MODELS_PREFIX`       | `linear-grade-predictor-model/models/`           | S3 prefix for trained models                |
| `S3_DATASETS_PREFIX`     | `linear-grade-predictor-model/training-dataset/` | S3 prefix for datasets                      |
| `CALLBACK_URL`           | `https://api.example.com/v1/training/callback`   | Backend callback endpoint (update required) |
| `MAX_EMPTY_POLLS`        | `5`                                              | Exit after N consecutive empty SQS polls    |
| `RIDGE_ALPHA`            | `1.0`                                            | Ridge regression regularization parameter   |
| `TEST_SIZE`              | `0.2`                                            | Test set size (20%)                         |
| `RANDOM_STATE`           | `42`                                             | Random seed for reproducibility             |
| `SQS_VISIBILITY_TIMEOUT` | `1800`                                           | SQS message visibility timeout (seconds)    |
| `CALLBACK_TIMEOUT`       | `60`                                             | HTTP callback timeout (seconds)             |

## Model Training

**Dataset Scale:** 4-point scale (0.0-4.0)

The input dataset (`course_grade` column) is already in 4-point scale. Model trains directly on this scale.

- **Pipeline:** SimpleImputer(median) → StandardScaler → Ridge(alpha=1.0)
- **Target:** `course_grade` (4-point scale: 0.0-4.0)
- **Split:** GroupShuffleSplit by `student_id` (80/20 train/test)
- **Metrics:** MAE, RMSE, R² computed on 4-point scale

## Callback Payload

```json
{
  "jobId": "TRAIN_001",
  "modelName": "demand_forecasting_v2",
  "modelS3Path": "s3://lms-wecancode/.../model.joblib",
  "status": "SUCCESS",
  "trainingTimeSeconds": 45.2,
  "metrics": {
    "train": {
      "baseline": { "mae": 0.45, "rmse": 0.62, "r2": 0.35 },
      "model": { "mae": 0.32, "rmse": 0.48, "r2": 0.58 },
      "error_distribution": {
        "mu_error": 0.01,
        "sigma_error": 0.31,
        "n": 8000,
        "lower_95": -0.6,
        "upper_95": 0.62
      }
    },
    "test": {
      "baseline": { "mae": 0.46, "rmse": 0.63, "r2": 0.33 },
      "model": { "mae": 0.34, "rmse": 0.5, "r2": 0.56 },
      "error_distribution": {
        "mu_error": 0.02,
        "sigma_error": 0.33,
        "n": 2000,
        "lower_95": -0.63,
        "upper_95": 0.67
      }
    }
  }
}
```

**Note:** All metrics are on 4-point scale (0.0-4.0).

## Setup

### 1. Configure Environment Variables

Copy the example environment file and update with your values:

```bash
cp .env.example .env
```

Edit `.env` and update:

- AWS credentials (for local testing)
- `CALLBACK_URL` with your actual backend endpoint

### 2. Build & Run

```bash
# Build Docker image
docker build -t fargate-model-training-worker .

# Run locally using .env file
docker run --rm --env-file .env fargate-model-training-worker

# Or run with explicit environment variables
docker run --rm \
  -e AWS_ACCESS_KEY_ID=xxx \
  -e AWS_SECRET_ACCESS_KEY=xxx \
  -e CALLBACK_URL=https://your-backend/callback \
  model-training-worker
```

### 3. Deploy to AWS ECS

When deploying to ECS Fargate, set environment variables in the task definition. AWS credentials are automatically provided by IAM task role.

## Cost Optimization

Worker auto-exits after 5 consecutive empty polls from SQS (approximately 100 seconds of idle time).
