package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<BannerEntity, Long>, JpaSpecificationExecutor<BannerEntity> {
    List<BannerEntity> findByShopId(Long shopId); // Find banners by shopId

    // Add methods with explicit sorting
    List<BannerEntity> findByShopIdOrderByCreatedAtDesc(Long shopId);

    // Method for pagination with sorting
    Page<BannerEntity> findByShopId(Long shopId, Pageable pageable);
}