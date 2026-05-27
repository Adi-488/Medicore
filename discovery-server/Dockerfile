# ─────────────────────────────────────────────────────────
# CurePulse HMS — Dockerfile (Discovery Registry Server)
# Runs the Netflix Eureka Discovery Registry Server
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
COPY --from=build /app/target/discovery-server-0.0.1-SNAPSHOT.jar app.jar

# Set ownership
RUN chown curepulse:curepulse app.jar
USER curepulse

ENV PORT=8761
EXPOSE 8761

# Start Eureka with production configuration
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-Djava.awt.headless=true", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
