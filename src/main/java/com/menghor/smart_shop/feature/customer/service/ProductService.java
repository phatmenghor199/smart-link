package com.menghor.smart_shop.feature.customer.service;

import java.util.List;
import java.util.UUID;

import com.menghor.smart_shop.feature.customer.dto.request.ProductFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {
    // Create methods
    @Transactional
    ProductResponseDto createProduct(ProductRequestDto requestDto);

    @Transactional
    ProductResponseDto addSizesToProduct(Long productId, List<ProductSizeRequestDto> sizeRequest);

    // Get products with pagination and filtering
    @Transactional(readOnly = true)
    CustomPaginationResponseDto<ProductResponseDto> getProductsByShopWithFilter(ProductFilterDto filterDto);

    // Get product by id
    @Transactional(readOnly = true)
    ProductResponseDto getProductById(Long id);

    // Update methods
    @Transactional
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);

    @Transactional
    ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto);

    // Delete methods
    @Transactional
    ProductResponseDto deleteProduct(Long id);

    // Discount reset method
    @Transactional
    ProductResponseDto resetDiscountForProduct(Long productId);

    ProductResponseDto deleteProductSize(Long productId, Long sizeId);

    // Product main image operations
    ProductResponseDto updateProductMainImage(Long productId, ImageRequestDto imageRequest);
    ProductResponseDto addProductAdditionalImages(Long productId, List<ImageRequestDto> imageRequests);
    ProductResponseDto removeProductAdditionalImage(Long productId, UUID imageId);

    // Product size image operations
    ProductResponseDto updateProductSizeMainImage(Long productId, Long sizeId, ImageRequestDto imageRequest);
    ProductResponseDto addProductSizeAdditionalImages(Long productId, Long sizeId, List<ImageRequestDto> imageRequests);
    ProductResponseDto removeProductSizeAdditionalImage(Long productId, Long sizeId, UUID imageId);
}