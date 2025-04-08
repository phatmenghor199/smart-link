package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Product name must be between 2 and 255 characters")
    private String name;

    private Double price;
    private String description;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private StatusData status = StatusData.ACTIVE;

    // Optional discount fields
    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    // Optional images
    private ImageRequestDto image;
    private List<ImageRequestDto> additionalImages;

    // Product sizes
    private List<ProductSizeRequestDto> sizes;
}