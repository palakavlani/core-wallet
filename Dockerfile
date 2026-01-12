# Stage 1: Build the Application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the jar file, skipping tests to save time
RUN mvn clean package -DskipTests

# Stage 2: Run the Application
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copy the built jar from Stage 1
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]