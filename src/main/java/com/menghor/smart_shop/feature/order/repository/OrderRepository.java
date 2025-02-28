package com.menghor.smart_shop.feature.order.repository;

import com.menghor.smart_shop.feature.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByPhoneNumber(String number);

    List<OrderEntity> findByShopId(Long shopId);
}