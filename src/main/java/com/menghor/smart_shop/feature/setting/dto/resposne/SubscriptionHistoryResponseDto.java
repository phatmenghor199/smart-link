package com.menghor.smart_shop.feature.setting.dto.resposne;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.enumations.SubscriptionActionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionHistoryResponseDto {
    private Long id;
    private Long userId;
    private String username;
    private PlanResponseDto plan;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Status status;
    private SubscriptionActionType actionType;
    private Long subscriptionId;
    private String transactionId;
    private Double amountPaid;
    private String notes;
    private LocalDateTime createdAt;
}
