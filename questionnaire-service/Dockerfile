FROM maven:3.9.4-amazoncorretto-21 AS builder

COPY target/questionnaire-service-0.0.1-SNAPSHOT.jar questionnaire-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","questionnaire-service.jar"]