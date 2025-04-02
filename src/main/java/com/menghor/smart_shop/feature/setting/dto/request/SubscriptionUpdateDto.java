package com.menghor.smart_shop.feature.setting.dto.request;

import lombok.Data;

@Data
public class SubscriptionUpdateDto {
    private Long userId;
    private String transactionId;
    private Boolean autoRenew;
    private Double amountPaid;
}