FROM maven:3.9.4-amazoncorretto-21 AS builder

COPY target/reward-service-0.0.1-SNAPSHOT.jar reward-service.jar

EXPOSE 8083

ENTRYPOINT ["java","-jar","reward-service.jar"]