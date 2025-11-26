# Multi-stage Dockerfile: build the Spring Boot jar in a Maven builder stage
# and produce a small runtime image. This avoids depending on a local target/ directory
# when running `docker build`.

# Builder stage: compile the Spring Boot jar
FROM maven:3.9-jdk-17-slim AS builder
WORKDIR /workspace

# Copy just the files needed to fetch dependencies (speeds up rebuilds)
COPY pom.xml mvnw ./
# Copy maven wrapper directory if present (keeps layer cacheable)
COPY .mvn .mvn

# Download dependencies
RUN mvn -B -Dmaven.repo.local=/root/.m2 -DskipTests dependency:go-offline || true

# Copy the rest of the project and build the jar
COPY . .
RUN mvn -B -Dmaven.repo.local=/root/.m2 -DskipTests package

# Runtime image for the Spring Boot jar built into target/
FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="stayzy-team@example.com"

# Install curl for HEALTHCHECK and keep image small
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user to run the app
RUN groupadd --system app && useradd --system --gid app --create-home --home-dir /home/app app

WORKDIR /app

# Copy the jar produced by the builder stage; allow override at build time if desired
ARG JAR_FILE=target/*.jar
COPY --from=builder /workspace/${JAR_FILE} app.jar

# ensure jar is readable by non-root user
RUN chown app:app /app/app.jar

# Tunable JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expose the app port (default used if PORT not supplied)
EXPOSE 9093

# Switch to non-root user
USER app

# Healthcheck: container-level probe
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:${PORT:-9093}/actuator/health || exit 1

# Run the jar. Bind server port to $PORT env var if present
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT:-9093} -jar /app/app.jar"]
