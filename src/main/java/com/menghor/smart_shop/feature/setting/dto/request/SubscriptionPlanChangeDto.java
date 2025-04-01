package com.menghor.smart_shop.feature.setting.dto.request;

import lombok.Data;

@Data
public class SubscriptionPlanChangeDto {
    private Long userId;
    private Long newPlanId;
    private String transactionId;
    private Double amountPaid;
    private String notes;
}