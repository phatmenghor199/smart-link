package com.menghor.smart_shop.feature.order.dto.response;

import lombok.Data;

@Data
public class DeliveryMethodResponseDto {
    private Long id;
    private String name; // e.g., "SELF", "G&T DELIVERY"
    private Double price; // Delivery price
    private String createdAt;
    private String updatedAt;
}