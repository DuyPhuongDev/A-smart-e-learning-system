"""
Configuration module for Model Training Worker.
"""

import os


class Config:
    """Static configuration with environment overrides for S3 and compute settings."""

    # AWS Region
    AWS_REGION = "ap-southeast-1"

    # SQS Configuration
    SQS_QUEUE_URL = "https://sqs.ap-southeast-1.amazonaws.com/211125750777/lms-prod-training-job-queue"
    SQS_VISIBILITY_TIMEOUT = 1800  # 30 minutes for longer training jobs
    SQS_WAIT_TIME_SECONDS = 20  # Long polling

    # S3 Configuration
    S3_BUCKET = os.getenv("S3_BUCKET", "lms-wecancode")
    S3_MODELS_PREFIX = os.getenv("S3_MODELS_PREFIX", "linear-grade-predictor-model/models/")
    S3_DATASETS_PREFIX = os.getenv("S3_DATASETS_PREFIX", "linear-grade-predictor-model/training-dataset/")

    # Worker Configuration
    MAX_EMPTY_POLLS = 5  # Exit after 5 consecutive empty polls (cost optimization)
    TEMP_DIR = "/tmp/worker"

    # Model Training Configuration (matching train_model.py)
    TEST_SIZE = 0.2
    RANDOM_STATE = 42
    RIDGE_ALPHA = 1.0

    # Callback Configuration
    CALLBACK_TIMEOUT = 60  # 60 seconds (longer for potential slow backend)
    CALLBACK_RETRIES = 3
    CALLBACK_URL_PLACEHOLDER = os.getenv(
        "CALLBACK_URL",
        "https://api.example.com/v1/training/callback"  # TODO: Replace with actual backend URL
    )

    @classmethod
    def validate(cls):
        """Validate required configuration."""
        required = ["SQS_QUEUE_URL", "S3_BUCKET"]
        missing = [key for key in required if not getattr(cls, key)]
        if missing:
            raise ValueError(f"Missing required configuration: {missing}")
        return True

    @classmethod
    def print_config(cls):
        """Print current configuration for debugging."""
        print("=" * 60)
        print("Model Training Worker Configuration")
        print("=" * 60)
        print(f"AWS_REGION: {cls.AWS_REGION}")
        print(f"SQS_QUEUE_URL: {cls.SQS_QUEUE_URL}")
        print(f"S3_BUCKET: {cls.S3_BUCKET}")
        print(f"S3_MODELS_PREFIX: {cls.S3_MODELS_PREFIX}")
        print(f"S3_DATASETS_PREFIX: {cls.S3_DATASETS_PREFIX}")
        print(f"MAX_EMPTY_POLLS: {cls.MAX_EMPTY_POLLS}")
        print(f"RIDGE_ALPHA: {cls.RIDGE_ALPHA}")
        print(f"TEST_SIZE: {cls.TEST_SIZE}")
        print(f"CALLBACK_URL: {cls.CALLBACK_URL_PLACEHOLDER}")
        print("=" * 60)
