package com.menghor.smart_shop.feature.customer.models;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.enumations.PromotionStatus;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_sizes")
@Data
@NoArgsConstructor
public class ProductSizeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String size; // e.g., S, M, L, XL

    private Double price;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // Enum for discount type (PERCENTAGE or FIXED_AMOUNT)

    private Double discountValue; // Holds the % or $ discount
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    // Add status field
    @Enumerated(EnumType.STRING)
    private StatusData status = StatusData.ACTIVE; // Default to active

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product; // Each size belongs to a product

    // Main size image (single image)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "main_image_id")
    private ImageEntity mainImage;

    // Multiple size images
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "product_size_additional_images",
            joinColumns = @JoinColumn(name = "product_size_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id"))
    private List<ImageEntity> additionalImages = new ArrayList<>();

    // Compute final price based on discount type
    public Double getFinalPrice() {
        if (isPromotionActive()) {
            double calculatedPrice;
            if (discountType == DiscountType.PERCENTAGE) {
                calculatedPrice = Math.max(price - (price * discountValue / 100), 0); // Ensure the price doesn't go below 0
            } else if (discountType == DiscountType.FIXED_AMOUNT) {
                calculatedPrice = Math.max(price - discountValue, 0); // Ensure the price doesn't go below 0
            } else {
                return price;
            }

            // Round to 2 decimal places
            BigDecimal bd = new BigDecimal(calculatedPrice).setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }
        return price;
    }

    // Method to add an additional image
    public void addAdditionalImage(ImageEntity image) {
        if (this.additionalImages == null) {
            this.additionalImages = new ArrayList<>();
        }
        this.additionalImages.add(image);
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

    public void resetDiscount() {
        this.discountType = null;
        this.discountValue = null;
        this.discountStartDate = null;
        this.discountEndDate = null;
    }
}