# Stage 1: builder
FROM maven:3.9.4-amazoncorretto-21 AS builder

# 1. Copier le module cible
# Nous sommes dans le contexte 'discovery-service'
WORKDIR /app/discovery-service
COPY . . 

# 2. Compiler le module discovery-service
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Récupérer le nom du JAR à partir de l'étape de compilation
ARG JAR_FILE=/app/discovery-service/target/*.jar
COPY --from=builder ${JAR_FILE} discovery-service.jar

EXPOSE 8761
ENTRYPOINT ["java","-jar","discovery-service.jar"]
