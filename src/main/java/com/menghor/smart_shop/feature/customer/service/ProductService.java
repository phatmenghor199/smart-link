package com.menghor.smart_shop.feature.customer.service;

import java.util.List;

import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    ProductResponseDto addSizesToProduct(Long productId, List<ProductSizeRequestDto> sizeRequest);
    ProductResponseDto getProductById(Long id);
    List<ProductResponseDto> getProductsByShop();

    List<ProductResponseDto> getAllProducts();

    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto);
    ProductResponseDto deleteProduct(Long id);

    void resetExpiredDiscounts();
    ProductResponseDto resetExpiredDiscountForProduct(Long productId);
    ProductResponseDto resetDiscountForProduct(Long productId);

    List<ProductResponseDto> getProductsWithActivePromotions();
    List<ProductResponseDto> getProductsWithActivePromotionsByShop();


}
