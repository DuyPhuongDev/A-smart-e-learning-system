FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace/app

# Copy ALL POM files to satisfy parent POM validation
COPY pom.xml .
COPY common/pom.xml common/
COPY eureka-server/pom.xml eureka-server/
COPY config-server/pom.xml config-server/
COPY api-gateway/pom.xml api-gateway/
COPY authentication-service/pom.xml authentication-service/
COPY user-management-service/pom.xml user-management-service/
COPY course-management-service/pom.xml course-management-service/
COPY assessment-management-service/pom.xml assessment-management-service/
COPY assessment-execution-service/pom.xml assessment-execution-service/
COPY course-delivery-service/pom.xml course-delivery-service/
COPY student-personalization-service/pom.xml student-personalization-service/
COPY tracking-service/pom.xml tracking-service/
COPY enrollment-service/pom.xml enrollment-service/
COPY notification-service/pom.xml notification-service/
COPY communication-service/pom.xml communication-service/
COPY analytics-reporting-service/pom.xml analytics-reporting-service/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B -pl :api-gateway -am || true

# Copy source code
COPY common/src common/src
COPY api-gateway/src api-gateway/src

# Build
RUN mvn clean package -DskipTests -pl :api-gateway -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar from build stage
COPY --from=build /workspace/app/api-gateway/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
