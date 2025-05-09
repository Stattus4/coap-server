# CoAP Proxy

## About

~~This Proof of Concept (PoC) demonstrates a basic Constrained Application Protocol (CoAP) server implementation using Eclipse Californium.~~


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

* Launcher: the launcher starts the CoAP and CoAP + DTLS servers
* CoAP: 5683/UDP (unencrypted + optional OSCORE)
* CoAP + DTLS: 5684/UDP (DTLS 1.2)


## Build & Runtime Requirements

* OpenJDK 21
* Maven 3.9.9


## Non-Production Installation

### Containerized Setup (Docker)

#### Downloading, Building and Starting the Server

```bash
git clone https://github.com/Stattus4/coap-server.git
```

```bash
cd coap-server/
```

```bash
sudo docker compose -f docker/docker-compose.yml up
```

#### Running the CLI (Command Line Interface) Applications

The CLI applications executable JARs are available within the container's working directory. It's possible to run them directly, or by running an interactive shell in the container first: 

```bash
sudo docker run -it --rm docker-coap-proxy-app /bin/bash
```

The CLI applications executable JAR paths:

| Application | JAR Path |
| --- | --- |
| cf-client | `/app/cf-client.jar` |
| load-test | `/app/load-test.jar` |

### Manual Build (Maven)

#### Downloading and Building

```bash
git clone https://github.com/Stattus4/coap-server.git
```

```bash
cd coap-server/
```

```bash
mvn clean package -Pcf-client -Pload-test
```

#### Executable JAR Paths

| Application | JAR Path |
| --- | --- |
| coap-proxy | `coap-proxy/target/coap-proxy-jar-with-dependencies.jar` |
| cf-client | `target/cf-client.jar` |
| load-test | `load-test/target/load-test-jar-with-dependencies.jar` |

#### Starting the Server (coap-proxy)

```bash
java -jar coap-proxy/target/coap-proxy-jar-with-dependencies.jar
```


## Environment Variables

`COAPPROXY_SERVER_START`  
*(Default: `true`)*  
Enables or disables the CoAP server on 5683/UDP port.

`COAPPROXY_SECURE_SERVER_START`  
*(Default: `true`)*  
Enables or disables the CoAP + DTLS server on 5684/UDP port.

`COAPPROXY_SECURE_SERVER_PSK_IDENTITY`  
*(Default: `identity`)*  
PSK identity used by the CoAP + DTLS server.

`COAPPROXY_SECURE_SERVER_PSK_SECRET`  
*(Default: `qwerty`)*  
PSK secret used by the CoAP + DTLS server.

`COAPPROXY_DEFAULT_FORWARD_SERVICE`  
*(**Required** — **No default value**)*  
Specifies the default forwarding service. Value must correspond to a forward service defined in the `coap-proxy/src/main/resources/coapproxy/forward-service.json` file.

> ⚠️ **Security Notice:**  
> The variables `COAPPROXY_SECURE_SERVER_PSK_IDENTITY` and `COAPPROXY_SECURE_SERVER_PSK_SECRET` are only used when `COAPPROXY_SECURE_SERVER_START` is set to `true`. Their default values are provided for testing purposes only. **Do not use these defaults in production.**  

## CoAP Resources

### CoAP (5683/UDP)

| Resource | Method |
| --- | --- |
| `/hello` | GET |
| `/info` | GET |
| `/oscore-context` | POST |
| `/readings` | POST |

### CoAP + OSCORE (5683/UDP)

| Resource | Method |
| --- | --- |
| `/oscore-hello` | GET |
| `/oscore-info` | GET |

### CoAP + DTLS 1.2 (5684/UDP)

| Resource | Method |
| --- | --- |
| `/hello` | GET |
| `/info` | GET |
| `/readings` | POST |


## CoAP Clients

### Eclipse Californium - cf-client

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

#### Containerized Setup (Docker)

Download the image:

```bash
sudo docker pull obgm/libcoap:develop
```

Run an interactive shell in the container:

```bash
sudo docker run -it --rm obgm/libcoap:develop /bin/bash
```
 
To allow the `coap-client` to read the OSCORE configuration file, run the interactive shell in the container instead, as shown below:

```bash
sudo docker run -it --rm -v $HOME/coap-client-oscore.conf:/home/user/coap-client-oscore.conf obgm/libcoap:develop /bin/bash
```

Run the `coap-client`:

```bash
$ coap-client 
coap-client v4.3.5 -- a small CoAP implementation
Copyright (C) 2010-2024 Olaf Bergmann <bergmann@tzi.org> and others

Build: v4.3.5-18-g9b0ec2da-dirty
TLS Library: OpenSSL - runtime 3.3.0b-dev, libcoap built for 3.3.0b-dev
(DTLS and TLS support; PSK, PKI, PKCS11, no RPK and no CID support)
(Have OSCORE)
(Have WebSockets)

...
```

#### Linux Manual Installation

Download the source code and build (no system-wide installation required):

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

\- [https://libcoap.net/install.html](https://libcoap.net/install.html).

Run the `coap-client`:

```bash
cd examples/
```

```bash
> ./coap-client
coap-client v4.3.5 -- a small CoAP implementation
Copyright (C) 2010-2025 Olaf Bergmann <bergmann@tzi.org> and others

Build: v4.3.5-54-g440b645a-dirty
TLS Library: TinyDTLS - runtime 0.8.6, libcoap built for 0.8.6
(DTLS and no TLS support; PSK, no PKI, no PKCS11, RPK and no CID support)
(Have OSCORE)
(Have WebSockets)

...
```

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
coap-client -m get 'coap://localhost/hello'
```

```bash
coap-client -m post 'coap://localhost/oscore-context'
```

```bash
coap-client -m get 'coap://localhost/oscore-hello' -E ~/coap-client-oscore.conf,/tmp/seq_file
```


## Load Test

### Usage

```bash
$ java -jar load-test.jar
Usage: java LoadTest --uri <coap://...> --method <GET|POST> [options]

    --ack-timeout <N>              ACK timeout in milliseconds
    --max-retransmit <N>           Maximum number of retransmissions per request
    --num-requests <N>             Total number of requests to send (default: 1)
    --thread-pool-size <N>         Thread pool size to use (default: 1)
    --payload-file <path>          Path to file containing request payload
```

### Sample

```bash
$ java -jar load-test.jar --uri 'coap://localhost/hello' --method GET --num-requests 6000 --thread-pool-size 2000
= Settings =============================

URI:                  coap://localhost/hello
Method:               GET
ACK Timeout:          2[s]
Max Retransmit:       4
Num Requests:         6000
Thread Pool Size:     2000

= Request Results ======================

Success:              5999
Failed:               1

Min Time:             10 ms
Avg Time:             2682,35 ms
Max Time:             41183 ms

= Request Details ======================

Cancel:               1
Reject:               0
Retransmission:       256
Timeout:              1

= Message Retransmission ===============

Retransmitted 1x:     218 (92,37%) requests
Retransmitted 2x:     17 (7,20%) requests
Retransmitted 4x:     1 (0,42%) requests
```
