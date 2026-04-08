FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy Maven wrapper and pom files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY common/ ./common/
COPY authentication-service/pom.xml ./authentication-service/

# Copy source code
COPY authentication-service/src ./authentication-service/src

# Build the application
RUN ./mvnw clean package -DskipTests -f authentication-service/pom.xml

# Run the application
EXPOSE 8081
CMD ["java", "-jar", "./authentication-service/target/authentication-service-1.0.0-SNAPSHOT.jar"]


