package com.menghor.smart_shop.scheduled;

import com.menghor.smart_shop.feature.setting.service.SubscriptionNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionNotificationScheduler {

    private final SubscriptionNotificationService notificationService;
    
    // Send notifications 7 days before expiration (runs at 9 AM every day)
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendExpirationNotifications() {
        log.info("Starting scheduled task: Sending subscription expiration notifications");
        notificationService.processExpirationNotifications();
        log.info("Completed scheduled task: Sending subscription expiration notifications");
    }
    
    // Send urgent notifications 1 day before expiration (runs at 10 AM every day)
    @Scheduled(cron = "0 0 10 * * ?")
    public void sendUrgentExpirationNotifications() {
        log.info("Starting scheduled task: Sending urgent subscription expiration notifications");
        // Find subscriptions expiring in 1 day and send urgent notifications
        notificationService.findSubscriptionsExpiringIn(1)
            .forEach(notificationService::sendExpirationNotification);
        log.info("Completed scheduled task: Sending urgent subscription expiration notifications");
    }
}