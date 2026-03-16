FROM python:3.11-slim

# Set working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements and install Python dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application source
COPY src/ ./src/

# Create non-root user
RUN useradd -m -u 1000 worker && \
    chown -R worker:worker /app && \
    mkdir -p /tmp/worker && \
    chown -R worker:worker /tmp/worker

USER worker

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD python -c "import boto3; import sklearn; import pandas" || exit 1

# Run the worker
ENTRYPOINT ["python", "-m", "src.main"]
