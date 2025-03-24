package com.menghor.smart_shop.feature.setting.model;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subscriptions")
@Data
public class SubscriptionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanEntity plan;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Status status;
    
    private Boolean autoRenew = false;
    
    // Payment information
    private String transactionId;
    private Double amountPaid;
    
    // Previous subscription id for tracking renewals/upgrades
    private Long previousSubscriptionId;
}