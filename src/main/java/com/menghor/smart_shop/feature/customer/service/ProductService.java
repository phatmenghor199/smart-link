package com.menghor.smart_shop.feature.customer.service;

import java.util.List;

import com.menghor.smart_shop.feature.customer.dto.request.ProductFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

public interface ProductService {
    // Create methods
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    ProductResponseDto addSizesToProduct(Long productId, List<ProductSizeRequestDto> sizeRequest);

    // Get products with pagination and filtering
    CustomPaginationResponseDto<ProductResponseDto> getProductsByShopWithFilter(ProductFilterDto filterDto);

    // Get simple list of products by shop
    List<ProductResponseDto> getProductsByShop();

    // Get products by category
    List<ProductResponseDto> getProductsByCategory(Long categoryId);
    CustomPaginationResponseDto<ProductResponseDto> getProductsByCategoryWithFilter(Long categoryId, ProductFilterDto filterDto);

    // Get products by id
    ProductResponseDto getProductById(Long id);

    // Get all products (usually for admin)
    List<ProductResponseDto> getAllProducts();
    CustomPaginationResponseDto<ProductResponseDto> getAllProductsWithFilter(ProductFilterDto filterDto);

    // Update methods
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto);

    // Delete methods
    ProductResponseDto deleteProduct(Long id);

    // Toggle status methods
    ProductResponseDto toggleProductSizeStatus(Long productId, Long sizeId);
    ProductResponseDto toggleProductStatus(Long productId);

    // Discount related methods
    void resetExpiredDiscounts();
    ProductResponseDto resetExpiredDiscountForProduct(Long productId);
    ProductResponseDto resetDiscountForProduct(Long productId);

    // Promotion related methods
    List<ProductResponseDto> getProductsWithActivePromotions();
    CustomPaginationResponseDto<ProductResponseDto> getProductsWithActivePromotionsWithFilter(ProductFilterDto filterDto);
    List<ProductResponseDto> getProductsWithActivePromotionsByShop();
    CustomPaginationResponseDto<ProductResponseDto> getProductsWithActivePromotionsByShopWithFilter(ProductFilterDto filterDto);

    // Image management methods
    ProductResponseDto addImagesToProduct(Long productId, List<ImageEntity> images);
    ProductResponseDto removeImageFromProduct(Long productId, String imageId);
    ProductResponseDto addImagesToProductSize(Long productId, Long sizeId, List<ImageEntity> images);
    ProductResponseDto removeImageFromProductSize(Long productId, Long sizeId, String imageId);
}