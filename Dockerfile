# Base image with OpenJDK 17
FROM openjdk:17-jdk-slim

# The application's JAR file
ARG JAR_FILE=build/libs/*.jar

# Copy the JAR file to the container
COPY ${JAR_FILE} app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the JAR file
ENTRYPOINT ["java","-jar","app.jar"]
