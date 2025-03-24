package com.menghor.smart_shop.feature.customer.controller;

import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;
import com.menghor.smart_shop.feature.customer.service.CategoryService;
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
     * Create a new category
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto createRequest) {
        log.info("Received request to create category");

        return new ApiResponse<>("Success", "Category created successfully", categoryService.createCategory(createRequest));
    }

    /**
     * Get category by id
     */
    @GetMapping("/{categoryId}")
    public ApiResponse<CategoryResponseDto> getCategoryById(@PathVariable Long categoryId) {
        log.info("Received request to get category by Id");

        return new ApiResponse<>("Success", "Category by ID response successfully", categoryService.getCategoryById(categoryId));
    }

    /**
     * Get category by shop
     */
    @GetMapping("/shop")
    public ApiResponse<List<CategoryResponseDto>> getCategoriesByShop() {
        log.info("Received request to get category by shop");

        final List<CategoryResponseDto> categoriesByShop = categoryService.getCategoriesByShop();
        return new ApiResponse<>("Success", "Category by shop response successfully", categoriesByShop);
    }

    /**
     * update category by id
     */
    @PutMapping("/{categoryId}")
    public ApiResponse<CategoryResponseDto> updateCategory(@PathVariable Long categoryId,
                                                           @RequestBody CategoryRequestDto requestDto) {

        log.info("Received request to update category by id");
        final CategoryResponseDto categoryResponseDto = categoryService.updateCategory(categoryId, requestDto);
        return new ApiResponse<>("Success", "Category updated by id successfully", categoryResponseDto);
    }

    /**
     * delete category by id
     */
    @DeleteMapping("/{categoryId}")
    public ApiResponse<CategoryResponseDto> deleteCategory(@PathVariable Long categoryId) {
        final CategoryResponseDto categoryResponseDto = categoryService.deleteCategory(categoryId);
        return new ApiResponse<>("Success", "Category deleted by id successfully", categoryResponseDto);
    }
}
