package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductSizeResponseDto;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.setting.mapper.ImageMapper;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ImageMapper.class})
public abstract class ProductMapper {

    @Autowired
    protected ImageMapper imageMapper;

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(target = "finalPrice", expression = "java(product.getFinalPrice())")
    @Mapping(target = "promotionStatus", expression = "java(product.getPromotionStatus().name())")
    @Mapping(target = "mainImage", source = "mainImage")
    @Mapping(target = "additionalImages", expression = "java(mapAdditionalImages(product))")
    public abstract ProductResponseDto toDto(ProductEntity product);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "finalPrice", expression = "java(size.getFinalPrice())")
    @Mapping(target = "promotionStatus", expression = "java(size.getPromotionStatus().name())")
    @Mapping(target = "mainImage", source = "mainImage")
    @Mapping(target = "additionalImages", expression = "java(mapAdditionalImagesForSize(size))")
    public abstract ProductSizeResponseDto toSizeDto(ProductSizeEntity size);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "mainImage", source = "image")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract ProductEntity toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "mainImage", source = "image")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract ProductSizeEntity toSizeEntity(ProductSizeRequestDto sizeRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mainImage.referenceType", constant = "product")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract void updateProductFromDto(ProductRequestDto dto, @MappingTarget ProductEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mainImage.referenceType", constant = "product_size")
    @Mapping(target = "additionalImages", ignore = true)
    public abstract void updateSizeFromDto(ProductSizeRequestDto dto, @MappingTarget ProductSizeEntity entity);

    // Helper method to map additional images for products
    protected List<com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto> mapAdditionalImages(ProductEntity product) {
        if (product.getAdditionalImages() == null) {
            return null;
        }
        return product.getAdditionalImages().stream()
                .map(imageMapper::toDto)
                .collect(Collectors.toList());
    }

    // Helper method to map additional images for product sizes
    protected List<com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto> mapAdditionalImagesForSize(ProductSizeEntity size) {
        if (size.getAdditionalImages() == null) {
            return null;
        }
        return size.getAdditionalImages().stream()
                .map(imageMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method to update or add additional images for a product
    public void updateProductAdditionalImages(ProductEntity product, List<ImageEntity> additionalImages) {
        // Clear existing additional images
        if (product.getAdditionalImages() != null) {
            product.getAdditionalImages().clear();
        }

        // Add new additional images
        if (additionalImages != null) {
            additionalImages.forEach(product::addAdditionalImage);
        }
    }

    // Method to update or add additional images for a product size
    public void updateProductSizeAdditionalImages(ProductSizeEntity size, List<ImageEntity> additionalImages) {
        // Clear existing additional images
        if (size.getAdditionalImages() != null) {
            size.getAdditionalImages().clear();
        }

        // Add new additional images
        if (additionalImages != null) {
            additionalImages.forEach(size::addAdditionalImage);
        }
    }
}