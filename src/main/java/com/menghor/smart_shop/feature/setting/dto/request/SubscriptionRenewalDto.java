package com.menghor.smart_shop.feature.setting.dto.request;

import lombok.Data;

@Data
public class SubscriptionRenewalDto {
    private Long subscriptionId;
    private String transactionId;
    private Double amountPaid;
    private String notes;
}