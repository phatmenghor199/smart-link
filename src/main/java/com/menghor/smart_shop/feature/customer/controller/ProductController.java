package com.menghor.smart_shop.feature.customer.controller;

import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    /**
     * Create a new category
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto createRequest) {

        log.info("Received request to create product");
        return new ApiResponse<>("Success", "Product created successfully", productService.createProduct(createRequest));
    }

    /**
     * Get product by id
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProductById(@PathVariable Long productId) {
        log.info("Received request to get product by Id");

        return new ApiResponse<>("Success", "Product by ID response successfully", productService.getProductById(productId));
    }

    /**
     * Get product by shop
     */
    @GetMapping("/shop")
    public ApiResponse<List<ProductResponseDto>> getProductByShop() {
        log.info("Received request to get product by shop");
        final List<ProductResponseDto> productsByShop = productService.getProductsByShop();
        return new ApiResponse<>("Success", "Product by shop response successfully", productsByShop);
    }

    /**
     * update product by id
     */
    @PutMapping("/{productId}")
    public ApiResponse<ProductResponseDto> updateCategory(@PathVariable Long productId,
                                                          @RequestBody ProductRequestDto requestDto) {

        log.info("Received request to update category by id");
        final ProductResponseDto productResponseDto = productService.updateProduct(productId, requestDto);
        return new ApiResponse<>("Success", "Product updated by id successfully", productResponseDto);
    }

    /**
     * delete product by id
     */
    @DeleteMapping("/{productId}")
    public ApiResponse<ProductResponseDto> deleteProduct(@PathVariable Long productId) {
        final ProductResponseDto productResponseDto = productService.deleteProduct(productId);
        return new ApiResponse<>("Success", "Product deleted by id successfully", productResponseDto);
    }

    /**
     * reset expired discounts for all products
     */
    @PostMapping("/reset-expired-discounts")
    public ApiResponse<String> resetExpiredDiscounts() {
        log.info("Received request to reset expired discounts for all products");
        productService.resetExpiredDiscounts();
        return new ApiResponse<>("Success", "Expired discounts have been reset.", "");
    }

    // Endpoint to reset the discount for a product by ID
    @PostMapping("/{productId}/reset-discount")
    public ApiResponse<ProductResponseDto> resetDiscountForProduct(@PathVariable Long productId) {

        log.info("Received request to reset discount for product with ID: {}", productId);

        ProductResponseDto updatedProduct = productService.resetDiscountForProduct(productId);
        return new ApiResponse<>("Success", "Product discount has been reset.", updatedProduct);
    }
}
