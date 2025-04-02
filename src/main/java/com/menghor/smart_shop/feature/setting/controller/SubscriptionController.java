package com.menghor.smart_shop.feature.setting.controller;

import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.setting.dto.request.*;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionHistoryResponseDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.feature.setting.service.SubscriptionService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubscriptionResponseDto> createSubscription(
            @RequestBody SubscriptionRequestDto subscriptionRequestDto
    ) {
        log.info("Received request to create a new subscription");
        final SubscriptionResponseDto subscriptionResponseDto =
                subscriptionService.createSubscription(subscriptionRequestDto);
        return new ApiResponse<>("Success", "Subscription created successfully", subscriptionResponseDto);
    }

    @GetMapping("/{subscriptionId}")
    public ApiResponse<SubscriptionResponseDto> getSubscriptionById(
            @PathVariable Long subscriptionId
    ) {
        log.info("Received request to get subscription by ID: {}", subscriptionId);
        final SubscriptionResponseDto subscriptionResponseDto =
                subscriptionService.getSubscriptionById(subscriptionId);
        return new ApiResponse<>("Success", "Subscription retrieved successfully", subscriptionResponseDto);
    }

    @PutMapping("/update")
    public ApiResponse<SubscriptionResponseDto> updateSubscription(
            @RequestBody SubscriptionUpdateDto updateDto
    ) {
        log.info("Received request to update subscription for user ID: {}", updateDto.getUserId());

        final SubscriptionResponseDto updatedSubscription = subscriptionService.updateSubscription(updateDto);
        return new ApiResponse<>("Success", "Subscription updated successfully", updatedSubscription);
    }

    @GetMapping("/user/{userId}/active")
    public ApiResponse<SubscriptionResponseDto> getActiveSubscriptionForCurrentUser(@PathVariable Long userId) {
        log.info("Received request to get active subscription for current user");
        final SubscriptionResponseDto subscriptionResponseDto =
                subscriptionService.getActiveSubscriptionForUser(userId);
        return new ApiResponse<>("Success", "Active subscription retrieved successfully", subscriptionResponseDto);
    }

    @PostMapping("/user/history")
    public ApiResponse<CustomPaginationResponseDto<SubscriptionHistoryResponseDto>> getSubscriptionHistory(
            @RequestBody(required = false) SubscriptionHistoryFilterDto filterDto
    ) {
        log.info("Received request to get subscription history for current user");

        // Use default filter if none provided
        if (filterDto == null) {
            filterDto = new SubscriptionHistoryFilterDto();
        }

        final CustomPaginationResponseDto<SubscriptionHistoryResponseDto> history =
                subscriptionService.getSubscriptionHistoryByUserId(filterDto);
        return new ApiResponse<>("Success", "Subscription history retrieved successfully", history);
    }

    @PostMapping("/renew")
    public ApiResponse<SubscriptionResponseDto> renewSubscription(
            @RequestBody SubscriptionRenewalDto renewalDto
    ) {
        log.info("Received request to renew subscription");
        final SubscriptionResponseDto subscriptionResponseDto =
                subscriptionService.renewSubscription(renewalDto);
        return new ApiResponse<>("Success", "Subscription renewed successfully", subscriptionResponseDto);
    }

    @PostMapping("/cancel")
    public ApiResponse<SubscriptionResponseDto> cancelSubscription(
            @RequestParam Long userId,
            @RequestParam(required = false) String reason
    ) {
        log.info("Received request to cancel subscription");
        final SubscriptionResponseDto subscriptionResponseDto =
                subscriptionService.cancelSubscription(userId, reason);
        return new ApiResponse<>("Success", "Subscription canceled successfully", subscriptionResponseDto);
    }

    @PostMapping("/change-plan")
    public ApiResponse<SubscriptionResponseDto> changeSubscriptionPlan(
            @RequestBody SubscriptionPlanChangeDto planChangeDto
    ) {

        log.info("Received request to change subscription plan");
        final SubscriptionResponseDto subscriptionResponseDto =
                subscriptionService.changeSubscriptionPlan(planChangeDto);
        return new ApiResponse<>("Success", "Subscription plan changed successfully", subscriptionResponseDto);
    }
}