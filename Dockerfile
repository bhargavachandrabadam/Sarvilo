# Runtime image for the Spring Boot jar built into target/
# Uses Temurin JRE 17 (Debian/Jammy) for a small, compatible runtime

FROM eclipse-temurin:17-jre-jammy

LABEL maintainer="stayzy-team@example.com"

# Install curl for HEALTHCHECK and keep image small
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Create a non-root user to run the app
RUN groupadd --system app && useradd --system --gid app --create-home --home-dir /home/app app

WORKDIR /app

# Allow overriding which jar to copy at build-time
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# ensure jar is readable by non-root user
RUN chown app:app /app/app.jar

# Tunable JVM options
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expose the app port (default used if PORT not supplied)
EXPOSE 9093

# Switch to non-root user
USER app

# Healthcheck: Render will also probe the mapped PORT, but container-level HEALTHCHECK is useful for other platforms.
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:${PORT:-9093}/actuator/health || exit 1

# Run the jar. Bind server port to Render's $PORT env var if present
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT:-9093} -jar /app/app.jar"]
