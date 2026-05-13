FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace/app

# Copy POM files to satisfy parent POM validation
COPY pom.xml .
COPY common/pom.xml common/
COPY eureka-server/pom.xml eureka-server/
COPY config-server/pom.xml config-server/
COPY api-gateway/pom.xml api-gateway/
COPY authentication-service/pom.xml authentication-service/
COPY user-management-service/pom.xml user-management-service/
COPY course-management-service/pom.xml course-management-service/
COPY learning-service/pom.xml learning-service/
COPY assessment-service/pom.xml assessment-service/
COPY personalization-service/pom.xml personalization-service/
COPY notification-service/pom.xml notification-service/
COPY communication-service/pom.xml communication-service/
COPY coaching-chatbot-service/pom.xml coaching-chatbot-service/

# Copy source code
COPY common/src common/src
COPY learning-service/src learning-service/src

# Build
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -pl :learning-service -am

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN mkdir -p /usr/bin/prediction_model_cache && chmod -R 777 /usr/bin/prediction_model_cache

# Download ONNX Runtime native library (glibc-based, matches Debian base image)
ARG ONNX_VERSION=1.16.3
RUN apt-get update && apt-get install -y --no-install-recommends wget ca-certificates \
    && wget -q https://github.com/microsoft/onnxruntime/releases/download/v${ONNX_VERSION}/onnxruntime-linux-x64-${ONNX_VERSION}.tgz \
    && tar -xzf onnxruntime-linux-x64-${ONNX_VERSION}.tgz \
    && cp onnxruntime-linux-x64-${ONNX_VERSION}/lib/libonnxruntime.so.${ONNX_VERSION} /usr/lib/ \
    && ln -s /usr/lib/libonnxruntime.so.${ONNX_VERSION} /usr/lib/libonnxruntime.so \
    && rm -rf onnxruntime-linux-x64-${ONNX_VERSION} onnxruntime-linux-x64-${ONNX_VERSION}.tgz \
    && apt-get remove -y wget && apt-get autoremove -y && rm -rf /var/lib/apt/lists/*

# Copy jar from build stage
COPY --from=build /workspace/app/learning-service/target/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
