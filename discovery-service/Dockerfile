
FROM maven:3.9.4-amazoncorretto-21 AS builder

COPY target/discovery-service-0.0.1-SNAPSHOT.jar discovery-service.jar

EXPOSE 8761

ENTRYPOINT ["java","-jar","discovery-service.jar"]