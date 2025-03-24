package com.menghor.smart_shop.feature.setting.model;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.enumations.SubscriptionActionType;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "subscription_histories")
@Data
public class SubscriptionHistoryEntity extends BaseEntity {
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
    
    @Enumerated(EnumType.STRING)
    private SubscriptionActionType actionType;
    
    // Reference to the subscription
    private Long subscriptionId;
    
    // Payment information
    private String transactionId;
    private Double amountPaid;
    
    // Notes about the action
    private String notes;
}