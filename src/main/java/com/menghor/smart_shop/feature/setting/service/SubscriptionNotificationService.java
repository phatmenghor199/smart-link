package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import com.menghor.smart_shop.feature.setting.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionNotificationService {

    private final SubscriptionRepository subscriptionRepository;
    
    /**
     * Find subscriptions that will expire soon and should trigger a notification
     * @param daysBeforeExpiration Number of days before expiration to send notification
     * @return List of subscriptions about to expire
     */
    public List<SubscriptionEntity> findSubscriptionsExpiringIn(int daysBeforeExpiration) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(daysBeforeExpiration);
        
        // Find active subscriptions expiring around the target date
        LocalDateTime startOfDay = targetDate.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = targetDate.toLocalDate().atTime(23, 59, 59);
        
        return subscriptionRepository.findAll().stream()
                .filter(subscription -> {
                    LocalDateTime expiryDate = subscription.getEndDate();
                    return subscription.getStatus().name().equals("ACTIVE") &&
                           expiryDate.isAfter(startOfDay) && 
                           expiryDate.isBefore(endOfDay);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Send an expiration notification to the user
     * This is a placeholder for real notification logic (email, SMS, in-app, etc.)
     */
    public void sendExpirationNotification(SubscriptionEntity subscription) {
        UserEntity user = subscription.getUser();
        long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), subscription.getEndDate());
        
        String notificationMessage = String.format(
                "Your subscription for %s will expire in %d days. " +
                "Please renew your subscription to continue using all features.",
                subscription.getPlan().getName(),
                daysRemaining
        );
        
        // In a real implementation, this would send an email, SMS, or other notification
        log.info("Sending notification to user {}: {}", user.getUsername(), notificationMessage);
        
        // Placeholder for actual notification service integration
        // notificationService.sendEmail(user.getEmail(), "Subscription Expiring Soon", notificationMessage);
    }
    
    /**
     * Process all notifications for subscriptions expiring soon
     * Call this method from a scheduled task
     */
    public void processExpirationNotifications() {
        // Notify 7 days before expiration
        List<SubscriptionEntity> subscriptionsExpiringSoon = findSubscriptionsExpiringIn(7);
        
        for (SubscriptionEntity subscription : subscriptionsExpiringSoon) {
            try {
                sendExpirationNotification(subscription);
            } catch (Exception e) {
                log.error("Failed to send notification for subscription ID: {}", subscription.getId(), e);
            }
        }
        
        log.info("Processed {} expiration notifications", subscriptionsExpiringSoon.size());
    }
}