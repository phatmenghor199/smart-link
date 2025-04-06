package com.menghor.smart_shop.feature.customer.specification;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<ProductEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<ProductEntity> hasStatus(StatusData status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<ProductEntity> belongsToShop(Long shopId) {
        return (root, query, criteriaBuilder) -> {
            if (shopId == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("shop").get("id"), shopId);
        };
    }

    public static Specification<ProductEntity> belongsToCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> {
            if (categoryId == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("category").get("id"), categoryId);
        };
    }

    public static Specification<ProductEntity> hasActivePromotion(Boolean hasPromotion) {
        return (root, query, criteriaBuilder) -> {
            if (hasPromotion == null) return criteriaBuilder.conjunction();
            
            LocalDate today = LocalDate.now();
            
            if (hasPromotion) {
                // Product has promotion on main product or any size
                Predicate mainProductPromotion = criteriaBuilder.and(
                    criteriaBuilder.isNotNull(root.get("discountStartDate")),
                    criteriaBuilder.isNotNull(root.get("discountEndDate")),
                    criteriaBuilder.lessThanOrEqualTo(root.get("discountStartDate"), today),
                    criteriaBuilder.greaterThanOrEqualTo(root.get("discountEndDate"), today)
                );

                // Check if any sizes have promotions
                Join<ProductEntity, ProductSizeEntity> sizeJoin = root.join("sizes", JoinType.LEFT);
                Predicate sizePromotion = criteriaBuilder.and(
                    criteriaBuilder.isNotNull(sizeJoin.get("discountStartDate")),
                    criteriaBuilder.isNotNull(sizeJoin.get("discountEndDate")),
                    criteriaBuilder.lessThanOrEqualTo(sizeJoin.get("discountStartDate"), today),
                    criteriaBuilder.greaterThanOrEqualTo(sizeJoin.get("discountEndDate"), today)
                );

                // Either main product or size has promotion
                return criteriaBuilder.or(mainProductPromotion, sizePromotion);
            } else {
                // Product has no promotions
                Predicate noMainPromotion = criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("discountStartDate")),
                    criteriaBuilder.isNull(root.get("discountEndDate")),
                    criteriaBuilder.greaterThan(root.get("discountStartDate"), today),
                    criteriaBuilder.lessThan(root.get("discountEndDate"), today)
                );

                // Ensure no sizes have promotions
                query.distinct(true);
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(noMainPromotion);
                
                // For the sizes, we need a subquery to check that no sizes have active promotions
                // This is a complex scenario, simpler approach:
                return criteriaBuilder.and(noMainPromotion);
            }
        };
    }

    public static Specification<ProductEntity> createSpecification(
            String name, 
            StatusData status, 
            Long shopId,
            Long categoryId, 
            Boolean hasPromotion) {
            
        Specification<ProductEntity> spec = Specification.where(null);

        if (StringUtils.hasText(name)) {
            spec = spec.and(hasName(name));
        }

        if (status != null) {
            spec = spec.and(hasStatus(status));
        }

        if (shopId != null) {
            spec = spec.and(belongsToShop(shopId));
        }

        if (categoryId != null) {
            spec = spec.and(belongsToCategory(categoryId));
        }

        if (hasPromotion != null) {
            spec = spec.and(hasActivePromotion(hasPromotion));
        }

        return spec;
    }
}