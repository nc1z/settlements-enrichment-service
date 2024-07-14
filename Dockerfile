# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper scripts and the .mvn directory
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Copy the project files to the working directory
COPY pom.xml .
RUN ./mvnw dependency:resolve

# Copy the rest of the project files
COPY src ./src

# Package the application using Maven
RUN ./mvnw clean package -DskipTests

# Expose the port that the application will run on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "target/settlements-enrichment-service-0.0.1-SNAPSHOT.jar"]
