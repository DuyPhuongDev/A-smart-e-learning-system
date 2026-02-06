# ============================================
# ECS Worker for Video to Transcript
# Base image: Python 3.11 slim
# ============================================

FROM python:3.11-slim

# Set labels
LABEL maintainer="LMS Team"
LABEL description="ECS Worker for processing video to transcript using AWS Transcribe"
LABEL version="1.0.0"

# Set environment variables
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1
ENV PIP_NO_CACHE_DIR=1
ENV PIP_DISABLE_PIP_VERSION_CHECK=1

# Set working directory
WORKDIR /app

# Install system dependencies
# - ffmpeg: for audio extraction from video
# - curl: for healthcheck (optional)
RUN apt-get update && apt-get install -y --no-install-recommends \
    ffmpeg \
    curl \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Copy requirements first (for better caching)
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Install yt-dlp (latest versnion for YouTube support)
RUN pip install --no-cache-dir --upgrade yt-dlp

# Create temp directory for downloads
RUN mkdir -p /tmp/worker && chmod 777 /tmp/worker

# Copy source code
COPY src/ /app/src/

# Create non-root user for security
RUN useradd --create-home --shell /bin/bash worker \
    && chown -R worker:worker /app /tmp/worker

# Switch to non-root user
USER worker

# Set only S3-related environment variables for runtime override; other settings
# use baked-in defaults inside src/config.py
ENV S3_BUCKET=lms-wecancode \
    S3_AUDIO_PREFIX=transcript-videos/

# Health check (optional - checks if Python is working)
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD python -c "import boto3; print('OK')" || exit 1

# Run the worker
CMD ["python", "-m", "src.main"]
