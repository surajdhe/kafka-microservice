```sh
.\mvnw -f .\order-service\pom.xml spring-boot:run - to run specific module
.\mvnw -f .\payment-service\pom.xml spring-boot:run
```

docker exec -it kafka-microservice-kafka-1 sh
kafka-console-consumer --bootstrap-server localhost:9092 --topic orders --from-beginning