package com.menghor.smart_shop.feature.order.dto.response;

import com.menghor.smart_shop.enumations.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private OrderStatus status; // Order status
    private Long shopId; // Shop ID for the order
    private Double totalAmount; // Total amount of the order
    private String phoneNumber; // Customer phone number
    private String location; // Customer location
    private List<OrderItemResponseDto> orderItems; // List of ordered products
}