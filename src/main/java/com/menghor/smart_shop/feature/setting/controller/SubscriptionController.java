package com.menghor.smart_shop.feature.setting.controller;

import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRenewalDto;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionHistoryResponseDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.feature.setting.service.SubscriptionService;
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
    public ApiResponse<SubscriptionResponseDto> createSubscription(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
        log.info("Received request to create a new subscription");
        final SubscriptionResponseDto subscriptionResponseDto = subscriptionService.createSubscription(subscriptionRequestDto);
        return new ApiResponse<>("Success", "Subscription created successfully", subscriptionResponseDto);
    }
    
    @GetMapping("/{subscriptionId}")
    public ApiResponse<SubscriptionResponseDto> getSubscriptionById(@PathVariable Long subscriptionId) {
        log.info("Received request to get subscription by ID: {}", subscriptionId);
        final SubscriptionResponseDto subscriptionResponseDto = subscriptionService.getSubscriptionById(subscriptionId);
        return new ApiResponse<>("Success", "Subscription retrieved successfully", subscriptionResponseDto);
    }
    
    @GetMapping("/user/{userId}")
    public ApiResponse<List<SubscriptionResponseDto>> getSubscriptionsByUserId(@PathVariable Long userId) {
        log.info("Received request to get subscriptions by user ID: {}", userId);
        final List<SubscriptionResponseDto> subscriptions = subscriptionService.getSubscriptionsByUserId(userId);
        return new ApiResponse<>("Success", "Subscriptions retrieved successfully", subscriptions);
    }
    
    @GetMapping("/user/{userId}/active")
    public ApiResponse<SubscriptionResponseDto> getActiveSubscriptionForUser(@PathVariable Long userId) {
        log.info("Received request to get active subscription for user ID: {}", userId);
        final SubscriptionResponseDto subscriptionResponseDto = subscriptionService.getActiveSubscriptionForUser(userId);
        return new ApiResponse<>("Success", "Active subscription retrieved successfully", subscriptionResponseDto);
    }
    
    @GetMapping("/user/{userId}/history")
    public ApiResponse<List<SubscriptionHistoryResponseDto>> getSubscriptionHistoryByUserId(@PathVariable Long userId) {
        log.info("Received request to get subscription history for user ID: {}", userId);
        final List<SubscriptionHistoryResponseDto> history = subscriptionService.getSubscriptionHistoryByUserId(userId);
        return new ApiResponse<>("Success", "Subscription history retrieved successfully", history);
    }
    
    @PostMapping("/renew")
    public ApiResponse<SubscriptionResponseDto> renewSubscription(@RequestBody SubscriptionRenewalDto renewalDto) {
        log.info("Received request to renew subscription with ID: {}", renewalDto.getSubscriptionId());
        final SubscriptionResponseDto subscriptionResponseDto = subscriptionService.renewSubscription(renewalDto);
        return new ApiResponse<>("Success", "Subscription renewed successfully", subscriptionResponseDto);
    }
    
    @PostMapping("/{subscriptionId}/cancel")
    public ApiResponse<SubscriptionResponseDto> cancelSubscription(
            @PathVariable Long subscriptionId, 
            @RequestParam(required = false) String reason) {
        log.info("Received request to cancel subscription with ID: {}", subscriptionId);
        final SubscriptionResponseDto subscriptionResponseDto = subscriptionService.cancelSubscription(subscriptionId, reason);
        return new ApiResponse<>("Success", "Subscription canceled successfully", subscriptionResponseDto);
    }
    
    @PostMapping("/{subscriptionId}/change-plan")
    public ApiResponse<SubscriptionResponseDto> changeSubscriptionPlan(
            @PathVariable Long subscriptionId,
            @RequestParam Long newPlanId,
            @RequestParam String transactionId,
            @RequestParam Double amountPaid) {
        log.info("Received request to change plan for subscription ID: {} to plan ID: {}", subscriptionId, newPlanId);
        final SubscriptionResponseDto subscriptionResponseDto = 
                subscriptionService.changeSubscriptionPlan(subscriptionId, newPlanId, transactionId, amountPaid);
        return new ApiResponse<>("Success", "Subscription plan changed successfully", subscriptionResponseDto);
    }
    
    @GetMapping("/check/{userId}")
    public ApiResponse<Boolean> hasActiveSubscription(@PathVariable Long userId) {
        log.info("Received request to check if user ID: {} has an active subscription", userId);
        final boolean hasActiveSubscription = subscriptionService.hasActiveSubscription(userId);
        return new ApiResponse<>("Success", "Subscription check successful", hasActiveSubscription);
    }
    
    @PostMapping("/process-expired")
    public ApiResponse<String> processExpiredSubscriptions() {
        log.info("Received request to process expired subscriptions");
        subscriptionService.processExpiredSubscriptions();
        return new ApiResponse<>("Success", "Expired subscriptions processed successfully", "Processed");
    }
}