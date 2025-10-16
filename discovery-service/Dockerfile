
FROM maven:3.9.4-amazoncorretto-21 AS builder


WORKDIR /app

COPY pom.xml .
COPY config-server ./config-server
COPY discovery-service ./discovery-service
COPY gateway-service ./gateway-service
COPY quiz-front ./quiz-front
COPY quiz-service ./quiz-service
COPY quiz-shared-dto ./quiz-shared-dto
COPY reward-service ./reward-service
COPY user-service ./user-service

# 2. SE DÉPLACER DANS LE DOSSIER DU MICROSERVICE
WORKDIR /app/discovery-service

# 3. Compiler le module discovery-service
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Récupérer le nom du JAR à partir de l'étape de compilation
ARG JAR_FILE=/app/discovery-service/target/*.jar
COPY --from=builder ${JAR_FILE} discovery-service.jar

EXPOSE 8761
ENTRYPOINT ["java","-jar","discovery-service.jar"]