# Multi-stage Dockerfile for QueueEase Spring Boot Backend (Root Context)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/queueease-1.0.0.jar app.jar

EXPOSE 8080
ENV PORT=8080
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar --server.port=${PORT}"]
