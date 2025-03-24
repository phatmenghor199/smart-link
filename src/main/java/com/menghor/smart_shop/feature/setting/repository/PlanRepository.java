package com.menghor.smart_shop.feature.setting.repository;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.feature.setting.model.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<PlanEntity, Long> {
    List<PlanEntity> findByStatus(Status status);
    
    List<PlanEntity> findByStatusOrderByPriceAsc(Status status);
}