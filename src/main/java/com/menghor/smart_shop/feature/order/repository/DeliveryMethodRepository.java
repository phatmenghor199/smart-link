package com.menghor.smart_shop.feature.order.repository;

import com.menghor.smart_shop.feature.order.model.DeliveryMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethodEntity, Long> {
    List<DeliveryMethodEntity> findByShopId(Long shopId);
}