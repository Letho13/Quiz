
FROM maven:3.9.4-amazoncorretto-21 AS builder

# 2. SE DÉPLACER DANS LE DOSSIER DU MICROSERVICE
WORKDIR /app/gateway-service
COPY . .

# 3. Compiler le module gateway-service
RUN mvn clean package -DskipTests

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Récupérer le nom du JAR à partir de l'étape de compilation
ARG JAR_FILE=/app/gateway-service/target/*.jar
COPY --from=builder ${JAR_FILE} gateway-service.jar

EXPOSE 8222
ENTRYPOINT ["java","-jar","gateway-service.jar"]