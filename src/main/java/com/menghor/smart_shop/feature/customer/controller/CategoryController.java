package com.menghor.smart_shop.feature.customer.controller;

import com.menghor.smart_shop.exceptions.response.ApiResponse;
import com.menghor.smart_shop.feature.customer.dto.request.CategoryFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;
import com.menghor.smart_shop.feature.customer.service.CategoryService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Create a new category with optional image
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto createRequest) {
        log.info("Received request to create category");
        return new ApiResponse<>("Success", "Category created successfully",
                categoryService.createCategory(createRequest));
    }

    /**
     * Get category by id
     */
    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponseDto> getCategoryById(@PathVariable Long categoryId) {
        log.info("Received request to get category by ID: {}", categoryId);
        return new ApiResponse<>("Success", "Category retrieved successfully",
                categoryService.getCategoryById(categoryId));
    }

    /**
     * Get all categories for the current shop
     */
    @GetMapping("/shop")
    public ApiResponse<List<CategoryResponseDto>> getCategoriesByShop() {
        log.info("Received request to get categories for current shop");
        final List<CategoryResponseDto> categoriesByShop = categoryService.getCategoriesByShop();
        return new ApiResponse<>("Success", "Categories retrieved successfully", categoriesByShop);
    }

    /**
     * Get categories for the current shop with pagination and filtering
     */
    @PostMapping("/shop/all")
    public ApiResponse<CustomPaginationResponseDto<CategoryResponseDto>> getCategoriesByShopWithFilters(
            @RequestBody CategoryFilterDto filterDto) {
        log.info("Received request to get categories with filters");
        final CustomPaginationResponseDto<CategoryResponseDto> categories =
                categoryService.getCategoriesByShopWithFilter(filterDto);
        return new ApiResponse<>("Success", "Categories retrieved successfully", categories);
    }

    /**
     * Get categories by shop id (public access)
     */
    @GetMapping("/shop/{shopId}")
    public ApiResponse<List<CategoryResponseDto>> getCategoriesByShopId(@PathVariable Long shopId) {
        log.info("Received request to get categories for shop ID: {}", shopId);
        return new ApiResponse<>("Success", "Categories retrieved successfully",
                categoryService.getCategoriesByShopId(shopId));
    }

    /**
     * Update category by id
     */
    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponseDto> updateCategory(@PathVariable Long categoryId,
                                                           @Valid @RequestBody CategoryRequestDto requestDto) {
        log.info("Received request to update category with ID: {}", categoryId);
        final CategoryResponseDto categoryResponseDto = categoryService.updateCategory(categoryId, requestDto);
        return new ApiResponse<>("Success", "Category updated successfully", categoryResponseDto);
    }

    /**
     * Delete category by id
     */
    @DeleteMapping("/{categoryId}")
    public ApiResponse<CategoryResponseDto> deleteCategory(@PathVariable Long categoryId) {
        log.info("Received request to delete category with ID: {}", categoryId);
        final CategoryResponseDto categoryResponseDto = categoryService.deleteCategory(categoryId);
        return new ApiResponse<>("Success", "Category deleted successfully", categoryResponseDto);
    }

    /**
     * Toggle category status (active/inactive)
     */
    @PatchMapping("/{categoryId}/toggle-status")
    public ApiResponse<CategoryResponseDto> toggleCategoryStatus(@PathVariable Long categoryId) {
        log.info("Received request to toggle status of category with ID: {}", categoryId);
        final CategoryResponseDto categoryResponseDto = categoryService.changeCategoryStatus(categoryId);
        return new ApiResponse<>("Success", "Category status updated successfully", categoryResponseDto);
    }
}