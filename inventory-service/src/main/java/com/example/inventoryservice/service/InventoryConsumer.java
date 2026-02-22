package com.example.inventoryservice.service;

import com.practise.model.Order;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
@Slf4j
public class InventoryConsumer {

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        this.stock.put("sugar", 50);
        this.stock.put("chocolate", 50);
        this.stock.put("protein", 100);
        this.stock.put("yogurt", 100);
        this.stock.put("bananḁ", 100);
    }

    @KafkaListener(topics = "orders", groupId = "inventory-group")
    public void consume(Order order, Acknowledgment ack) {

        CompletableFuture
                .supplyAsync(() -> checkStock(order), executorService)
                .thenApply(inStock -> reserveStock(order, inStock))
                .thenAccept(result -> {
                    log.info("[Inventory] Result {}", result);
                    ack.acknowledge();
                })
                .exceptionally(ex -> {
                    log.error("[Inventory] Error", ex);
                    return null;
                });
    }

    private boolean checkStock(Order order) {
        if (null != stock.get(order.getProduct())) {
            return order.getQuantity() <= stock.get(order.getProduct());
        }
        return false;
    }

    private String reserveStock(Order order, boolean inStock) {
        if (inStock) {
            log.info("[Inventory] Reserved {} unit of {}", order.getQuantity(), order.getProduct());
            stock.put(order.getProduct(), stock.get(order.getProduct()) - order.getQuantity());
            return "RESERVED";
        }
        return "OUT_OF_STOCK! You will be refunded!";
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
        try {
            if (executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            executorService.shutdownNow();
        }
    }

}
