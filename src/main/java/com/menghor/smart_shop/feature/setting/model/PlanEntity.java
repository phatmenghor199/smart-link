package com.menghor.smart_shop.feature.setting.model;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "plans")
@Data
public class PlanEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    
    // Duration in days
    private Integer durationDays;
    
    private Double price;
    
    // Features and limitations
    private Integer maxProducts;
    private Boolean allowBanners;
    private Boolean allowPromotions;
    private Boolean allowDelivery;
    
    @Enumerated(EnumType.STRING)
    private Status status;
}