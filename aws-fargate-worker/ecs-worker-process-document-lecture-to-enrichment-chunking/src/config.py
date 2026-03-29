"""
Configuration module for ECS Document/Text Enrichment Worker.

Configuration for SQS, Gotenberg, Gemini API, and callback settings.
"""

import os


class Config:
    """Static configuration with environment variable overrides."""

    # AWS Region
    AWS_REGION = os.getenv("AWS_REGION", "ap-southeast-1")

    # SQS Configuration
    SQS_QUEUE_URL = os.getenv(
        "SQS_QUEUE_URL",
        "https://sqs.ap-southeast-1.amazonaws.com/211125750777/lms-prod-document-lecture-to-enrichment-chunking-queue"
    )
    SQS_VISIBILITY_TIMEOUT = 900  # 15 minutes (documents take longer)
    SQS_WAIT_TIME_SECONDS = 20  # Long polling

    # Worker Configuration
    MAX_EMPTY_POLLS = 5  # Exit after 5 empty polls

    # Temp directory for downloads
    TEMP_DIR = os.getenv("TEMP_DIR", "/tmp/worker")

    # Gotenberg Configuration (for document conversion)
    GOTENBERG_URL = os.getenv("GOTENBERG_URL", "http://localhost:3000")
    GOTENBERG_TIMEOUT = 120  # seconds

    # Gemini API Configuration
    GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "")
    GEMINI_MODEL = os.getenv("GEMINI_MODEL", "gemini-3-flash-preview")

    # LLM Processing Configuration
    # Optimized for Gemini 2.0 Flash: 1000 RPM, 4M TPM
    # Batch size 10 + 100ms delay = ~600 requests/min (safe under 1000 RPM)
    LLM_DELAY_MS = int(os.getenv("LLM_DELAY_MS", "100"))  # 100ms delay between batches
    LLM_MAX_RETRIES = 3
    LLM_BATCH_SIZE = int(os.getenv("LLM_BATCH_SIZE", "10"))  # Process 10 chunks concurrently

    # Document Processing Configuration
    PAGE_WINDOW_SIZE = 5  # 5 pages context window
    TARGET_TOKENS_PER_CHUNK = 500

    # Text Processing Configuration
    TEXT_CHUNK_SIZE = 500  # ~500 tokens per chunk
    TEXT_CHUNK_OVERLAP = 50  # 50 tokens overlap

    # Callback Configuration
    CALLBACK_URL = os.getenv(
        "CALLBACK_URL",
        "https://dev.viettrandai.xyz/api/coaching-chatbot/v1/document-enrichment-callback"
    )
    CALLBACK_TIMEOUT = 60  # seconds
    CALLBACK_RETRIES = 3

    @classmethod
    def validate(cls):
        """Validate required configuration."""
        required = ["SQS_QUEUE_URL", "GEMINI_API_KEY"]
        missing = [key for key in required if not getattr(cls, key)]
        if missing:
            raise ValueError(f"Missing required configuration: {missing}")
        return True

    @classmethod
    def print_config(cls):
        """Print current configuration (for debugging)."""
        print("=" * 60)
        print("ECS Document/Text Enrichment Worker Configuration")
        print("=" * 60)
        print(f"AWS_REGION: {cls.AWS_REGION}")
        print(f"SQS_QUEUE_URL: {cls.SQS_QUEUE_URL}")
        print(f"GOTENBERG_URL: {cls.GOTENBERG_URL}")
        print(f"GEMINI_API_KEY: {'[SET]' if cls.GEMINI_API_KEY else '[NOT SET]'}")
        print(f"GEMINI_MODEL: {cls.GEMINI_MODEL}")
        print(f"CALLBACK_URL: {cls.CALLBACK_URL}")
        print(f"MAX_EMPTY_POLLS: {cls.MAX_EMPTY_POLLS}")
        print(f"TEMP_DIR: {cls.TEMP_DIR}")
        print(f"LLM_DELAY_MS: {cls.LLM_DELAY_MS}")
        print(f"LLM_BATCH_SIZE: {cls.LLM_BATCH_SIZE}")
        print(f"PAGE_WINDOW_SIZE: {cls.PAGE_WINDOW_SIZE}")
        print("=" * 60)
