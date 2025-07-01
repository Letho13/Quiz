FROM maven:3.9.4-amazoncorretto-21 AS builder

COPY target/config-server-0.0.1-SNAPSHOT.jar config-server.jar

EXPOSE 8888

ENTRYPOINT ["java","-jar","config-server.jar"]