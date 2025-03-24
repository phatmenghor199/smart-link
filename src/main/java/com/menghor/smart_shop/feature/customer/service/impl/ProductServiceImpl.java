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
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final SecurityUtils securityUtils;
    private final ShopRepository shopRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
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

        product.getSizes().addAll(sizes);
        // Clear product promotion details if it has sizes
        if (!product.getSizes().isEmpty()) {
            log.info("Clearing promotion details for product with sizes");
            product.resetDiscount();
        }

        // Save the product again to persist the changes with sizes
        productRepository.save(product);

        log.info("Product created successfully in shop {}", shopId);
        return productMapper.toDto(productEntity);
    }

    @Override
    public ProductResponseDto addSizesToProduct(Long productId, List<ProductSizeRequestDto> sizeRequest) {
        log.info("Adding sizes to product with ID {}", productId);
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

        // Clear product promotion details if it has sizes
        if (!product.getSizes().isEmpty()) {
            log.info("Clearing promotion details for product with sizes");
            product.resetDiscount();
        }

        productRepository.save(product);
        log.info("Sizes added successfully to product {}", productId);
        return productMapper.toDto(product);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        log.info("Getting product by ID {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));
        return productMapper.toDto(product);
    }

    @Override
    public List<ProductResponseDto> getProductsByShop() {
        log.info("Getting products by shop");

        Long shopId = securityUtils.getShopIdFromToken();
        List<ProductEntity> products = productRepository.findByShopId(shopId);
        List<ProductResponseDto> productDtos = products.stream().map(productMapper::toDto).toList();

        // Always prioritize promotion details from sizes
        productDtos.forEach(productDto -> {
            productDto.getSizes().stream()
                    .filter(size -> "ACTIVE".equals(size.getPromotionStatus()))
                    .findFirst()
                    .ifPresent(size -> {
                        productDto.setSize(size.getSize());
                        productDto.setPrice(size.getPrice());
                        productDto.setDiscountType(size.getDiscountType());
                        productDto.setDiscountValue(size.getDiscountValue());
                        productDto.setDiscountStartDate(size.getDiscountStartDate());
                        productDto.setDiscountEndDate(size.getDiscountEndDate());
                        productDto.setFinalPrice(size.getFinalPrice());
                        productDto.setPromotionStatus(size.getPromotionStatus());
                    });
        });
        return productDtos;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        log.info("Getting all products");
        List<ProductEntity> products = productRepository.findAll();
        List<ProductResponseDto> productDtos = products.stream().map(productMapper::toDto).toList();

        // Always prioritize promotion details from sizes
        productDtos.forEach(productDto -> {
            productDto.getSizes().stream()
                    .filter(size -> "ACTIVE".equals(size.getPromotionStatus()))
                    .findFirst()
                    .ifPresent(size -> {
                        productDto.setSize(size.getSize());
                        productDto.setPrice(size.getPrice());
                        productDto.setDiscountType(size.getDiscountType());
                        productDto.setDiscountValue(size.getDiscountValue());
                        productDto.setDiscountStartDate(size.getDiscountStartDate());
                        productDto.setDiscountEndDate(size.getDiscountEndDate());
                        productDto.setFinalPrice(size.getFinalPrice());
                        productDto.setPromotionStatus(size.getPromotionStatus());
                    });
        });

        return productDtos;
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        log.info("Updating product with ID {}", id);
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

        // Clear product promotion details if it has sizes
        if (!product.getSizes().isEmpty()) {
            product.resetDiscount();
        }
        return productMapper.toDto(productEntity);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto) {
        log.info("Updating product size with ID {} for product with ID {}", sizeId, productId);
        // Retrieve the product entity by its ID
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Retrieve the product size by its ID
        ProductSizeEntity size = productSizeRepository.findById(sizeId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_SIZE_NOT_FOUND, sizeId)));

        // Ensure that the product size belongs to the specified product
        if (!size.getProduct().getId().equals(productId)) {
            log.error("Product size with ID {} is not associated with product with ID {}", sizeId, productId);
            throw new NotFoundException(String.format(ErrorMessages.PRODUCT_SIZE_NOT_ASSOCIATED_WITH_PRODUCT, sizeId, productId));
        }

        productMapper.updateSizeFromDto(sizeRequestDto, size);
        size.validateDiscount();
        productSizeRepository.save(size);

        // Clear product promotion details if it has sizes
        if (!product.getSizes().isEmpty()) {
            log.info("Clearing promotion details for product with sizes");
            product.resetDiscount();
        }

        return productMapper.toDto(product);
    }


    @Override
    public ProductResponseDto deleteProduct(Long id) {
        log.info("Deleting product with ID {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));
        productRepository.delete(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
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
            productSizeRepository.save(size);
        }
    }

    @Override
    @Transactional
    public ProductResponseDto resetExpiredDiscountForProduct(Long productId) {
        log.info("Resetting discount for product with ID {}", productId);
        // Find the product by ID
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Check if the discount has expired
        if (product.getDiscountEndDate() != null && LocalDate.now().isAfter(product.getDiscountEndDate())) {
            // Reset discount fields
            product.resetDiscount();
            log.info("Discount reset for product with ID {}", productId);
            // Save the updated product entity
            productRepository.save(product);
            List<ProductSizeEntity> sizes = productSizeRepository.findByProductId(productId);
            for (ProductSizeEntity size : sizes) {
                size.resetDiscount();
                log.info("Discount reset for product size with ID {}", size.getId());
                productSizeRepository.save(size);
            }

            // Return a response DTO with the updated product
            return productMapper.toDto(product);
        }

        // If the discount was not expired, just return the product data as is
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto resetDiscountForProduct(Long productId) {
        log.info("Resetting discount new for product with ID {}", productId);
        // Find the product by ID
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));
        // Reset discount fields
        product.resetDiscount();
        // Save the updated product entity
        productRepository.save(product);

        List<ProductSizeEntity> sizes = productSizeRepository.findByProductId(productId);
        for (ProductSizeEntity size : sizes) {
            size.resetDiscount();
            log.info("Discount reset new for product size with ID {}", size.getId());
            productSizeRepository.save(size);
        }

        log.info("Discount resets for product with ID {}", productId);
        // Return a response DTO with the updated product
        return productMapper.toDto(product);


    }

    @Override
    public List<ProductResponseDto> getProductsWithActivePromotions() {
        log.info("Getting products with active promotions");

        List<ProductEntity> products = productRepository.findAllWithActivePromotions();
        List<ProductResponseDto> productDtos = products.stream().map(productMapper::toDto).toList();

        // Check for promotion on sizes if product does not have an active promotion
        productDtos.forEach(productDto -> {
//            if ("INACTIVE".equals(productDto.getPromotionStatus())) {
            productDto.getSizes().stream()
                    .filter(size -> "ACTIVE".equals(size.getPromotionStatus()))
                    .findFirst()
                    .ifPresent(size -> {
                        productDto.setSize(size.getSize());
                        productDto.setPrice(size.getPrice());
                        productDto.setDiscountType(size.getDiscountType());
                        productDto.setDiscountValue(size.getDiscountValue());
                        productDto.setDiscountStartDate(size.getDiscountStartDate());
                        productDto.setDiscountEndDate(size.getDiscountEndDate());
                        productDto.setFinalPrice(size.getFinalPrice());
                        productDto.setPromotionStatus(size.getPromotionStatus());
                    });
//            }
        });
        return productDtos;
    }

    @Override
    public List<ProductResponseDto> getProductsWithActivePromotionsByShop() {
        log.info("Getting products with active promotions by shop");

        Long shopId = securityUtils.getShopIdFromToken();
        List<ProductEntity> products = productRepository.findAllWithActivePromotionsByShopId(shopId);
        List<ProductResponseDto> productDtos = products.stream().map(productMapper::toDto).toList();

        // Check for promotion on sizes if product does not have an active promotion
        productDtos.forEach(productDto -> {
            productDto.getSizes().stream()
                    .filter(size -> "ACTIVE".equals(size.getPromotionStatus()))
                    .findFirst()
                    .ifPresent(size -> {
                        productDto.setSize(size.getSize());
                        productDto.setPrice(size.getPrice());
                        productDto.setDiscountType(size.getDiscountType());
                        productDto.setDiscountValue(size.getDiscountValue());
                        productDto.setDiscountStartDate(size.getDiscountStartDate());
                        productDto.setDiscountEndDate(size.getDiscountEndDate());
                        productDto.setFinalPrice(size.getFinalPrice());
                        productDto.setPromotionStatus(size.getPromotionStatus());
                    });

        });

        return productDtos;
    }

    private CategoryEntity getUserOwnedCategory(Long categoryId, Long shopId) {
        log.info("Getting category with ID {} in shop {}", categoryId, shopId);
        return categoryRepository.findByIdAndShopId(categoryId, shopId)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found in shop {}", categoryId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId));
                });
    }
}
