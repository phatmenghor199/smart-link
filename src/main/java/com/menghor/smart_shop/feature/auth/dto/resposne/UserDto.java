package com.menghor.smart_shop.feature.auth.dto.resposne;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private RoleEnum userRole;
    private ShopResponseDto shop;
    private SubscriptionResponseDto activeSubscription; // Add subscription info
    private Boolean hasActiveSubscription = false; // Default to false
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}