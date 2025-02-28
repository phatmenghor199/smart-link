package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;

import java.util.List;

public interface ProductService {
    ProductResponseDto createProduct(ProductRequestDto requestDto);
    ProductResponseDto getProductById(Long id);
    List<ProductResponseDto> getProductsByShop();

    ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto);
    ProductResponseDto deleteProduct(Long id);

    void resetExpiredDiscounts();
    ProductResponseDto resetDiscountForProduct(Long productId);
}
