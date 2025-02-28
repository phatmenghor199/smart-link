package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<BannerEntity, Long> {
    List<BannerEntity> findByShopId(Long shopId); // Find banners by shopId
}