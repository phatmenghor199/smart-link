package com.menghor.smart_shop.feature.customer.repository;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
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
    List<ProductEntity> findByDiscountEndDateBefore(LocalDate now);
}