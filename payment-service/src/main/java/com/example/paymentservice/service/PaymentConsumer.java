package com.example.paymentservice.service;

import com.practise.model.Order;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PaymentConsumer {

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @KafkaListener(topics = "orders", groupId = "payment-group")
    public void consume(Order order) {
        log.info("[Payment] Received order " + order.getOrderId());
        executorService.submit(() -> processPayment(order));
    }

    private void processPayment(Order order) {
        try {
            log.info("[Payment] Processing payemnt for order {} | Thread: {}", order.getOrderId(), Thread.currentThread().getName());
            Thread.sleep(2000);
            double total = order.getPrice() * order.getQuantity();
            log.info("[Payment] SUCCESS - Order: {} | Amount: ${}", order.getOrderId(), total);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.info("[Payment] FAILED - Order: {}", order.getOrderId());
        }
    }


    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }


}
