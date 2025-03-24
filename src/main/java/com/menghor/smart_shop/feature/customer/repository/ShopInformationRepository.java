package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.feature.customer.models.ShopInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopInformationRepository extends JpaRepository<ShopInformationEntity, Long> {
    Optional<ShopInformationEntity> findByName(String name);
}