FROM maven:3.9.4-amazoncorretto-21 AS builder

COPY target/ranking-service-0.0.1-SNAPSHOT.jar ranking-service.jar

EXPOSE 8082

ENTRYPOINT ["java","-jar","ranking-service.jar"]