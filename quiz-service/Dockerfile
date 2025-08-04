FROM maven:3.9.4-amazoncorretto-21 AS builder

COPY target/quiz-service-0.0.1-SNAPSHOT.jar quiz-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","quiz-service.jar"]