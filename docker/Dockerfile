FROM maven:3.9.9-eclipse-temurin-21 AS build

ARG BUILD_CF_CLIENT=false
ARG BUILD_LOAD_TEST=false

WORKDIR /app

COPY ./pom.xml .
COPY ./coap-proxy/pom.xml coap-proxy/
COPY ./load-test/pom.xml load-test/

RUN mvn dependency:resolve

COPY . .

RUN ARGS="" && \
    if [ "$BUILD_CF_CLIENT" = "true" ]; then ARGS="$ARGS -Pcf-client"; fi && \
    if [ "$BUILD_LOAD_TEST" = "true" ]; then ARGS="$ARGS -Pload-test"; fi && \
    mvn clean package -DskipTests $ARGS

RUN mkdir -p load-test/target && mkdir -p target

FROM openjdk:21-jdk-slim

ARG COAP_PROXY_JAR=coap-proxy-jar-with-dependencies.jar
ARG LOAD_TEST_JAR=load-test-jar-with-dependencies.jar

WORKDIR /app

COPY --from=build /app/coap-proxy/src/main/resources/certs certs
COPY --from=build /app/coap-proxy/target/*.jar .
COPY --from=build /app/load-test/target/*.jar .
COPY --from=build /app/target/*.jar .

RUN mv $COAP_PROXY_JAR coap-proxy.jar
RUN test -f $LOAD_TEST_JAR && mv $LOAD_TEST_JAR load-test.jar || true

EXPOSE 5683/udp 5684/udp

ENTRYPOINT ["java", "-jar", "coap-proxy.jar"]
