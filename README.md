## About

This Proof of Concept (PoC) demonstrates a basic Constrained Application Protocol (CoAP) server implementation using Eclipse Californium.

## The Eclipse Californium Framework

> “Eclipse Californium™ (Cf) is an open source implementation of the Constrained Application Protocol (CoAP). It is written in Java and targets unconstrained environments such as back-end service infrastructures (e.g., proxies, resource directories, or cloud services)”.
>
> \- [https://projects.eclipse.org/projects/iot.californium](https://projects.eclipse.org/projects/iot.californium)

[https://eclipse.dev/californium/](https://eclipse.dev/californium/)  
[https://github.com/eclipse-californium/californium](https://github.com/eclipse-californium/californium)

## Protocols - Eclipse Californium Implementations

[https://projects.eclipse.org/projects/iot.californium](https://projects.eclipse.org/projects/iot.californium)

## Protocols - Application Implementations

* Constrained Application Protocol (CoAP): RFC 7252 ([https://datatracker.ietf.org/doc/html/rfc7252](https://datatracker.ietf.org/doc/html/rfc7252))
* Datagram Transport Layer Security (DTLS) 1.2: RFC 6347 ([https://datatracker.ietf.org/doc/html/rfc6347](https://datatracker.ietf.org/doc/html/rfc6347))

## Application Implementation Overview

* Launcher: the launcher starts the two implemented servers
* CoAP: 5683/UDP (unencrypted)
* CoAP + DTLS: 5684/UDP (DTLS 1.2)

Currently the CoAP + DTLS implementation uses only Pre-shared keys (PSK) security mode.

## Build & Runtime Requirements

* OpenJDK 21
* Maven 3.9.9

## Non-Production Installation

### Containerized Setup (Docker)

```bash
git clone https://github.com/Stattus4/coap-server.git
cd coap-server/
docker-compose -f docker/docker-compose.yml up
```

### Manual Build (Maven)

```bash
git clone https://github.com/Stattus4/coap-server.git
cd coap-server/coap-server/
mvn clean package
java -jar target/coap-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Environment Variables

`PSK_IDENTITY` (Default value: `identity`)
`PSK_SECRET` (Default value: `qwerty`)

**Warning:** The default values for `PSK_IDENTITY` and `PSK_SECRET` are provided for testing purposes only. In a production environment, it is crucial to generate and use strong, unique values.

## CoAP Resources

Currently the only CoAP implemented resource is `/hello` working under the GET method.
