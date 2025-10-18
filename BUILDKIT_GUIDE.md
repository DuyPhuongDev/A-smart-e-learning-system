# Docker BuildKit Cache Mount Guide

## Overview

All Dockerfiles in this project now use **BuildKit cache mount** for Maven dependencies. This significantly speeds up builds when dependencies change.

## Benefits

### Traditional Docker Layer Cache
- ❌ When POM changes → Download ALL dependencies (~150-200 MB per service)
- ✅ When code changes → Fast rebuild

### With BuildKit Cache Mount
- ✅ When POM changes → Download ONLY changed dependencies (~3-20 MB)
- ✅ When code changes → Fast rebuild
- ✅ Maven repository persists across builds

## How to Use

### Option 1: Using the Build Scripts (Recommended)

**Windows:**
```bash
# Build all services
docker-build-with-buildkit.bat

# Build specific service
docker-build-with-buildkit.bat user-management-service
```

**Linux/Mac:**
```bash
# Make script executable (first time only)
chmod +x docker-build-with-buildkit.sh

# Build all services
./docker-build-with-buildkit.sh

# Build specific service
./docker-build-with-buildkit.sh user-management-service
```

### Option 2: Manual Build

**Enable BuildKit for single command:**
```bash
# Windows
set DOCKER_BUILDKIT=1
docker-compose build

# Linux/Mac
DOCKER_BUILDKIT=1 docker-compose build
```

**Enable BuildKit permanently:**

Edit `~/.docker/config.json` (Linux/Mac) or `%USERPROFILE%\.docker\config.json` (Windows):
```json
{
  "features": {
    "buildkit": true
  }
}
```

Or edit Docker daemon config `/etc/docker/daemon.json`:
```json
{
  "features": {
    "buildkit": true
  }
}
```

Then restart Docker daemon.

### Option 3: Using docker buildx

```bash
# Build specific service
docker buildx build -f user-management-service/Dockerfile -t user-management-service .

# Build with docker-compose
DOCKER_BUILDKIT=1 docker-compose build
```

## Build & Run Workflow

```bash
# 1. Build with BuildKit (first time)
docker-build-with-buildkit.bat

# 2. Start services
docker-compose up -d

# 3. Update dependencies in pom.xml
# Edit user-management-service/pom.xml

# 4. Rebuild (much faster!)
docker-build-with-buildkit.bat user-management-service

# 5. Restart service
docker-compose up -d user-management-service
```

## Performance Comparison

### Scenario: Update Flyway from 9.x to 10.21.0

**Without BuildKit Cache Mount:**
```
1. POM changed → Invalidate dependency layer
2. Download ALL dependencies: ~150 MB
3. Compile & package
Total: ~3-4 minutes
```

**With BuildKit Cache Mount:**
```
1. POM changed → Maven repo still available
2. Download ONLY Flyway 10.21.0: ~3 MB
3. Compile & package
Total: ~30-40 seconds
```

**Speed improvement: 5-7x faster! ⚡**

## Cache Management

### View Cache Usage

```bash
# Check cache size
docker buildx du

# Or on Windows/Linux
docker system df -v
```

### Clear BuildKit Cache

```bash
# Clear all BuildKit cache
docker builder prune

# Clear cache mounts specifically
docker builder prune --filter type=exec.cachemount

# Clear everything (use with caution)
docker builder prune --all
```

### Clear Specific Service Cache

The Maven cache is shared across all services, but you can force rebuild:

```bash
# Force rebuild without cache
docker-compose build --no-cache user-management-service
```

## Troubleshooting

### Problem: "ERROR: failed to solve: failed to compute cache key"

**Solution:** Ensure BuildKit is enabled:
```bash
# Check if BuildKit is enabled
docker buildx version

# If not available, update Docker to version 18.09+
```

### Problem: "WARN: --mount=type=cache is not supported"

**Solution:** Your Docker version is too old. Update to Docker 18.09 or later.

```bash
# Check Docker version
docker --version

# Update Docker Desktop (Windows/Mac)
# Or update Docker Engine (Linux)
```

### Problem: Build fails with "Permission denied" on /root/.m2

**Solution:** This is rare, but try:
```bash
# Clear all caches and rebuild
docker builder prune --all
docker-compose build --no-cache
```

### Problem: Maven downloads dependencies every time despite cache mount

**Solution 1:** Check if BuildKit is actually being used:
```bash
# Enable BuildKit explicitly
set DOCKER_BUILDKIT=1  # Windows
export DOCKER_BUILDKIT=1  # Linux/Mac

docker-compose build
```

**Solution 2:** Verify Dockerfile syntax:
```dockerfile
# Correct syntax
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# NOT like this (missing mount)
RUN mvn dependency:go-offline -B
```

## Technical Details

### How Cache Mount Works

```dockerfile
# Traditional approach (no cache mount)
RUN mvn dependency:go-offline
# ↓
# Downloads to /root/.m2 inside the layer
# When layer invalidates → lost forever

# With cache mount
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline
# ↓
# Downloads to /root/.m2 mounted from persistent cache
# When layer invalidates → cache remains!
```

### Cache Location

- **Linux:** `/var/lib/docker/buildkit/`
- **Windows (WSL2):** `\\wsl$\docker-desktop-data\version-pack-data\community\docker\buildkit\`
- **Mac:** `~/Library/Containers/com.docker.docker/Data/vms/0/`

### Cache Sharing

- Cache is per Docker host
- Multiple services share the same Maven cache
- Cache persists across:
  - Different builds
  - Different services
  - Docker container deletions
  - Image deletions

## Best Practices

1. **Always use the build scripts** for consistency
2. **Don't commit .m2 to Git** - it's for cache only
3. **Clean cache periodically** to avoid stale dependencies
4. **In CI/CD**, consider using traditional layer cache for reproducibility
5. **For production builds**, consider not using cache mount for deterministic builds

## Comparison: Dev vs Production

| Environment | Recommendation |
|-------------|----------------|
| **Local Development** | ✅ Use BuildKit cache mount |
| **CI/CD Pipeline** | ⚠️ Depends on setup (may not persist) |
| **Production Build** | ⚠️ Consider traditional for reproducibility |
| **Team Development** | ✅ Great for individual workstations |

## Additional Resources

- [Docker BuildKit Documentation](https://docs.docker.com/build/buildkit/)
- [BuildKit Cache Mounts](https://docs.docker.com/build/guide/mounts/)
- [Maven in Docker Best Practices](https://www.docker.com/blog/faster-multi-platform-builds-dockerfile-cross-compilation-guide/)

## Summary

BuildKit cache mount is **highly recommended for local development** where you frequently:
- Add/update dependencies
- Change POM versions
- Experiment with new libraries
- Build multiple services

It provides **5-7x faster builds** when dependencies change, saving significant development time! 🚀

