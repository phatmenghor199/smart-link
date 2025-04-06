package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.CategoryFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

import java.util.List;

public interface CategoryService {
    // Create a new category with embedded image
    CategoryResponseDto createCategory(CategoryRequestDto requestDto);

    // Get all categories by shop
    List<CategoryResponseDto> getCategoriesByShop();

    // Get a category by ID
    CategoryResponseDto getCategoryById(Long categoryId);

    // Update a category
    CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto);

    // Delete a category
    CategoryResponseDto deleteCategory(Long categoryId);

    // Get categories with pagination and filtering
    CustomPaginationResponseDto<CategoryResponseDto> getCategoriesByShopWithFilter(CategoryFilterDto filterDto);

    // Get categories by shop ID (for public access)
    List<CategoryResponseDto> getCategoriesByShopId(Long shopId);

    // Change category status (active/inactive)
    CategoryResponseDto changeCategoryStatus(Long categoryId);
}