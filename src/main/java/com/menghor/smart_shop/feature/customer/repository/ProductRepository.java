package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByShopId(Long shopId);
    List<ProductEntity> findByDiscountEndDateBefore(LocalDate now);
}