# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Install Docker CLI
RUN apt-get update && apt-get install -y docker.io

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper to leverage caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:resolve

# Copy the project files to the working directory
COPY src ./src

# Package the application using Maven
RUN ./mvnw clean package -DskipTests

# Expose the port that the application will run on
EXPOSE 8080

# Set environment variables for Testcontainers
ENV TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal

# Default command
CMD ["java", "-jar", "target/settlements-enrichment-service-0.0.1-SNAPSHOT.jar"]
