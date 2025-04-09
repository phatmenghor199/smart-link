package com.menghor.smart_shop.feature.customer.controller;

import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.customer.dto.request.ProductFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.service.ProductService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.Delete;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    /**
     * Create a new product
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto createRequest
    ) {
        log.info("Received request to create product");
        ProductResponseDto createdProduct = productService.createProduct(createRequest);
        return new ApiResponse<>("Success", "Product created successfully", createdProduct);
    }

    /**
     * Add sizes to an existing product
     */
    @PostMapping("/{productId}/sizes")
    public ApiResponse<ProductResponseDto> addSizesToProduct(
            @PathVariable Long productId,
            @Valid @RequestBody List<ProductSizeRequestDto> sizeRequest
    ) {
        log.info("Received request to add sizes to product with ID: {}", productId);
        ProductResponseDto updatedProduct = productService.addSizesToProduct(productId, sizeRequest);
        return new ApiResponse<>("Success", "Sizes added successfully", updatedProduct);
    }

    /**
     * Get products with filtering and pagination
     */
    @PostMapping("/shop/all")
    public ApiResponse<CustomPaginationResponseDto<ProductResponseDto>> getProductsByShopWithFilter(
            @RequestBody ProductFilterDto filterDto
    ) {
        log.info("Received request to get products with filter");
        CustomPaginationResponseDto<ProductResponseDto> products =
                productService.getProductsByShopWithFilter(filterDto);
        return new ApiResponse<>("Success", "Products retrieved successfully", products);
    }

    /**
     * Get a specific product by ID
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProductById(
            @PathVariable Long productId
    ) {
        log.info("Received request to get product by ID: {}", productId);
        ProductResponseDto product = productService.getProductById(productId);
        return new ApiResponse<>("Success", "Product retrieved successfully", product);
    }

    /**
     * Update a product
     */
    @PutMapping("/{productId}")
    public ApiResponse<ProductResponseDto> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody ProductRequestDto updateRequest
    ) {
        log.info("Received request to update product with ID: {}", productId);
        ProductResponseDto updatedProduct = productService.updateProduct(productId, updateRequest);
        return new ApiResponse<>("Success", "Product updated successfully", updatedProduct);
    }

    /**
     * Update a specific product size
     */
    @PutMapping("/{productId}/sizes/{sizeId}")
    public ApiResponse<ProductResponseDto> updateProductSize(
            @PathVariable Long productId,
            @PathVariable Long sizeId,
            @Valid @RequestBody ProductSizeRequestDto sizeRequest
    ) {
        log.info("Received request to update size with ID: {} for product with ID: {}", sizeId, productId);
        ProductResponseDto updatedProduct = productService.updateProductSize(productId, sizeId, sizeRequest);
        return new ApiResponse<>("Success", "Product size updated successfully", updatedProduct);
    }

    /**
     * Delete a product
     */
    @DeleteMapping("/{productId}")
    public ApiResponse<ProductResponseDto> deleteProduct(
            @PathVariable Long productId
    ) {
        log.info("Received request to delete product with ID: {}", productId);
        ProductResponseDto deletedProduct = productService.deleteProduct(productId);
        return new ApiResponse<>("Success", "Product deleted successfully", deletedProduct);
    }

    /**
     * Reset discount for a product
     */
    @PostMapping("/{productId}/reset-discount")
    public ApiResponse<ProductResponseDto> resetDiscountForProduct(
            @PathVariable Long productId
    ) {
        log.info("Received request to reset discount for product with ID: {}", productId);
        ProductResponseDto updatedProduct = productService.resetDiscountForProduct(productId);
        return new ApiResponse<>("Success", "Product discount reset successfully", updatedProduct);
    }

    /**
     * Delete a specific size from a product
     */
    @DeleteMapping("/{productId}/sizes/{sizeId}")
    public ApiResponse<ProductResponseDto> deleteProductSize(
            @PathVariable Long productId,
            @PathVariable Long sizeId
    ) {
        log.info("Received request to delete size ID: {} from product ID: {}", sizeId, productId);
        ProductResponseDto updatedProduct = productService.deleteProductSize(productId, sizeId);
        return new ApiResponse<>("Success", "Size deleted successfully", updatedProduct);
    }
}