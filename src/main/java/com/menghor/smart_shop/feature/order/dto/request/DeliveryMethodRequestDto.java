package com.menghor.smart_shop.feature.order.dto.request;

import lombok.Data;

@Data
public class DeliveryMethodRequestDto {
    private String name; // e.g., "SELF", "G&T DELIVERY"
    private Double price; // Delivery price
}