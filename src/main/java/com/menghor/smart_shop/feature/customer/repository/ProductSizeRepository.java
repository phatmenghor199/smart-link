package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSizeEntity, Long> {
    List<ProductSizeEntity> findByDiscountEndDateBefore(LocalDate now);
    List<ProductSizeEntity> findByProductId(Long productId);
}