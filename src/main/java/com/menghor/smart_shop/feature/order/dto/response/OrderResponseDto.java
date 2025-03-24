package com.menghor.smart_shop.feature.order.dto.response;

import com.menghor.smart_shop.enumations.DeliveryCharge;
import com.menghor.smart_shop.enumations.OrderFilterType;
import com.menghor.smart_shop.enumations.OrderStatus;
import com.menghor.smart_shop.feature.order.model.DeliveryMethodEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private String status;
    private String phoneNumber;
    private String location;
    private Long shopId;
    private OrderFilterType orderBy;
    private Double deliveryFee;
    private DeliveryCharge deliveryCharge;
    private Double totalAmount;
    private DeliveryMethodResponseDto deliveryMethod; // Include delivery method details
    private List<OrderItemResponseDto> orderItems;
    private String createdAt;
    private String updatedAt;
}