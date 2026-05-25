# ─────────────────────────────────────────────────────────
# MediCore HMS — Dockerfile (Backend API Server)
# Runs the Spring Boot REST API in headless (no JavaFX) mode
# Optimized for pre-compiled local builds
# ─────────────────────────────────────────────────────────

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add a non-root user for security
RUN addgroup -S medicore && adduser -S medicore -G medicore

# Copy the pre-built local fat-JAR
COPY target/Hospital_Management-0.0.1-SNAPSHOT.jar app.jar

# Set ownership
RUN chown medicore:medicore app.jar
USER medicore

# Cloud Run uses PORT env variable
ENV PORT=8080
EXPOSE 8080

# Start Spring Boot with production profile
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-Djava.awt.headless=true", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
