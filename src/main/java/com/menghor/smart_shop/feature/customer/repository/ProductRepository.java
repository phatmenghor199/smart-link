package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByShopId(Long shopId);
    List<ProductEntity> findByDiscountEndDateBefore(LocalDate now);

    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(p.discountStartDate IS NOT NULL AND p.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN p.discountStartDate AND p.discountEndDate) " +
            "OR EXISTS (SELECT ps FROM ProductSizeEntity ps WHERE ps.product = p AND " +
            "(ps.discountStartDate IS NOT NULL AND ps.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN ps.discountStartDate AND ps.discountEndDate))")
    List<ProductEntity> findAllWithActivePromotions();


    @Query("SELECT p FROM ProductEntity p WHERE " +
            "p.shop.id = :shopId AND (" +
            "(p.discountStartDate IS NOT NULL AND p.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN p.discountStartDate AND p.discountEndDate) " +
            "OR EXISTS (SELECT ps FROM ProductSizeEntity ps WHERE ps.product = p AND " +
            "(ps.discountStartDate IS NOT NULL AND ps.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN ps.discountStartDate AND ps.discountEndDate)))")
    List<ProductEntity> findAllWithActivePromotionsByShopId(Long shopId);
}