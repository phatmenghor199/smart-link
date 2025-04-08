package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.enumations.PromotionStatus;
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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ImageMapper.class})
public abstract class ProductMapper {

    @Autowired
    protected ImageMapper imageMapper;

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(target = "finalPrice", expression = "java(getProductFinalPrice(product))")
    @Mapping(target = "promotionStatus", expression = "java(getProductPromotionStatus(product))")
    @Mapping(target = "mainImage", qualifiedByName = "mapProductMainImage")
    @Mapping(target = "additionalImages", qualifiedByName = "mapProductAdditionalImages")
    @Mapping(target = "sizes", qualifiedByName = "mapProductSizes")
    public abstract ProductResponseDto toDto(ProductEntity product);

    @AfterMapping
    protected void calculatePromotionDetails(
            @MappingTarget ProductResponseDto dto,
            @Context ProductEntity product
    ) {
        boolean hasSizes = product.getSizes() != null && !product.getSizes().isEmpty();

        // If product has sizes, clear product-level discount and image fields
        if (hasSizes) {
            // Reset product-level discount and image fields
            dto.setDiscountType(null);
            dto.setDiscountValue(null);
            dto.setDiscountStartDate(null);
            dto.setDiscountEndDate(null);
            dto.setMainImage(null);
            dto.setAdditionalImages(null);
            dto.setPromotionStatus(PromotionStatus.INACTIVE.name());

            // Filter sizes that have active promotions
            List<ProductSizeEntity> promotionalSizes = product.getSizes().stream()
                    .filter(ProductSizeEntity::isPromotionActive) // Only include sizes with active promotions
                    .sorted(Comparator.comparing(ProductSizeEntity::getDiscountValue).reversed()) // Sort by highest discount
                    .collect(Collectors.toList());

            // If there are promotional sizes, take the one with the highest discount
            ProductSizeEntity selectedSize = promotionalSizes.isEmpty()
                    ? product.getSizes().get(0) // If no size has promotion, take the first size
                    : promotionalSizes.get(0); // Take the size with the highest discount

            // Set promotion details from the selected size
            dto.setDiscountType(selectedSize.getDiscountType());
            dto.setDiscountValue(selectedSize.getDiscountValue());
            dto.setDiscountStartDate(selectedSize.getDiscountStartDate());
            dto.setDiscountEndDate(selectedSize.getDiscountEndDate());
            dto.setPromotionStatus(PromotionStatus.ACTIVE.name());
            dto.setFinalPrice(selectedSize.getFinalPrice());

            // Set images from the selected size (if available)
            dto.setMainImage(imageMapper.toDto(selectedSize.getMainImage()));
            if (selectedSize.getAdditionalImages() != null) {
                dto.setAdditionalImages(
                        selectedSize.getAdditionalImages().stream()
                                .map(imageMapper::toDto)
                                .collect(Collectors.toList())
                );
            }
        } else {
            // If the product doesn't have sizes, use the product-level data
            dto.setDiscountType(product.getDiscountType());
            dto.setDiscountValue(product.getDiscountValue());
            dto.setDiscountStartDate(product.getDiscountStartDate());
            dto.setDiscountEndDate(product.getDiscountEndDate());
            dto.setMainImage(imageMapper.toDto(product.getMainImage()));
            dto.setAdditionalImages(product.getAdditionalImages() != null
                    ? product.getAdditionalImages().stream().map(imageMapper::toDto).collect(Collectors.toList())
                    : null);
            dto.setPromotionStatus(getProductPromotionStatus(product));
            dto.setFinalPrice(getProductFinalPrice(product));
        }
    }


    @Named("mapProductMainImage")
    public ImageResponseDto mapProductMainImage(ImageEntity image) {
        return imageMapper.toDto(image);
    }

    protected Double getProductFinalPrice(ProductEntity product) {
        return product != null ? product.getFinalPrice() : null;
    }

    protected String getProductPromotionStatus(ProductEntity product) {
        return product != null && product.getPromotionStatus() != null
                ? product.getPromotionStatus().name()
                : PromotionStatus.INACTIVE.name();
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

    protected ProductSizeResponseDto mapProductSize(ProductSizeEntity size) {
        if (size == null) return null;

        ProductSizeResponseDto sizeDto = new ProductSizeResponseDto();
        sizeDto.setId(size.getId());
        sizeDto.setSize(size.getSize());
        sizeDto.setPrice(size.getPrice());
        sizeDto.setFinalPrice(size.getFinalPrice());
        sizeDto.setPromotionStatus(size.getPromotionStatus() != null
                ? size.getPromotionStatus().name()
                : PromotionStatus.INACTIVE.name());
        sizeDto.setDiscountType(size.getDiscountType());
        sizeDto.setDiscountValue(size.getDiscountValue());
        sizeDto.setDiscountStartDate(size.getDiscountStartDate());
        sizeDto.setDiscountEndDate(size.getDiscountEndDate());
        sizeDto.setStatus(size.getStatus());
        sizeDto.setProductId(size.getProduct() != null ? size.getProduct().getId() : null);
        sizeDto.setMainImage(imageMapper.toDto(size.getMainImage()));

        if (size.getAdditionalImages() != null) {
            sizeDto.setAdditionalImages(
                    size.getAdditionalImages().stream()
                            .map(imageMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        return sizeDto;
    }

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

    public CustomPaginationResponseDto<ProductResponseDto> toPaginationDto(
            List<ProductEntity> products,
            Page<ProductEntity> productPage
    ) {
        List<ProductResponseDto> productDtos = products.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        CustomPaginationResponseDto<ProductResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(productDtos);
        response.setPageNo(productPage.getNumber() + 1);
        response.setPageSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    public void updateProductImages(ProductEntity product, List<ImageEntity> images) {
        if (product.getAdditionalImages() != null) {
            product.getAdditionalImages().clear();
        }

        if (images != null && !images.isEmpty()) {
            images.forEach(image -> {
                image.setReferenceType("product");
                product.addAdditionalImage(image);
            });
        }
    }

    public void updateProductSizeImages(ProductSizeEntity size, List<ImageEntity> images) {
        if (size.getAdditionalImages() != null) {
            size.getAdditionalImages().clear();
        }

        if (images != null && !images.isEmpty()) {
            images.forEach(image -> {
                image.setReferenceType("product_size");
                size.addAdditionalImage(image);
            });
        }
    }
}
