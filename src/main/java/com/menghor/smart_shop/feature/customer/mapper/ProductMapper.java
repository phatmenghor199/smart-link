package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductSizeResponseDto;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.ImageMapper;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ImageMapper.class})
public abstract class ProductMapper {

    @Autowired
    protected ImageMapper imageMapper;

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(target = "finalPrice", expression = "java(product.getFinalPrice())")
    @Mapping(target = "promotionStatus", expression = "java(product.getPromotionStatus().name())")
    @Mapping(target = "mainImage", expression = "java(mapImageWithoutBase64(product.getMainImage()))")
    @Mapping(target = "additionalImages", expression = "java(mapImagesWithoutBase64(product.getAdditionalImages()))")
    @Mapping(target = "sizes", expression = "java(mapSizesWithoutBase64(product.getSizes()))")
    public abstract ProductResponseDto toDto(ProductEntity product);

    // Helper method to map image without accessing base64 content
    protected ImageResponseDto mapImageWithoutBase64(ImageEntity image) {
        if (image == null || image.getId() == null) return null;

        ImageResponseDto dto = new ImageResponseDto();
        dto.setId(image.getId());
        dto.setUrl("/api/v1/images/" + image.getId());
        return dto;
    }

    // Helper method to map multiple images without accessing base64 content
    protected List<ImageResponseDto> mapImagesWithoutBase64(List<ImageEntity> images) {
        if (images == null || images.isEmpty()) return new ArrayList<>();

        return images.stream()
                .filter(Objects::nonNull)
                .filter(img -> img.getId() != null)
                .map(this::mapImageWithoutBase64)
                .collect(Collectors.toList());
    }

    // Helper method to map sizes without accessing base64 content in their images
    protected List<ProductSizeResponseDto> mapSizesWithoutBase64(List<ProductSizeEntity> sizes) {
        if (sizes == null || sizes.isEmpty()) return new ArrayList<>();

        return sizes.stream()
                .filter(Objects::nonNull)
                .map(size -> {
                    ProductSizeResponseDto dto = new ProductSizeResponseDto();
                    dto.setId(size.getId());
                    dto.setSize(size.getSize());
                    dto.setPrice(size.getPrice());
                    dto.setFinalPrice(size.getFinalPrice());
                    dto.setPromotionStatus(size.getPromotionStatus().name());
                    dto.setDiscountType(size.getDiscountType());
                    dto.setDiscountValue(size.getDiscountValue());
                    dto.setDiscountStartDate(size.getDiscountStartDate());
                    dto.setDiscountEndDate(size.getDiscountEndDate());
                    dto.setStatus(size.getStatus());
                    dto.setProductId(size.getProduct().getId());
                    dto.setMainImage(mapImageWithoutBase64(size.getMainImage()));
                    dto.setAdditionalImages(mapImagesWithoutBase64(size.getAdditionalImages()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "finalPrice", expression = "java(size.getFinalPrice())")
    @Mapping(target = "promotionStatus", expression = "java(size.getPromotionStatus().name())")
    @Mapping(target = "mainImage", expression = "java(mapImageWithoutBase64(size.getMainImage()))")
    @Mapping(target = "additionalImages", expression = "java(mapImagesWithoutBase64(size.getAdditionalImages()))")
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

    // Method to update or add images for a product
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