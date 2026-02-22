package com.example.orderservice.controller;

import com.example.orderservice.service.OrderService;
import com.practise.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

   private final OrderService orderService;

    @GetMapping
    public ResponseEntity<String> getAllOrders() {
        return ResponseEntity.ok().body("Service is running");
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        order.setOrderId(UUID.randomUUID().toString());
        orderService.sendOrder(order);
        return new ResponseEntity<>("Order placed. Your order id " + order.getOrderId(), HttpStatus.OK);
    }
}
