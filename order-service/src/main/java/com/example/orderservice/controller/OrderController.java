package com.example.orderservice.controller;

import com.example.orderservice.service.OrderService;
import com.practise.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<String> getAppStatus() {
        return ResponseEntity.ok().body("Service is running");
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody Order order, @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("https://order-service/email");
        String name  = jwt.getClaimAsString("https://order-service/name");

        log.info("Order from user {} with email {} ", userId, email);

        order.setOrderId(UUID.randomUUID().toString());
        orderService.sendOrder(order, userId, email, name);
        return new ResponseEntity<>("Order placed. Your order id " + order.getOrderId(), HttpStatus.OK);
    }
}
