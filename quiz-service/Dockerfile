# Stage 1: builder
FROM maven:3.9.4-amazoncorretto-21 AS builder
WORKDIR /app

# Copier les fichiers Maven
# POM racine
COPY pom.xml ./
# POM du module
COPY quiz-service/pom.xml ./quiz-service/
COPY quiz-service/src ./quiz-service/src

# Compiler et packager le module
RUN mvn -pl quiz-service clean package -DskipTests

# Stage 2: runtime
FROM amazoncorretto:21
WORKDIR /app

# Copier le JAR depuis le builder
COPY --from=builder /app/quiz-service/target/quiz-service-0.0.1-SNAPSHOT.jar quiz-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "quiz-service.jar"]