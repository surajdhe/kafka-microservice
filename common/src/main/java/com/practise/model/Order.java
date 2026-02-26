package com.practise.model;

import lombok.Data;

@Data
public class Order {
    private String orderId;
    private String product;
    private int quantity;
    private double price;
    private String customerID;
    private String customerEmail;
    private String customerName;
}
