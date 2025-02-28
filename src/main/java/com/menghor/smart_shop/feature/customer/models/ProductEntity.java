package com.menghor.smart_shop.feature.customer.models;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.enumations.PromotionStatus;
import com.menghor.smart_shop.utils.database.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class ProductEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private String description;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // Enum for discount type (PERCENTAGE or FIXED_AMOUNT)

    private Double discountValue; // Holds the % or $ discount
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category; // Each product belongs to a category

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop; // Each product belongs to a shop


    // Compute final price based on discount type
    public Double getFinalPrice() {
        if (isPromotionActive()) {
            if (discountType == DiscountType.PERCENTAGE) {
                return Math.max(price - (price * discountValue / 100), 0); // Ensure the price doesn't go below 0
            } else if (discountType == DiscountType.FIXED_AMOUNT) {
                return Math.max(price - discountValue, 0); // Ensure the price doesn't go below 0
            }
        }
        return price;
    }

    // Method to manually reset the discount
    public void resetDiscount() {
        this.discountType = null;
        this.discountValue = null;
        this.discountStartDate = null;
        this.discountEndDate = null;
    }


    // Check if the promotion is active
    public boolean isPromotionActive() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Phnom_Penh")); // Cambodia's time zone
        return discountStartDate != null && discountEndDate != null &&
                !today.isBefore(discountStartDate) && !today.isAfter(discountEndDate);
    }

    // Get Promotion Status (ACTIVE/INACTIVE)
    public PromotionStatus getPromotionStatus() {
        return isPromotionActive() ? PromotionStatus.ACTIVE : PromotionStatus.INACTIVE;
    }

    // Validate discount before saving/updating
    public void validateDiscount() {
        // Check if discount dates are valid
        if (discountStartDate != null && discountEndDate != null && discountStartDate.isAfter(discountEndDate)) {
            throw new IllegalArgumentException("Discount start date cannot be after the end date.");
        }

        // Validate discount value based on discount type
        if (discountType != null) {
            if (discountType == DiscountType.PERCENTAGE) {
                // Percentage discount must be between 0 and 100
                if (discountValue <= 0 || discountValue > 100) {
                    throw new IllegalArgumentException("Percentage discount must be between 0 and 100.");
                }
            } else if (discountType == DiscountType.FIXED_AMOUNT) {
                // Fixed discount must be positive and cannot exceed the product price
                if (discountValue <= 0) {
                    throw new IllegalArgumentException("Fixed discount must be a positive value.");
                }
                if (discountValue > price) {
                    throw new IllegalArgumentException("Fixed discount cannot exceed the product price.");
                }
            }
        }

        // Check if final price would be negative
        if (getFinalPrice() < 0) {
            throw new IllegalArgumentException("The discount cannot result in a negative final price.");
        }
    }
}