package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionHistoryFilterDto;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionPlanChangeDto;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRenewalDto;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionHistoryResponseDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionResponseDto createSubscription(SubscriptionRequestDto subscriptionRequestDto);

    SubscriptionResponseDto getSubscriptionById(Long subscriptionId);

    SubscriptionResponseDto getActiveSubscriptionForUser(Long userId);

    CustomPaginationResponseDto<SubscriptionHistoryResponseDto> getSubscriptionHistoryByUserId(
            SubscriptionHistoryFilterDto filterDto
    );

    SubscriptionResponseDto renewSubscription(SubscriptionRenewalDto renewalDto);

    void processExpiredSubscriptions();

    SubscriptionResponseDto cancelSubscription(Long userId, String reason);

    SubscriptionResponseDto changeSubscriptionPlan(SubscriptionPlanChangeDto planChangeDto);
}