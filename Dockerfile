# Use a minimal JRE image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the jar file. The jar is expected to be built by the CI pipeline before this stage.
COPY target/*.jar app.jar

# Expose the SSE transport port for MCP HTTP streaming
EXPOSE 8080

# Configure the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]