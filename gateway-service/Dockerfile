
FROM maven:3.9.4-amazoncorretto-21 AS builder

COPY target/gateway-service-0.0.1-SNAPSHOT.jar gateway-service.jar

EXPOSE 8222

ENTRYPOINT ["java","-jar","gateway-service.jar"]