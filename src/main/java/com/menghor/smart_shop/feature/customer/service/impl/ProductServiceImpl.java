package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.ProductMapper;
import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.repository.CategoryRepository;
import com.menghor.smart_shop.feature.customer.repository.ProductRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.feature.customer.service.ProductService;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final SecurityUtils securityUtils;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Creating all product for shopId: {} by userId: {}", shopId, userId);
        CategoryEntity category = getUserOwnedCategory(requestDto.getCategoryId(), shopId);

        // Check if discountEndDate is after discountStartDate
        if (requestDto.getDiscountEndDate().isBefore(requestDto.getDiscountStartDate())) {
            throw new IllegalArgumentException("Discount end date cannot be before start date.");
        }

        ProductEntity product = productMapper.toEntity(requestDto);
        // Validate the discount before saving
        product.validateDiscount();
        product.setShop(shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.error("Shop with id {} not found", shopId);
                    return new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, shopId));
                }));

        product.setCategory(category);
        final ProductEntity productEntity = productRepository.save(product);
        log.info("Product created successfully in shop {}", shopId);
        return productMapper.toDto(productEntity);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));
        return productMapper.toDto(product);
    }

    @Override
    public List<ProductResponseDto> getProductsByShop() {
        Long shopId = securityUtils.getShopIdFromToken();

        List<ProductEntity> products = productRepository.findByShopId(shopId);

        return products.stream().map(productMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));

        // Check if discountEndDate is after discountStartDate
        if (requestDto.getDiscountEndDate().isBefore(requestDto.getDiscountStartDate())) {
            throw new IllegalArgumentException("Discount end date cannot be before start date.");
        }

        productMapper.updateProductFromDto(requestDto, product);
        // Validate the discount before saving the updated product
        product.validateDiscount();

        final ProductEntity productEntity = productRepository.save(product);

        return productMapper.toDto(productEntity);
    }

    @Override
    public ProductResponseDto deleteProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));
        productRepository.delete(product);
        return productMapper.toDto(product);
    }

    @Override
    public void resetExpiredDiscounts() {
        log.info("Resetting expired discounts for all products");
        LocalDate now = LocalDate.now();
        List<ProductEntity> productsWithExpiredDiscounts = productRepository.findByDiscountEndDateBefore(now);

        for (ProductEntity product : productsWithExpiredDiscounts) {
            product.resetDiscount(); // Reset the discount fields
            productRepository.save(product); // Save the updated product
        }
    }

    @Override
    public ProductResponseDto resetDiscountForProduct(Long productId) {
        // Find the product by ID
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Check if the discount has expired
        if (product.getDiscountEndDate() != null) {
            // Convert LocalDate to LocalDateTime (if you want time to be ignored, you can compare only by the date)
            LocalDateTime discountEndDateTime = product.getDiscountEndDate().atStartOfDay();  // set time to 00:00:00

            // If the current date and time are after the discount end date
            if (LocalDateTime.now().isAfter(discountEndDateTime)) {
                // Reset discount fields
                product.setDiscountType(null);
                product.setDiscountValue(null);
                product.setDiscountStartDate(null);
                product.setDiscountEndDate(null);

                // Save the updated product entity
                productRepository.save(product);

                // Return a response DTO with the updated product
                return productMapper.toDto(product); // Assuming you have a method to convert ProductEntity to ProductResponseDto
            }
        }

        // If the discount was not expired, just return the product data as is
        return productMapper.toDto(product);
    }



    /**
     * Helper method to validate if the user owns the categories.
     *
     * @param categoryId The user ID
     * @param shopId The shop ID
     * @return ShopEntity if the user owns the shop
     */
    private CategoryEntity  getUserOwnedCategory(Long categoryId, Long shopId) {
        return categoryRepository.findByIdAndShopId(categoryId, shopId)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found in shop {}", categoryId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId));
                });
    }
}
