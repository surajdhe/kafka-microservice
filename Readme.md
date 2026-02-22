# Kafka Microservices Playground 🚀

A simple multi-module Spring Boot microservices system using **Apache Kafka** for event-driven communication.

Services communicate through the `orders` topic — place an order → process payment → reserve inventory → notify.

---

## 🧱 Architecture

```
order-service  --->  payment-service  --->  inventory-service  --->  notification-service
        \____________________ Kafka (orders topic) ____________________/
```

Each service is independently runnable and communicates asynchronously via Kafka.

---

## ⚙️ Prerequisites

Make sure you have installed:

* Java 17
* Docker
* Maven Wrapper (already included)

---

## 🐳 Start Kafka & Infrastructure

```sh
docker compose up -d
```

This will start:

* Kafka
* Zookeeper (if used)
* Required network

Wait ~10–15 seconds before starting services.

---

## ▶️ Run Individual Services

Run each microservice separately from project root.

```sh
.\mvnw -f order-service\pom.xml spring-boot:run
.\mvnw -f payment-service\pom.xml spring-boot:run
.\mvnw -f inventory-service\pom.xml spring-boot:run
```



