# ============================================
# ECS Worker for Document/Text Lecture Enrichment
# Dual container setup: Gotenberg + Python Worker
# ============================================

FROM python:3.11-slim

# Set labels
LABEL maintainer="LMS Team"
LABEL description="ECS Worker for processing document/text lectures to enriched chunks using Gotenberg + Gemini LLM"
LABEL version="1.0.0"

# Set environment variables
ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1
ENV PIP_NO_CACHE_DIR=1
ENV PIP_DISABLE_PIP_VERSION_CHECK=1

# Set working directory
WORKDIR /app

# Install system dependencies
# - curl: for healthcheck and API calls
# - poppler-utils: for PDF utilities (optional fallback)
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    poppler-utils \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get clean

# Copy requirements first (for better caching)
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Create temp directory for downloads
RUN mkdir -p /tmp/worker && chmod 777 /tmp/worker

# Copy source code
COPY src/ /app/src/

# Create non-root user for security
RUN useradd --create-home --shell /bin/bash worker \
    && chown -R worker:worker /app /tmp/worker

# Switch to non-root user
USER worker

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Run the worker
CMD ["python", "-m", "src.main"]
