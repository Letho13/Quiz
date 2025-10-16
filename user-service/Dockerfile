FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app

# 1. Copier le POM parent
COPY pom.xml .

# 2. Copier tous les modules listés dans le POM parent
COPY config-server ./config-server
COPY discovery-service ./discovery-service
COPY gateway-service ./gateway-service
COPY quiz-front ./quiz-front

COPY quiz-service ./quiz-service
COPY quiz-shared-dto ./quiz-shared-dto
COPY reward-service ./reward-service
COPY user-service ./user-service

# 3. Installer les artefacts partagés (DTO) dans le répertoire local Maven du conteneur
RUN mvn clean install -pl user-service -am -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Récupérer le nom du JAR à partir de l'étape de compilation
ARG JAR_FILE=/app/user-service/target/*.jar
COPY --from=builder ${JAR_FILE} user-service.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","user-service.jar"]