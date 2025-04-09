README.txt
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
* Object Security for Constrained RESTful Environments (OSCORE): RFC 8613 ([https://datatracker.ietf.org/doc/html/rfc8613/](https://datatracker.ietf.org/doc/html/rfc8613/))

## Application Implementation Overview

* Launcher: the launcher starts the two implemented servers
* CoAP: 5683/UDP (unencrypted + optional OSCORE)
* CoAP + DTLS: 5684/UDP (DTLS 1.2)

## Build & Runtime Requirements

* OpenJDK 21
* Maven 3.9.9

## Non-Production Installation

### Containerized Setup (Docker)

```bash
git clone https://github.com/Stattus4/coap-server.git
```
```bash
cd coap-server/
```
```bash
sudo docker compose -f docker/docker-compose.yml up
```

### Manual Build (Maven)

```bash
git clone https://github.com/Stattus4/coap-server.git
```
```bash
cd coap-server/coap-server/
```
```bash
mvn clean package
```
```bash
java -jar target/coap-server-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

## Environment Variables

`PSK_IDENTITY` (Default value: `identity`)

`PSK_SECRET` (Default value: `qwerty`)

**Warning:** The default values for `PSK_IDENTITY` and `PSK_SECRET` are provided for testing purposes only. In a production environment, it is crucial to generate and use strong, unique values.

## CoAP Resources

### CoAP (5683/UDP)

| Resource | Method |
| --- | --- |
| `/hello` | GET |
| `/info` | GET |
| `/readings` | POST |

### CoAP + OSCORE (5683/UDP)

| Resource | Method |
| --- | --- |
| `/secure-hello` | GET |

### CoAP + DTLS 1.2 (5683/UDP)

| Resource | Method |
| --- | --- |
| `/hello` | GET |
| `/info` | GET |
| `/readings` | POST |

## CoAP Clients

### Eclipse Californium - cf-client

#### Enter Into the Docker Container's Shell

```bash
sudo docker exec -it coap-server-app /bin/bash
```

#### Examples

```bash
java -jar cf-client.jar -m GET 'coap://localhost/hello'
```
```bash
java -jar cf-client.jar --non -m GET 'coap://localhost/hello'
```
```bash
java -jar cf-client.jar -i 'identity' -s 'qwerty' -m GET 'coaps://localhost/hello'
```
```bash
java -jar cf-client.jar --non -i 'identity' -s 'qwerty' -m GET 'coaps://localhost/hello'
```
```bash
java -jar cf-client.jar --cert="certs/keyStore.jks#656E6450617373#656E6450617373#server" -m GET 'coaps://localhost/hello'
```
```bash
java -jar cf-client.jar --non --cert="certs/keyStore.jks#656E6450617373#656E6450617373#server" -m GET 'coaps://localhost/hello'
```

### libCoAP - coap-client

#### Linux Installation

```bash
git clone https://github.com/obgm/libcoap.git
```
```bash
cd libcoap/
```
```bash
./autogen.sh
```
```bash
./configure --disable-doxygen --disable-manpages
```
```bash
make
```
```bash
cd examples/
```

\- [https://libcoap.net/install.html](https://libcoap.net/install.html).

#### coap-client OSCORE Configuration

Create the `~/coap-client-oscore.conf` file with the following content:

```bash
master_secret,hex,"0102030405060708090a0b0c0d0e0f10"
master_salt,hex,"9e7ca92223786340"
sender_id,hex,"02"
recipient_id,hex,"01"
id_context,hex,"37cbf3210017a2d3"
aead_alg,integer,10
hkdf_alg,integer,-10
```

#### Examples

```bash
./coap-client -m get 'coap://localhost/hello'
```
```bash
./coap-client -m get 'coap://localhost/secure-hello' -E ~/coap-client-oscore.conf,/tmp/seq_file
```