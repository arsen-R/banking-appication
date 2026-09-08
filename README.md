# Banking-application
A modular, microservice-based banking application build in which each service is responsible for 
its own area of functionality.

## Table of Content
- [Overview](#overview)
- [Service](#service)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Future Improvements](#future-improvements)

## Overview
This project demonstrate backend engineering patterns including:
- Clean microservice separation with independently deployable services
- JWT based authentication and role-based access control (RBAC)
- Eureka-based service discovery
- Per-service PostgreSQL databases (Database Per Service Pattern)

## Service
| Service       | Port | Responsibility |
|---------------|----|---|
|**config-service**|8888|Centralized configuration (Spring Cloud Config)|
|**api-gateway**|8281|API Gateway for routing requests.|
|**discovery-service**|8761|Service registry/discovery (Netflix Eureka)|
|**auth-service**|8081|User authentication and authorization service.|
|**user-service**|8082|User Management Service.|

## Tech Stack
- Java 21+
- Spring Boot 4.1.0
- Maven
- PostgresSQL
- Spring Cloud (Eureka, Gateway, Feign)
- Lombok, MapStruct, SLF4J
## Getting Started
### Prerequisites
- JDK 21+
- Maven 
- A running PostgreSQL instance
### Clone the repository
```bash
git clone https://github.com/arsen-R/banking-application.git
cd banking-application
```
### Startup Order
1) Run Eureka Server
2) Run Config Server
3) Run Gateway
4) Run other services (auth-service, user-service, etc.)

## Future Improvements
- Containerize application with Docker
- Setting up Jenkins for CI/CD
- Asynchronous event-driven communication via Apache Kafka
- Distributed tracing with Zipkin
- Metrics and monitoring with Prometheus
- Deploy into AWS