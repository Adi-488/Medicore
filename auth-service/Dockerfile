# ─────────────────────────────────────────────────────────
# CurePulse HMS — Dockerfile (Auth Microservice)
# Runs the Spring Boot REST API in headless mode
# ─────────────────────────────────────────────────────────

# Stage 1: Build stage
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add a non-root user for security
RUN addgroup -S curepulse && adduser -S curepulse -G curepulse

# Copy the built fat-JAR from the build stage
COPY --from=build /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

# Set ownership
RUN chown curepulse:curepulse app.jar
USER curepulse

ENV PORT=8081
EXPOSE 8081

# Start Spring Boot with production profile
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-Djava.awt.headless=true", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
