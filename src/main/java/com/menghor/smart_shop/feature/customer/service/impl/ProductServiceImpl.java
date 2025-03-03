package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.ProductSizeRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.ProductMapper;
import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.customer.repository.CategoryRepository;
import com.menghor.smart_shop.feature.customer.repository.ProductRepository;
import com.menghor.smart_shop.feature.customer.repository.ProductSizeRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.feature.customer.service.ProductService;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
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
    private  final ProductSizeRepository productSizeRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Creating all product for shopId: {} by userId: {}", shopId, userId);
        CategoryEntity category = getUserOwnedCategory(requestDto.getCategoryId(), shopId);

        ProductEntity product = productMapper.toEntity(requestDto);

        product.setShop(shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.error("Shop with id {} not found", shopId);
                    return new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, shopId));
                }));

        product.setCategory(category);

        ProductEntity productEntity = productRepository.saveAndFlush(product);

        product.getSizes().clear();

        List<ProductSizeEntity> sizes = requestDto.getSizes().stream()
                .map(sizeRequestDto -> {
                    ProductSizeEntity size = productMapper.toSizeEntity(sizeRequestDto);
                    size.setProduct(productEntity); // Ensure the relationship is set
                    size.validateDiscount(); // Validate the discount
                    productSizeRepository.save(size); // Save each size
                    return size;
                })
                .toList();

        // Add the saved sizes to the product
        product.getSizes().addAll(sizes);

        // Save the product again to persist the changes with sizes
        productRepository.save(product);

        log.info("Product created successfully in shop {}", shopId);
        return productMapper.toDto(productEntity);
    }

    @Override
    public ProductResponseDto addSizesToProduct(Long productId, List<ProductSizeRequestDto> sizeRequest) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Save product sizes
        List<ProductSizeEntity> sizes = sizeRequest.stream()
                .map(sizeRequestDto -> {
                    ProductSizeEntity size = productMapper.toSizeEntity(sizeRequestDto);
                    size.setProduct(product); // Ensure the relationship is set
                    size.validateDiscount();
                    return productSizeRepository.save(size);
                })
                .toList();

        product.getSizes().addAll(sizes);
        productRepository.save(product);
        log.info("Sizes added successfully to product {}", productId);
        return productMapper.toDto(product);
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

        // Update product fields
        productMapper.updateProductFromDto(requestDto, product);

        final ProductEntity productEntity = productRepository.save(product);

        List<ProductSizeRequestDto> sizeRequestDtos = requestDto.getSizes() != null ? requestDto.getSizes() : Collections.emptyList();

        // Update product sizes
        for (ProductSizeRequestDto sizeRequestDto : sizeRequestDtos) {
            ProductSizeEntity size = product.getSizes().stream()
                    .filter(s -> s.getId().equals(sizeRequestDto.getId()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_SIZE_NOT_FOUND, sizeRequestDto.getId())));

            productMapper.updateSizeFromDto(sizeRequestDto, size);
            size.validateDiscount();
            size.setProduct(product); // Ensure the relationship is set
            productSizeRepository.save(size);
        }

        return productMapper.toDto(productEntity);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto) {
        // Retrieve the product entity by its ID
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Retrieve the product size by its ID
        ProductSizeEntity size = productSizeRepository.findById(sizeId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_SIZE_NOT_FOUND, sizeId)));

        // Ensure that the product size belongs to the specified product
        if (!size.getProduct().getId().equals(productId)) {
            throw new NotFoundException(String.format(ErrorMessages.PRODUCT_SIZE_NOT_ASSOCIATED_WITH_PRODUCT, sizeId, productId));
        }

        // Update the product size properties using the sizeRequestDto
        productMapper.updateSizeFromDto(sizeRequestDto, size);

        // Validate the discount for the updated product size
        size.validateDiscount();

        // Save the updated product size back to the repository
        productSizeRepository.save(size);

        // Return the updated product DTO
        return productMapper.toDto(product);
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

        // Step 1: Reset expired discounts for sizes
        List<ProductSizeEntity> expiredSizes = productSizeRepository.findByDiscountEndDateBefore(now);

        for (ProductSizeEntity size : expiredSizes) {
            // Reset the discount fields for the expired product size
            size.resetDiscount();

            // Save the updated size
            productSizeRepository.save(size);
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

    private CategoryEntity  getUserOwnedCategory(Long categoryId, Long shopId) {
        return categoryRepository.findByIdAndShopId(categoryId, shopId)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found in shop {}", categoryId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId));
                });
    }
}
