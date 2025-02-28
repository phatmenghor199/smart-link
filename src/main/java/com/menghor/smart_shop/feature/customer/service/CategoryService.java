package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto requestDto);
    List<CategoryResponseDto> getCategoriesByShop();
    CategoryResponseDto getCategoryById(Long categoryId);

    CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto);
    CategoryResponseDto deleteCategory(Long categoryId);
}
