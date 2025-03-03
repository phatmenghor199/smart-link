package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductSizeResponseDto;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(target = "finalPrice", expression = "java(product.getFinalPrice())") // Mapping finalPrice
    @Mapping(target = "promotionStatus", expression = "java(product.getPromotionStatus().name())") // Mapping promotionStatus
    ProductResponseDto toDto(ProductEntity product);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(target = "finalPrice", expression = "java(size.getFinalPrice())") // Mapping finalPrice
    @Mapping(target = "promotionStatus", expression = "java(size.getPromotionStatus().name())") // Mapping promotionStatus
    ProductSizeResponseDto toSizeDto(ProductSizeEntity size);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "shop", ignore = true)
    ProductEntity toEntity(ProductRequestDto productRequestDto);

    @Mapping(target = "product", ignore = true)
    ProductSizeEntity toSizeEntity(ProductSizeRequestDto sizeRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(ProductRequestDto dto, @MappingTarget ProductEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSizeFromDto(ProductSizeRequestDto dto, @MappingTarget ProductSizeEntity entity);
}
