package com.menghor.smart_shop.feature.setting.dto.request;

import lombok.Data;

@Data
public class SubscriptionRequestDto {
    private Long userId;
    private Long planId;
    private Boolean autoRenew;
    private String transactionId;
    private Double amountPaid;
}