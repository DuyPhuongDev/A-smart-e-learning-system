FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace/app

# Copy POM files to satisfy parent POM validation
# Only copy pom.xml files that actually exist in the parent pom.xml modules
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

# Copy source code
COPY common/src common/src
COPY user-management-service/src user-management-service/src

# Build
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -pl :user-management-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/app/user-management-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
