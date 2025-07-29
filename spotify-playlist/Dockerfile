# Use Maven to build the app
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use a lightweight JDK image to run the app
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
