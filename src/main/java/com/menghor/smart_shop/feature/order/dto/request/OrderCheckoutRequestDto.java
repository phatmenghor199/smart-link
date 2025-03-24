package com.menghor.smart_shop.feature.order.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderCheckoutRequestDto {
    private String phoneNumber;
    private String location;
    private Long deliveryMethodId; // ID of the chosen delivery method
    private Boolean isFreeDelivery = false; // Flag indicating if the delivery should be free
}
