package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findByShopId(Long shopId); // Find banners by shopId
    Optional<CategoryEntity> findByIdAndShopId(Long categoryId, Long shopId);

}