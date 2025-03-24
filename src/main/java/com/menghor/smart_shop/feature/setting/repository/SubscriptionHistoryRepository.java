package com.menghor.smart_shop.feature.setting.repository;

import com.menghor.smart_shop.feature.setting.model.SubscriptionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionHistoryRepository extends JpaRepository<SubscriptionHistoryEntity, Long> {
    List<SubscriptionHistoryEntity> findByUserId(Long userId);
    
    List<SubscriptionHistoryEntity> findBySubscriptionId(Long subscriptionId);
}