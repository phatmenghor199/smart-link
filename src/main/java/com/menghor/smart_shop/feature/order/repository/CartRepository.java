package com.menghor.smart_shop.feature.order.repository;

import com.menghor.smart_shop.feature.order.model.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByShopId(Long shopId);
}