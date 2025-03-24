package com.menghor.smart_shop.feature.setting.dto.resposne;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriptionResponseDto {
    private Long id;
    private UserDto user;
    private PlanResponseDto plan;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Status status;
    private Boolean autoRenew;
    private String transactionId;
    private Double amountPaid;
    private Long previousSubscriptionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private Long daysRemaining;
}