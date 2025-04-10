package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.exceptions.error.BadRequestException;
import com.menghor.smart_shop.exceptions.error.DuplicateNameException;
import com.menghor.smart_shop.exceptions.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.CategoryFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.CategoryMapper;
import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.CategoryRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.feature.customer.service.CategoryService;
import com.menghor.smart_shop.feature.customer.specification.CategorySpecification;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import com.menghor.smart_shop.utils.pagiantion.PaginationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Creating category for shopId: {} by userId: {}", shopId, userId);
        ShopEntity shop = getUserOwnedShop(userId, shopId);

        // Check if category name already exists for this shop
        if (categoryRepository.existsByNameAndShopId(requestDto.getName(), shopId)) {
            throw new DuplicateNameException("A category with this name already exists in your shop");
        }

        CategoryEntity categoryEntity = categoryMapper.toEntity(requestDto);
        categoryEntity.setShop(shop);

        // Set status if provided, otherwise use default (ACTIVE)
        if (requestDto.getStatus() != null) {
            categoryEntity.setStatus(requestDto.getStatus());
        } else {
            categoryEntity.setStatus(StatusData.ACTIVE);
        }

        // If image data is provided in the request, set reference type
        if (categoryEntity.getImage() != null) {
            categoryEntity.getImage().setReferenceType("category");
            log.info("Setting referenceType to 'category' for new image");
            // Entity ID will be set after saving
        }

        final CategoryEntity savedCategory = categoryRepository.save(categoryEntity);

        // Log the saved image details for debugging
        if (savedCategory.getImage() != null) {
            log.info("Saved image with ID: {}, referenceType: {}",
                    savedCategory.getImage().getId(),
                    savedCategory.getImage().getReferenceType());
        }

        log.info("Category created successfully with ID: {}", savedCategory.getId());

        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByShop() {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Getting all categories for shopId: {} by userId: {}", shopId, userId);
        getUserOwnedShop(userId, shopId);

        List<CategoryEntity> categories = categoryRepository.findByShopId(shopId);
        log.info("Found {} categories", categories.size());

        return categories.stream().map(categoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long categoryId) {
        log.info("Getting category by ID: {}", categoryId);
        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId)));

        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long categoryId, CategoryRequestDto requestDto) {
        Long userId = securityUtils.getUserIdFromToken();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId)));

        ShopEntity shop = getUserOwnedShop(userId, category.getShop().getId());

        log.info("Updating category ID: {} for shop ID: {}", categoryId, category.getShop().getId());

        // Check if name is changing and if it already exists
        if (requestDto.getName() != null && !requestDto.getName().equals(category.getName()) &&
                categoryRepository.existsByNameAndShopId(requestDto.getName(), shop.getId())) {
            throw new DuplicateNameException("A category with this name already exists in your shop");
        }

        // Log current image state for debugging
        if (category.getImage() != null) {
            log.info("Before update: Image ID: {}, referenceType: {}",
                    category.getImage().getId(),
                    category.getImage().getReferenceType());
        } else {
            log.info("Before update: Category has no image");
        }

        // Handle image update
        if (requestDto.getImage() != null) {
            if (category.getImage() == null) {
                // Create new image if none exists
                ImageEntity newImage = new ImageEntity();
                newImage.setImageType(requestDto.getImage().getImageType());
                newImage.setBase64Image(requestDto.getImage().getBase64Image());
                newImage.setReferenceType("category");
                category.setImage(newImage);
                log.info("Created new image with referenceType: category");
            } else {
                // Update existing image instead of replacing it
                category.getImage().setImageType(requestDto.getImage().getImageType());
                category.getImage().setBase64Image(requestDto.getImage().getBase64Image());
                category.getImage().setReferenceType("category"); // Explicitly set referenceType again
                log.info("Updated existing image and set referenceType: category");
            }
        }

        // Update other fields
        categoryMapper.updateCategoryFromDto(requestDto, category);

        // IMPORTANT FIX: Re-set the referenceType AFTER the mapper update
        if (category.getImage() != null) {
            category.getImage().setReferenceType("category");
            log.info("Re-applying referenceType 'category' after mapper update");
        }

        // Update status if provided
        if (requestDto.getStatus() != null) {
            category.setStatus(requestDto.getStatus());
        }

        CategoryEntity updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", updatedCategory.getId());

        // Additional verification log after save
        if (updatedCategory.getImage() != null) {
            log.info("Verified after save: Image ID: {}, referenceType: {}",
                    updatedCategory.getImage().getId(),
                    updatedCategory.getImage().getReferenceType());
        }

        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto deleteCategory(Long categoryId) {
        Long userId = securityUtils.getUserIdFromToken();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId)));

        getUserOwnedShop(userId, category.getShop().getId());

        // Check if category has products
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new BadRequestException("Cannot delete category with products. Remove all products first.");
        }

        log.info("Deleting category ID: {} for shop ID: {}", categoryId, category.getShop().getId());

        // The associated image will be automatically deleted due to orphanRemoval=true in the relationship
        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", categoryId);

        return categoryMapper.toDto(category);
    }

    @Override
    public CustomPaginationResponseDto<CategoryResponseDto> getCategoriesByShopWithFilter(CategoryFilterDto filterDto) {
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Filtering categories for shop ID: {} with criteria: {}", shopId, filterDto);

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterDto.getPageNo() != null ? filterDto.getPageNo() : 1,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10);

        // Create specification using filter criteria
        Specification<CategoryEntity> spec = CategorySpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                shopId
        );

        // Create pageable object with sorting by creation date in descending order
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1,
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute the query with specification, pagination, and sorting
        Page<CategoryEntity> categoryPage = categoryRepository.findAll(spec, pageable);
        log.info("Found {} categories matching the criteria", categoryPage.getTotalElements());

        // Map entities to DTOs
        List<CategoryResponseDto> categoryDtos = categoryPage.getContent().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        return categoryMapper.toPaginationDto(categoryDtos, categoryPage);
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByShopId(Long shopId) {
        log.info("Getting categories for shop ID: {}", shopId);

        // Verify shop exists
        shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, shopId)));

        // Only fetch active categories for public access
        List<CategoryEntity> categories = categoryRepository.findByShopIdAndStatus(shopId, StatusData.ACTIVE,
                PageRequest.of(0, 1000, Sort.by(Sort.Direction.ASC, "name"))).getContent();

        log.info("Found {} active categories for shop ID: {}", categories.size(), shopId);

        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryResponseDto changeCategoryStatus(Long categoryId) {
        Long userId = securityUtils.getUserIdFromToken();

        CategoryEntity category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId)));

        getUserOwnedShop(userId, category.getShop().getId());

        // Toggle status
        if (category.getStatus() == StatusData.ACTIVE) {
            category.setStatus(StatusData.INACTIVE);
        } else {
            category.setStatus(StatusData.ACTIVE);
        }

        categoryRepository.save(category);
        log.info("Category status updated to {} for ID: {}", category.getStatus(), categoryId);

        return categoryMapper.toDto(category);
    }

    /**
     * Helper method to validate if the user owns the shop.
     */
    private ShopEntity getUserOwnedShop(Long userId, Long shopId) {
        return shopRepository.findByIdAndUserId(shopId, userId)
                .orElseThrow(() -> {
                    log.error("User {} does not own shop {}", userId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.USER_DOES_NOT_OWN_SHOP, userId, shopId));
                });
    }
}