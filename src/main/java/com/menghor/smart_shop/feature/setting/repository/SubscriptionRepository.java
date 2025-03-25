package com.menghor.smart_shop.feature.setting.repository;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    // Fetch all subscriptions for a user with eager loading of related entities
    @EntityGraph(attributePaths = {"user", "plan"})
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId ORDER BY s.createdAt DESC")
    List<SubscriptionEntity> findByUserId(@Param("userId") Long userId);

    // Use explicit @Query for findActiveSubscriptionForUser
    @EntityGraph(attributePaths = {"user", "plan"})
    @Query("SELECT s FROM SubscriptionEntity s " +
            "WHERE s.user.id = :userId " +
            "AND s.status = com.menghor.smart_shop.enumations.Status.ACTIVE " +
            "AND s.endDate > :now " +
            "ORDER BY s.createdAt DESC")
    Optional<SubscriptionEntity> findActiveSubscriptionForUser(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    @EntityGraph(attributePaths = {"user", "plan"})
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id IN :userIds")
    List<SubscriptionEntity> findByUserIdIn(@Param("userIds") List<Long> userIds);

    @Query("SELECT COUNT(s) > 0 FROM SubscriptionEntity s " +
            "WHERE s.user.id = :userId " +
            "AND s.status = com.menghor.smart_shop.enumations.Status.ACTIVE " +
            "AND s.endDate > :now")
    boolean hasActiveSubscription(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );

    @EntityGraph(attributePaths = {"user", "plan"})
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.endDate < :now AND s.status = 'ACTIVE'")
    List<SubscriptionEntity> findExpiredSubscriptions(@Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"user", "plan"})
    @Query("SELECT s FROM SubscriptionEntity s " +
            "WHERE s.user.id IN :userIds " +
            "AND s.status = com.menghor.smart_shop.enumations.Status.ACTIVE " +
            "AND s.endDate > :now")
    List<SubscriptionEntity> findActiveSubscriptionsForUsers(
            @Param("userIds") List<Long> userIds,
            @Param("now") LocalDateTime now
    );
}