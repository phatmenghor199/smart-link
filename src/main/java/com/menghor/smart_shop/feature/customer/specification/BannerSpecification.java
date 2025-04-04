package com.menghor.smart_shop.feature.customer.specification;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public class BannerSpecification {

    public static Specification<BannerEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) return criteriaBuilder.conjunction();
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<BannerEntity> hasStatus(StatusData status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    public static Specification<BannerEntity> belongsToShop(Long shopId) {
        return (root, query, criteriaBuilder) -> {
            if (shopId == null) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("shop").get("id"), shopId);
        };
    }

    public static Specification<BannerEntity> createSpecification(String name, StatusData status, Long shopId) {
        Specification<BannerEntity> spec = Specification.where(null);

        if (StringUtils.hasText(name)) {
            spec = spec.and(hasName(name));
        }

        if (status != null) {
            spec = spec.and(hasStatus(status));
        }

        if (shopId != null) {
            spec = spec.and(belongsToShop(shopId));
        }

        return spec;
    }
}