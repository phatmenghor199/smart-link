package com.menghor.smart_shop.feature.order.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private Long shopId; // The shop where the order is placed
    private List<OrderItemRequestDto> orderItems; // List of order items (products and quantity)
    private String phoneNumber; // Customer phone number
    private String location; // Customer location
}