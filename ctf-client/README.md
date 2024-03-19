# Capture the Flag (Client)

## Project

A Spring Boot module which serves as a consumer of the APIs produced by the cfp-service

Requirements

* Java 17
* Maven (Wrapper, see https://maven.apache.org/wrapper/)

### Build

Linux/MacOS

```bash
./mvnw clean install
```

Windows

```bash
mvnw.cmd clean install
```

### Run

In root directory of the project

```bash
java -jar target/ctf-0.0.1-SNAPSHOT.jar
```

### Integrated Webservice

Configuration (e.g., port) is located in [application.properties](src%2Fmain%2Fresources%2Fapplication.properties).

## Documentation
To use the Module either start the Springboot App by using the CtfClientApplication.java or by making an instance of cfpClientController.java and call functions from there.

### Reference Documentation


### Guides
