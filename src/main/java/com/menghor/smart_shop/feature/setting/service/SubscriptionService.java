package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRenewalDto;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionHistoryResponseDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;

import java.util.List;

public interface SubscriptionService {
    SubscriptionResponseDto createSubscription(SubscriptionRequestDto subscriptionRequestDto);

    SubscriptionResponseDto getSubscriptionById(Long subscriptionId);

    SubscriptionResponseDto getActiveSubscriptionForUser(Long userId);

    List<SubscriptionResponseDto> getSubscriptionsByUserId(Long userId);

    List<SubscriptionHistoryResponseDto> getSubscriptionHistoryByUserId(Long userId);

    SubscriptionResponseDto renewSubscription(SubscriptionRenewalDto renewalDto);

    void processExpiredSubscriptions();

    SubscriptionResponseDto cancelSubscription(Long subscriptionId, String reason);

    SubscriptionResponseDto changeSubscriptionPlan(Long subscriptionId, Long newPlanId, String transactionId, Double amountPaid);
}