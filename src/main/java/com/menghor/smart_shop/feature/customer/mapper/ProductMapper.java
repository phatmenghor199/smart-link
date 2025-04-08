package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.enumations.PromotionStatus;
import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductSizeResponseDto;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.ImageMapper;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ImageMapper.class})
public abstract class ProductMapper {

    @Autowired
    protected ImageMapper imageMapper;

    // Comprehensive mapping for ProductEntity to ProductResponseDto
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(target = "finalPrice", expression = "java(getProductFinalPrice(product))")
    @Mapping(target = "promotionStatus", expression = "java(getProductPromotionStatus(product))")
    @Mapping(target = "mainImage", qualifiedByName = "mapProductMainImage")
    @Mapping(target = "additionalImages", qualifiedByName = "mapProductAdditionalImages")
    @Mapping(target = "sizes", qualifiedByName = "mapProductSizes")
    @Mapping(target = "minPrice", ignore = true)
    @Mapping(target = "maxPrice", ignore = true)
    @Mapping(target = "maxDiscountPercentage", ignore = true)
    @Mapping(target = "hasActivePromotion", ignore = true)
    @Mapping(target = "sizeCount", ignore = true)
    @Mapping(target = "hasSizes", ignore = true)
    public abstract ProductResponseDto toDto(ProductEntity product);

    @AfterMapping
    protected void calculatePricingSummary(ProductEntity product, @MappingTarget ProductResponseDto dto) {
        // Check if product has sizes
        boolean hasSizes = product.getSizes() != null && !product.getSizes().isEmpty();
        dto.setHasSizes(hasSizes);

        if (hasSizes) {
            // Count sizes
            dto.setSizeCount(product.getSizes().size());

            // Calculate min and max prices
            List<Double> finalPrices = product.getSizes().stream()
                    .map(ProductSizeEntity::getFinalPrice)
                    .collect(Collectors.toList());

            dto.setMinPrice(Collections.min(finalPrices));
            dto.setMaxPrice(Collections.max(finalPrices));

            // Find highest discount percentage and check for active promotions
            double maxDiscount = 0.0;
            boolean hasPromotion = false;

            for (ProductSizeEntity size : product.getSizes()) {
                if (size.isPromotionActive()) {
                    hasPromotion = true;
                    if (size.getDiscountType() == DiscountType.PERCENTAGE &&
                            size.getDiscountValue() != null &&
                            size.getDiscountValue() > maxDiscount) {
                        maxDiscount = size.getDiscountValue();
                    }
                }
            }

            dto.setMaxDiscountPercentage(maxDiscount > 0 ? maxDiscount : null);
            dto.setHasActivePromotion(hasPromotion);
        } else {
            // For product without sizes
            dto.setMinPrice(product.getFinalPrice());
            dto.setMaxPrice(product.getFinalPrice());
            dto.setSizeCount(0);

            boolean hasPromotion = product.isPromotionActive();
            dto.setHasActivePromotion(hasPromotion);

            if (hasPromotion && product.getDiscountType() == DiscountType.PERCENTAGE) {
                dto.setMaxDiscountPercentage(product.getDiscountValue());
            } else {
                dto.setMaxDiscountPercentage(null);
            }
        }
    }

    // Pagination helper method
    public CustomPaginationResponseDto<ProductResponseDto> toPaginationDto(
            List<ProductEntity> products,
            Page<ProductEntity> productPage
    ) {
        // Convert entities to DTOs
        List<ProductResponseDto> productDtos = products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        CustomPaginationResponseDto<ProductResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(productDtos);
        response.setPageNo(productPage.getNumber() + 1);
        response.setPageSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    // Helper methods for mapping with null safety
    protected Double getProductFinalPrice(ProductEntity product) {
        return product != null ? product.getFinalPrice() : null;
    }

    protected String getProductPromotionStatus(ProductEntity product) {
        return product != null && product.getPromotionStatus() != null
                ? product.getPromotionStatus().name()
                : null;
    }

    @Named("mapProductMainImage")
    protected ImageResponseDto mapProductMainImage(ImageEntity image) {
        return imageMapper.toDto(image);
    }

    @Named("mapProductAdditionalImages")
    protected List<ImageResponseDto> mapProductAdditionalImages(List<ImageEntity> images) {
        if (images == null) return null;
        return images.stream()
                .map(imageMapper::toDto)
                .collect(Collectors.toList());
    }

    @Named("mapProductSizes")
    protected List<ProductSizeResponseDto> mapProductSizes(List<ProductSizeEntity> sizes) {
        if (sizes == null) return null;
        return sizes.stream()
                .map(this::mapProductSize)
                .collect(Collectors.toList());
    }

    // Detailed mapping for individual product size
    protected ProductSizeResponseDto mapProductSize(ProductSizeEntity size) {
        if (size == null) return null;

        ProductSizeResponseDto sizeDto = new ProductSizeResponseDto();
        sizeDto.setId(size.getId());
        sizeDto.setSize(size.getSize());
        sizeDto.setPrice(size.getPrice());
        sizeDto.setFinalPrice(size.getFinalPrice());
        sizeDto.setPromotionStatus(size.getPromotionStatus() != null
                ? size.getPromotionStatus().name()
                : null);
        sizeDto.setDiscountType(size.getDiscountType());
        sizeDto.setDiscountValue(size.getDiscountValue());
        sizeDto.setDiscountStartDate(size.getDiscountStartDate());
        sizeDto.setDiscountEndDate(size.getDiscountEndDate());
        sizeDto.setStatus(size.getStatus());
        sizeDto.setProductId(size.getProduct() != null ? size.getProduct().getId() : null);

        // Map size main image
        sizeDto.setMainImage(imageMapper.toDto(size.getMainImage()));

        // Map size additional images
        if (size.getAdditionalImages() != null) {
            sizeDto.setAdditionalImages(
                    size.getAdditionalImages().stream()
                            .map(imageMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        return sizeDto;
    }

    // Entity to DTO mappings
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "mainImage", source = "image")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract ProductEntity toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "mainImage", source = "image")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract ProductSizeEntity toSizeEntity(ProductSizeRequestDto sizeRequestDto);

    // Update methods with null value property mapping strategy
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mainImage.referenceType", constant = "product")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract void updateProductFromDto(ProductRequestDto dto, @MappingTarget ProductEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mainImage.referenceType", constant = "product_size")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract void updateSizeFromDto(ProductSizeRequestDto dto, @MappingTarget ProductSizeEntity entity);

    // Image update methods
    public void updateProductImages(ProductEntity product, List<ImageEntity> images) {
        // Clear existing images
        if (product.getAdditionalImages() != null) {
            product.getAdditionalImages().clear();
        }

        // Add new images
        if (images != null && !images.isEmpty()) {
            images.forEach(image -> {
                image.setReferenceType("product");
                product.addAdditionalImage(image);
            });
        }
    }

    // Method to update or add images for a product size
    public void updateProductSizeImages(ProductSizeEntity size, List<ImageEntity> images) {
        // Clear existing images
        if (size.getAdditionalImages() != null) {
            size.getAdditionalImages().clear();
        }

        // Add new images
        if (images != null && !images.isEmpty()) {
            images.forEach(image -> {
                image.setReferenceType("product_size");
                size.addAdditionalImage(image);
            });
        }
    }
}