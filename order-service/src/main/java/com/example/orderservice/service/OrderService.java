package com.example.orderservice.service;

import com.practise.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    private static final String topic = "orders";

    public void sendOrder(Order order, String userId, String email, String name) {

        order.setCustomerID(userId);
        order.setCustomerEmail(email);
        order.setCustomerName(name);

        kafkaTemplate.send(topic, order.getOrderId(), order);
        log.info("Order sent: {}", order.getOrderId());
    }

}
