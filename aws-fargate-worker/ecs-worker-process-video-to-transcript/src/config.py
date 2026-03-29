"""
Configuration module for ECS Worker.

Only S3-related credentials/paths are loaded from environment variables; all
other settings use sane defaults baked into the image. This keeps runtime
configuration minimal while still allowing S3 tenancy to be injected by the
ECS task definition or Docker environment.
"""

import os


class Config:
    """Static configuration with limited env overrides for S3."""

    # AWS Region (static default for SQS)
    AWS_REGION = "ap-southeast-1"

    # SQS Configuration (static defaults)
    SQS_QUEUE_URL = "https://sqs.ap-southeast-1.amazonaws.com/211125750777/lms-prod-video-transcription-queue"
    SQS_VISIBILITY_TIMEOUT = 600  # 10 minutes
    SQS_WAIT_TIME_SECONDS = 20  # Long polling

    # S3 Configuration (env-driven because it is account/tenant specific)
    S3_BUCKET = os.getenv("S3_BUCKET", "lms-wecancode")
    S3_AUDIO_PREFIX = os.getenv("S3_AUDIO_PREFIX", "transcript-videos/")

    # Worker Configuration (static defaults)
    MAX_EMPTY_POLLS = 5  # Exit after 5 empty polls

    # Temp directory for downloads (static default matches Dockerfile)
    TEMP_DIR = "/tmp/worker"

    # AssemblyAI
    ASSEMBLYAI_API_KEY = os.getenv("ASSEMBLYAI_API_KEY", "")

    # Callback Configuration (static defaults)
    CALLBACK_TIMEOUT = 30  # seconds
    CALLBACK_RETRIES = 3

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
        """Print current configuration (for debugging)."""
        print("=" * 60)
        print("ECS Worker Configuration")
        print("=" * 60)
        print(f"AWS_REGION: {cls.AWS_REGION}")
        print(f"SQS_QUEUE_URL: {cls.SQS_QUEUE_URL}")
        print(f"S3_BUCKET: {cls.S3_BUCKET}")
        print(f"S3_AUDIO_PREFIX: {cls.S3_AUDIO_PREFIX}")
        print(f"MAX_EMPTY_POLLS: {cls.MAX_EMPTY_POLLS}")
        print(f"TEMP_DIR: {cls.TEMP_DIR}")
        print(f"CALLBACK_TIMEOUT: {cls.CALLBACK_TIMEOUT}")
        print("=" * 60)
