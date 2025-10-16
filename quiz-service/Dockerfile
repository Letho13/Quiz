# Stage 1: builder
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

RUN mvn clean install -pl quiz-shared-dto -DskipTests

# 4. Compiler et packager le module cible (quiz-service)
RUN mvn package -pl quiz-service -DskipTests

# Stage 2: runtime
FROM amazoncorretto:21-jre-alpine
WORKDIR /app

# Récupérer le nom du JAR à partir de l'étape de compilation
ARG JAR_FILE=/app/quiz-service/target/*.jar
COPY --from=builder ${JAR_FILE} quiz-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quiz-service.jar"]