FROM maven:3.9.4-amazoncorretto-21 AS builder


# 2. SE DÉPLACER DANS LE DOSSIER DU MICROSERVICE
WORKDIR /app/config-server
COPY . .

# 2. Compiler le module config-server
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

# Récupérer le nom du JAR à partir de l'étape de compilation
ARG JAR_FILE=/app/config-server/target/*.jar

COPY --from=builder ${JAR_FILE} config-server.jar

EXPOSE 8888
ENTRYPOINT ["java","-jar","config-server.jar"]