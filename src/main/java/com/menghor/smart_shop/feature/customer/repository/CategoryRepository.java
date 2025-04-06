package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>, JpaSpecificationExecutor<CategoryEntity> {
    List<CategoryEntity> findByShopId(Long shopId);

    Optional<CategoryEntity> findByIdAndShopId(Long categoryId, Long shopId);

    // Find categories by shop ID with pagination
    Page<CategoryEntity> findByShopId(Long shopId, Pageable pageable);

    // Find categories by name containing (case-insensitive)
    Page<CategoryEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Find categories by status
    Page<CategoryEntity> findByStatus(StatusData status, Pageable pageable);

    // Find categories by shop ID and status
    Page<CategoryEntity> findByShopIdAndStatus(Long shopId, StatusData status, Pageable pageable);

    // Check if category name exists for a shop
    boolean existsByNameAndShopId(String name, Long shopId);
}