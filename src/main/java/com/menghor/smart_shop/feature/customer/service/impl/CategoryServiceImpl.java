package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.CategoryMapper;
import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.CategoryRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.feature.customer.service.CategoryService;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final SecurityUtils securityUtils;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {

        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Creating category for shopId: {} by userId: {}", shopId, userId);
        ShopEntity shop = getUserOwnedShop(userId, shopId);

        CategoryEntity categoryEntity = categoryMapper.toEntity(requestDto);
        categoryEntity.setShop(shop);

        final CategoryEntity saveCategory = categoryRepository.save(categoryEntity);

        return categoryMapper.toDto(saveCategory);

    }

    @Override
    public List<CategoryResponseDto> getCategoriesByShop() {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Getting all category for shopId: {} by userId: {}", shopId, userId);
        getUserOwnedShop(userId, shopId);

        List<CategoryEntity> categories = categoryRepository.findByShopId(shopId);

        return categories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long categoryId) {

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category by id could not be found"));

        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto) {
        Long userId = securityUtils.getUserIdFromToken();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId)));
        getUserOwnedShop(userId, category.getShop().getId());

        log.info("Updating category ID: {} for shop ID: {}", categoryId, category.getShop().getId());

        categoryMapper.updateCategoryFromDto(requestDto, category);
        CategoryEntity categoryEntity = categoryRepository.save(category);

        log.info("Category updated successfully with ID: {}", categoryEntity.getId());

        return categoryMapper.toDto(categoryEntity);
    }

    @Override
    public CategoryResponseDto deleteCategory(Long categoryId) {
        Long userId = securityUtils.getUserIdFromToken();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId)));

        getUserOwnedShop(userId, category.getShop().getId());
        log.info("Deleting category ID: {} for shop ID: {}", categoryId, category.getShop().getId());
        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", categoryId);
        return categoryMapper.toDto(category);
    }

    /**
     * Helper method to validate if the user owns the shop.
     *
     * @param userId The user ID
     * @param shopId The shop ID
     * @return ShopEntity if the user owns the shop
     */
    private ShopEntity getUserOwnedShop(Long userId, Long shopId) {
        return shopRepository.findByIdAndUserId(shopId, userId)
                .orElseThrow(() -> {
                    log.error("User {} does not own shop {}", userId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.USER_DOES_NOT_OWN_SHOP, userId, shopId));
                });
    }
}
