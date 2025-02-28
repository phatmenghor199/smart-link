package com.menghor.smart_shop.feature.order.repository;

import com.menghor.smart_shop.feature.order.model.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}