package com.menghor.smart_shop.feature.customer.service;

import java.util.List;

import com.menghor.smart_shop.feature.customer.dto.request.ProductFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

public interface ProductService {
    // Create methods
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    ProductResponseDto addSizesToProduct(Long productId, List<ProductSizeRequestDto> sizeRequest);

    // Get methods with ID
    ProductResponseDto getProductById(Long id);

    // Get all products for a shop
    List<ProductResponseDto> getProductsByShop();

    // Get products with pagination and filtering
    CustomPaginationResponseDto<ProductResponseDto> getProductsByShopWithFilter(ProductFilterDto filterDto);

    // Get products by category
    List<ProductResponseDto> getProductsByCategory(Long categoryId);
    CustomPaginationResponseDto<ProductResponseDto> getProductsByCategoryWithFilter(Long categoryId, ProductFilterDto filterDto);

    // Get all products (usually for admin)
    List<ProductResponseDto> getAllProducts();
    CustomPaginationResponseDto<ProductResponseDto> getAllProductsWithFilter(ProductFilterDto filterDto);

    // Update methods
    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto);

    // Delete methods
    ProductResponseDto deleteProduct(Long id);

    // Product size status toggle
    ProductResponseDto toggleProductSizeStatus(Long productId, Long sizeId);

    // Product status toggle
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
}