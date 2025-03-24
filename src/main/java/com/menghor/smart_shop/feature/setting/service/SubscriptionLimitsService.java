package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.exceptoins.error.BadRequestException;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.setting.model.PlanEntity;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import com.menghor.smart_shop.feature.setting.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionLimitsService {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * Check if the user can add more products based on their subscription plan
     */
    public void checkProductLimit(Long userId) {
        log.info("Checking product limit for user ID: {}", userId);

        SubscriptionEntity subscription = getActiveSubscription(userId);
        UserEntity user = subscription.getUser();

        int currentProductCount = user.getShop() != null && user.getShop().getProducts() != null
                ? user.getShop().getProducts().size()
                : 0;

        int maxProducts = subscription.getPlan().getMaxProducts();

        if (currentProductCount >= maxProducts) {
            log.warn("User has reached the product limit: {}/{}", currentProductCount, maxProducts);
            throw new BadRequestException(
                    "You have reached the maximum product limit for your subscription plan. " +
                            "Please upgrade your plan to add more products.");
        }

        log.info("Product limit check passed: {}/{}", currentProductCount, maxProducts);
    }

    /**
     * Check if the user can create banners based on their subscription plan
     */
    public void checkBannerAllowed(Long userId) {
        log.info("Checking if banner creation is allowed for user ID: {}", userId);

        SubscriptionEntity subscription = getActiveSubscription(userId);
        PlanEntity plan = subscription.getPlan();

        if (!plan.getAllowBanners()) {
            log.warn("User's plan does not allow banner creation");
            throw new BadRequestException(
                    "Your current subscription plan does not support banner creation. " +
                            "Please upgrade to a plan that includes banner support.");
        }

        log.info("Banner creation check passed");
    }

    /**
     * Check if the user can manage promotions based on their subscription plan
     */
    public void checkPromotionsAllowed(Long userId) {
        log.info("Checking if promotion management is allowed for user ID: {}", userId);

        SubscriptionEntity subscription = getActiveSubscription(userId);
        PlanEntity plan = subscription.getPlan();

        if (!plan.getAllowPromotions()) {
            log.warn("User's plan does not allow promotion management");
            throw new BadRequestException(
                    "Your current subscription plan does not support promotion management. " +
                            "Please upgrade to a plan that includes promotion features.");
        }

        log.info("Promotion management check passed");
    }

    /**
     * Check if the user can manage delivery methods based on their subscription plan
     */
    public void checkDeliveryAllowed(Long userId) {
        log.info("Checking if delivery management is allowed for user ID: {}", userId);

        SubscriptionEntity subscription = getActiveSubscription(userId);
        PlanEntity plan = subscription.getPlan();

        if (!plan.getAllowDelivery()) {
            log.warn("User's plan does not allow delivery management");
            throw new BadRequestException(
                    "Your current subscription plan does not support delivery management. " +
                            "Please upgrade to a plan that includes delivery features.");
        }

        log.info("Delivery management check passed");
    }

    /**
     * Get the active subscription for a user, throwing an exception if none exists
     */
    private SubscriptionEntity getActiveSubscription(Long userId) {
        Optional<SubscriptionEntity> subscriptionOpt =
                subscriptionRepository.findActiveSubscriptionForUser(userId, LocalDateTime.now());

        if (subscriptionOpt.isEmpty()) {
            log.warn("User does not have an active subscription");
            throw new BadRequestException("You do not have an active subscription. Please subscribe to access this feature.");
        }

        return subscriptionOpt.get();
    }
}
