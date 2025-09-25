FROM openjdk:21-jdk-slim
LABEL authors="IhorSoprunov"

WORKDIR /app

# Copy your Spring Boot jar file into the container
COPY target/shop-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (Spring Boot default port)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]