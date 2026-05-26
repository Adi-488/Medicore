# ─────────────────────────────────────────────────────────
# CurePulse HMS — Dockerfile (Patient Microservice)
# Runs the Spring Boot REST API in headless mode
# Optimized for pre-compiled local builds
# ─────────────────────────────────────────────────────────

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add a non-root user for security
RUN addgroup -S curepulse && adduser -S curepulse -G curepulse

# Copy the pre-built local fat-JAR
COPY target/patient-service-0.0.1-SNAPSHOT.jar app.jar

# Set ownership
RUN chown curepulse:curepulse app.jar
USER curepulse

# Cloud Run uses PORT env variable
ENV PORT=8082
EXPOSE 8082

# Start Spring Boot with production profile
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-Djava.awt.headless=true", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
