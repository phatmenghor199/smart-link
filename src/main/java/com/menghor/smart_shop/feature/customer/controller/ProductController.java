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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ApiResponse<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto createRequest) {
        log.info("Received request to create product");
        return new ApiResponse<>("Success", "Product created successfully", productService.createProduct(createRequest));
    }

    /**
     * Add sizes to a product
     */
    @PostMapping("/{productId}/sizes")
    public ApiResponse<ProductResponseDto> addSizesToProduct(
            @PathVariable Long productId,
            @RequestBody List<ProductSizeRequestDto> sizeRequestDtos) {
        log.info("Adding sizes to product with ID: {}", productId);
        ProductResponseDto productResponse = productService.addSizesToProduct(productId, sizeRequestDtos);
        return new ApiResponse<>("Success", "Sizes added successfully", productResponse);
    }

    /**
     * Get product by id
     */
    @GetMapping("/{productId}")
    public ApiResponse<ProductResponseDto> getProductById(@PathVariable Long productId) {
        log.info("Getting product by ID: {}", productId);
        return new ApiResponse<>("Success", "Product retrieved successfully", productService.getProductById(productId));
    }

    /**
     * Get all products by shop with pagination and filtering
     */
    @PostMapping("/shop/all")
    public ApiResponse<CustomPaginationResponseDto<ProductResponseDto>> getProductsByShop(
            @RequestBody ProductFilterDto filterDto) {
        log.info("Getting products for shop with filter: {}", filterDto);
        final CustomPaginationResponseDto<ProductResponseDto> products =
                productService.getProductsByShopWithFilter(filterDto);
        return new ApiResponse<>("Success", "Products retrieved successfully", products);
    }

    /**
     * Get all products by shop (simple list)
     */
    @GetMapping("/shop")
    public ApiResponse<List<ProductResponseDto>> getSimpleProductsByShop() {
        log.info("Getting simple list of products for current shop");
        final List<ProductResponseDto> products = productService.getProductsByShop();
        return new ApiResponse<>("Success", "Products retrieved successfully", products);
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ProductResponseDto>> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("Getting products for category ID: {}", categoryId);
        final List<ProductResponseDto> products = productService.getProductsByCategory(categoryId);
        return new ApiResponse<>("Success", "Products retrieved successfully", products);
    }

    /**
     * Get products by category with pagination and filtering
     */
    @PostMapping("/category/{categoryId}/all")
    public ApiResponse<CustomPaginationResponseDto<ProductResponseDto>> getProductsByCategoryWithFilter(
            @PathVariable Long categoryId,
            @RequestBody ProductFilterDto filterDto) {
        log.info("Getting products for category ID: {} with filters", categoryId);
        final CustomPaginationResponseDto<ProductResponseDto> products =
                productService.getProductsByCategoryWithFilter(categoryId, filterDto);
        return new ApiResponse<>("Success", "Products retrieved successfully", products);
    }

    /**
     * Get all products (admin)
     */
    @GetMapping()
    public ApiResponse<List<ProductResponseDto>> getAllProducts() {
        log.info("Getting all products");
        final List<ProductResponseDto> products = productService.getAllProducts();
        return new ApiResponse<>("Success", "All products retrieved successfully", products);
    }

    /**
     * Get all products with pagination and filtering (admin)
     */
    @PostMapping("/all")
    public ApiResponse<CustomPaginationResponseDto<ProductResponseDto>> getAllProductsWithFilter(
            @RequestBody ProductFilterDto filterDto) {
        log.info("Getting all products with filters");
        final CustomPaginationResponseDto<ProductResponseDto> products =
                productService.getAllProductsWithFilter(filterDto);
        return new ApiResponse<>("Success", "All products retrieved successfully", products);
    }

    /**
     * Update product
     */
    @PutMapping("/{productId}")
    public ApiResponse<ProductResponseDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequestDto requestDto) {
        log.info("Updating product with ID: {}", productId);
        final ProductResponseDto product = productService.updateProduct(productId, requestDto);
        return new ApiResponse<>("Success", "Product updated successfully", product);
    }

    /**
     * Update product size
     */
    @PutMapping("/{productId}/sizes/{sizeId}")
    public ApiResponse<ProductResponseDto> updateProductSize(
            @PathVariable Long productId,
            @PathVariable Long sizeId,
            @RequestBody ProductSizeRequestDto sizeRequestDto) {
        log.info("Updating size with ID: {} for product with ID: {}", sizeId, productId);
        ProductResponseDto product = productService.updateProductSize(productId, sizeId, sizeRequestDto);
        return new ApiResponse<>("Success", "Product size updated successfully", product);
    }

    /**
     * Toggle product status
     */
    @PatchMapping("/{productId}/toggle-status")
    public ApiResponse<ProductResponseDto> toggleProductStatus(@PathVariable Long productId) {
        log.info("Toggling status for product with ID: {}", productId);
        final ProductResponseDto product = productService.toggleProductStatus(productId);
        return new ApiResponse<>("Success", "Product status toggled successfully", product);
    }

    /**
     * Toggle product size status
     */
    @PatchMapping("/{productId}/sizes/{sizeId}/toggle-status")
    public ApiResponse<ProductResponseDto> toggleProductSizeStatus(
            @PathVariable Long productId,
            @PathVariable Long sizeId) {
        log.info("Toggling status for size with ID: {} of product with ID: {}", sizeId, productId);
        final ProductResponseDto product = productService.toggleProductSizeStatus(productId, sizeId);
        return new ApiResponse<>("Success", "Product size status toggled successfully", product);
    }

    /**
     * Delete product
     */
    @DeleteMapping("/{productId}")
    public ApiResponse<ProductResponseDto> deleteProduct(@PathVariable Long productId) {
        log.info("Deleting product with ID: {}", productId);
        final ProductResponseDto product = productService.deleteProduct(productId);
        return new ApiResponse<>("Success", "Product deleted successfully", product);
    }

    /**
     * Reset expired discounts for all products
     */
    @PostMapping("/reset-expired-discounts")
    public ApiResponse<String> resetExpiredDiscounts() {
        log.info("Resetting expired discounts for all products");
        productService.resetExpiredDiscounts();
        return new ApiResponse<>("Success", "Expired discounts have been reset", "");
    }

    /**
     * Reset expired discount for a product by ID
     */
    @PostMapping("/{productId}/reset-expired-discount")
    public ApiResponse<ProductResponseDto> resetDiscountExpiredForProduct(@PathVariable Long productId) {
        log.info("Resetting expired discount for product with ID: {}", productId);
        ProductResponseDto product = productService.resetExpiredDiscountForProduct(productId);
        return new ApiResponse<>("Success", "Product discount has been reset", product);
    }

    /**
     * Reset discount for a product by ID
     */
    @PostMapping("/{productId}/reset-discount")
    public ApiResponse<ProductResponseDto> resetDiscountForProduct(@PathVariable Long productId) {
        log.info("Resetting discount for product with ID: {}", productId);
        ProductResponseDto product = productService.resetDiscountForProduct(productId);
        return new ApiResponse<>("Success", "Product discount has been reset", product);
    }

    /**
     * Get all products with active promotions
     */
    @GetMapping("/promotions")
    public ApiResponse<List<ProductResponseDto>> getAllProductsPromotions() {
        log.info("Getting all products with active promotions");
        List<ProductResponseDto> products = productService.getProductsWithActivePromotions();
        return new ApiResponse<>("Success", "All products with active promotions retrieved", products);
    }

    /**
     * Get all products with active promotions (with pagination)
     */
    @PostMapping("/promotions/all")
    public ApiResponse<CustomPaginationResponseDto<ProductResponseDto>> getAllProductsPromotionsWithFilter(
            @RequestBody ProductFilterDto filterDto) {
        log.info("Getting all products with active promotions with filters");
        CustomPaginationResponseDto<ProductResponseDto> products =
                productService.getProductsWithActivePromotionsWithFilter(filterDto);
        return new ApiResponse<>("Success", "All products with active promotions retrieved", products);
    }

    /**
     * Get all products by shop with active promotions
     */
    @GetMapping("/shop/promotions")
    public ApiResponse<List<ProductResponseDto>> getProductsWithActivePromotionsByShop() {
        log.info("Getting products with active promotions for current shop");
        List<ProductResponseDto> products = productService.getProductsWithActivePromotionsByShop();
        return new ApiResponse<>("Success", "Products with active promotions retrieved", products);
    }

    /**
     * Get all products by shop with active promotions (with pagination)
     */
    @PostMapping("/shop/promotions/all")
    public ApiResponse<CustomPaginationResponseDto<ProductResponseDto>> getProductsWithActivePromotionsByShopWithFilter(
            @RequestBody ProductFilterDto filterDto) {
        log.info("Getting products with active promotions for current shop with filters");
        CustomPaginationResponseDto<ProductResponseDto> products =
                productService.getProductsWithActivePromotionsByShopWithFilter(filterDto);
        return new ApiResponse<>("Success", "Products with active promotions retrieved", products);
    }
}