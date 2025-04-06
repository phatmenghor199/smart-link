package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    // Basic queries
    List<ProductEntity> findByShopId(Long shopId);
    List<ProductEntity> findByShopIdAndStatus(Long shopId, StatusData status);
    List<ProductEntity> findByCategoryId(Long categoryId);
    List<ProductEntity> findByCategoryIdAndStatus(Long categoryId, StatusData status);

    // Pagination queries
    Page<ProductEntity> findByShopId(Long shopId, Pageable pageable);
    Page<ProductEntity> findByShopIdAndStatus(Long shopId, StatusData status, Pageable pageable);
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductEntity> findByCategoryIdAndStatus(Long categoryId, StatusData status, Pageable pageable);

    // Search by name
    Page<ProductEntity> findByShopIdAndNameContainingIgnoreCase(Long shopId, String name, Pageable pageable);
    Page<ProductEntity> findByShopIdAndNameContainingIgnoreCaseAndStatus(Long shopId, String name, StatusData status, Pageable pageable);
    Page<ProductEntity> findByCategoryIdAndNameContainingIgnoreCase(Long categoryId, String name, Pageable pageable);
    Page<ProductEntity> findByCategoryIdAndNameContainingIgnoreCaseAndStatus(Long categoryId, String name, StatusData status, Pageable pageable);

    // Expired discounts
    List<ProductEntity> findByDiscountEndDateBefore(LocalDate now);

    // Active promotions
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(p.discountStartDate IS NOT NULL AND p.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN p.discountStartDate AND p.discountEndDate) " +
            "OR EXISTS (SELECT ps FROM ProductSizeEntity ps WHERE ps.product = p AND " +
            "(ps.discountStartDate IS NOT NULL AND ps.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN ps.discountStartDate AND ps.discountEndDate))")
    List<ProductEntity> findAllWithActivePromotions();

    // Active promotions with pagination
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "(p.discountStartDate IS NOT NULL AND p.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN p.discountStartDate AND p.discountEndDate) " +
            "OR EXISTS (SELECT ps FROM ProductSizeEntity ps WHERE ps.product = p AND " +
            "(ps.discountStartDate IS NOT NULL AND ps.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN ps.discountStartDate AND ps.discountEndDate))")
    Page<ProductEntity> findAllWithActivePromotions(Pageable pageable);

    // Active promotions by shop
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "p.shop.id = :shopId AND (" +
            "(p.discountStartDate IS NOT NULL AND p.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN p.discountStartDate AND p.discountEndDate) " +
            "OR EXISTS (SELECT ps FROM ProductSizeEntity ps WHERE ps.product = p AND " +
            "(ps.discountStartDate IS NOT NULL AND ps.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN ps.discountStartDate AND ps.discountEndDate)))")
    List<ProductEntity> findAllWithActivePromotionsByShopId(Long shopId);

    // Active promotions by shop with pagination
    @Query("SELECT p FROM ProductEntity p WHERE " +
            "p.shop.id = :shopId AND (" +
            "(p.discountStartDate IS NOT NULL AND p.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN p.discountStartDate AND p.discountEndDate) " +
            "OR EXISTS (SELECT ps FROM ProductSizeEntity ps WHERE ps.product = p AND " +
            "(ps.discountStartDate IS NOT NULL AND ps.discountEndDate IS NOT NULL AND " +
            "CURRENT_DATE BETWEEN ps.discountStartDate AND ps.discountEndDate)))")
    Page<ProductEntity> findAllWithActivePromotionsByShopId(@Param("shopId") Long shopId, Pageable pageable);
}