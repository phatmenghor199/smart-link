package com.menghor.smart_shop.feature.setting.repository;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    List<SubscriptionEntity> findByUserId(Long userId);

    Optional<SubscriptionEntity> findByUserIdAndStatus(Long userId, Status status);

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.endDate < ?1 AND s.status = 'ACTIVE'")
    List<SubscriptionEntity> findExpiredSubscriptions(LocalDateTime now);

    // Query to find active subscription for a specific user
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = ?1 AND s.status = com.menghor.smart_shop.enumations.Status.ACTIVE AND s.endDate > ?2")
    Optional<SubscriptionEntity> findActiveSubscriptionForUser(Long userId, LocalDateTime now);

    @Query("SELECT COUNT(s) > 0 FROM SubscriptionEntity s WHERE s.user.id = ?1 AND s.status = com.menghor.smart_shop.enumations.Status.ACTIVE AND s.endDate > ?2")
    boolean hasActiveSubscription(Long userId, LocalDateTime now);

    /**
     * Find active subscriptions for a list of user IDs
     * This helps avoid the N+1 query problem when loading subscriptions for multiple users
     */
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id IN :userIds AND s.status = com.menghor.smart_shop.enumations.Status.ACTIVE AND s.endDate > :now")
    List<SubscriptionEntity> findActiveSubscriptionsForUsers(@Param("userIds") List<Long> userIds, @Param("now") LocalDateTime now);
}