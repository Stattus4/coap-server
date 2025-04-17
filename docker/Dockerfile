FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY ./coap-proxy/pom.xml .

RUN mvn dependency:resolve

COPY ./coap-proxy/src ./src

RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim

# RUN apt-get update && \
#     apt-get install -y --no-install-recommends net-tools && \
#     apt-get clean && \
#     rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /app/target/coap-proxy-0.0.1-jar-with-dependencies.jar coap-proxy.jar
COPY --from=builder /app/target/cf-client.jar .
COPY --from=builder /app/src/main/resources/certs certs

EXPOSE 5683/udp 5684/udp

ENTRYPOINT ["java", "-jar", "coap-proxy.jar"]
