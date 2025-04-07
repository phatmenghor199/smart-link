package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.ProductFilterDto;
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
import com.menghor.smart_shop.feature.customer.specification.ProductSpecification;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.feature.setting.repository.ImageRepository;
import com.menghor.smart_shop.feature.customer.service.ProductService;
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

import java.util.ArrayList;
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
    private final ProductSizeRepository productSizeRepository;
    private final ImageRepository imageRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Creating product for shopId: {} by userId: {}", shopId, userId);

        // Find and validate category
        CategoryEntity category = getUserOwnedCategory(requestDto.getCategoryId(), shopId);

        // Convert DTO to entity
        ProductEntity product = productMapper.toEntity(requestDto);
        product.setShop(shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, shopId))));
        product.setCategory(category);

        // Set default status if not provided
        if (product.getStatus() == null) {
            product.setStatus(StatusData.ACTIVE);
        }

        // Handle main image
        if (product.getMainImage() != null) {
            product.getMainImage().setReferenceType("product");
        }

        // Save initial product first
        ProductEntity savedProduct = productRepository.saveAndFlush(product);

        // Handle additional images
        if (requestDto.getAdditionalImages() != null && !requestDto.getAdditionalImages().isEmpty()) {
            List<ImageEntity> additionalImages = requestDto.getAdditionalImages().stream()
                    .map(imageDto -> {
                        ImageEntity image = new ImageEntity();
                        image.setBase64Image(imageDto.getBase64Image());
                        image.setImageType(imageDto.getImageType());
                        image.setReferenceType("product");
                        return imageRepository.save(image);
                    })
                    .collect(Collectors.toList());

            productMapper.updateProductImages(savedProduct, additionalImages);
        }

        // Handle product sizes
        if (requestDto.getSizes() != null && !requestDto.getSizes().isEmpty()) {
            List<ProductSizeEntity> sizes = new ArrayList<>();
            for (ProductSizeRequestDto sizeRequestDto : requestDto.getSizes()) {
                // Create size entity
                ProductSizeEntity size = productMapper.toSizeEntity(sizeRequestDto);

                // CRITICAL: Set the product reference
                size.setProduct(savedProduct);

                // Validate discount and set default status
                size.validateDiscount();
                if (size.getStatus() == null) {
                    size.setStatus(StatusData.ACTIVE);
                }

                // Handle main image for size
                if (size.getMainImage() != null) {
                    size.getMainImage().setReferenceType("product_size");
                }

                // Save the size entity
                ProductSizeEntity savedSize = productSizeRepository.save(size);
                sizes.add(savedSize);

                // Handle additional images for size
                if (sizeRequestDto.getAdditionalImages() != null && !sizeRequestDto.getAdditionalImages().isEmpty()) {
                    List<ImageEntity> sizeImages = sizeRequestDto.getAdditionalImages().stream()
                            .map(imageDto -> {
                                ImageEntity image = new ImageEntity();
                                image.setBase64Image(imageDto.getBase64Image());
                                image.setImageType(imageDto.getImageType());
                                image.setReferenceType("product_size");
                                return imageRepository.save(image);
                            })
                            .collect(Collectors.toList());

                    productMapper.updateProductSizeImages(savedSize, sizeImages);
                    productSizeRepository.save(savedSize);
                }
            }

            // Update product sizes list
            savedProduct.setSizes(sizes);

            // Clear product promotion if it has sizes
            if (!savedProduct.getSizes().isEmpty()) {
                savedProduct.resetDiscount();
            }
        }

        // Final save to ensure all changes are persisted
        ProductEntity finalProduct = productRepository.saveAndFlush(savedProduct);
        log.info("Product created successfully in shop {}", shopId);
        return productMapper.toDto(finalProduct);
    }

    @Override
    public ProductResponseDto addSizesToProduct(Long productId, List<ProductSizeRequestDto> sizeRequest) {
        log.info("Adding sizes to product with ID {}", productId);
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Ensure the product belongs to the current shop
        Long shopId = securityUtils.getShopIdFromToken();
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException(String.format(ErrorMessages.USER_DOES_NOT_OWN_SHOP, securityUtils.getUserIdFromToken(), shopId));
        }

        // Save product sizes
        List<ProductSizeEntity> sizes = sizeRequest.stream()
                .map(sizeRequestDto -> {
                    ProductSizeEntity size = productMapper.toSizeEntity(sizeRequestDto);
                    size.setProduct(product);
                    size.validateDiscount();

                    // Set default status
                    if (size.getStatus() == null) {
                        size.setStatus(StatusData.ACTIVE);
                    }

                    // Handle size main image
                    if (size.getMainImage() != null) {
                        size.getMainImage().setReferenceType("product_size");
                    }

                    return productSizeRepository.save(size);
                })
                .toList();

        product.getSizes().addAll(sizes);

        // Clear product promotion if it has sizes
        if (!product.getSizes().isEmpty()) {
            product.resetDiscount();
        }

        ProductEntity updatedProduct = productRepository.save(product);
        log.info("Sizes added successfully to product {}", productId);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    public CustomPaginationResponseDto<ProductResponseDto> getProductsByShopWithFilter(ProductFilterDto filterDto) {
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Getting products for shop ID {} with filter: {}", shopId, filterDto);

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterDto.getPageNo() != null ? filterDto.getPageNo() : 1,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10);

        // Create specification
        Specification<ProductEntity> spec = ProductSpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                shopId,
                filterDto.getCategoryId(),
                filterDto.getHasPromotion()
        );

        // Create pageable with sorting
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1,
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute query
        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

        // Use mapper's pagination method
        return productMapper.toPaginationDto(productPage.getContent(), productPage);
    }

    @Override
    public ProductResponseDto getProductById(Long id) {
        log.info("Getting product by ID {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));
        return productMapper.toDto(product);
    }

    @Override
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        log.info("Updating product with ID {}", id);
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        // Update category if provided
        if (requestDto.getCategoryId() != null) {
            CategoryEntity category = getUserOwnedCategory(requestDto.getCategoryId(), shopId);
            product.setCategory(category);
        }

        // Update product fields
        productMapper.updateProductFromDto(requestDto, product);

        // Save updated product
        ProductEntity updatedProduct = productRepository.save(product);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    public ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto) {
        log.info("Updating product size with ID {} for product with ID {}", sizeId, productId);
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        // Find the size
        ProductSizeEntity size = productSizeRepository.findById(sizeId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_SIZE_NOT_FOUND, sizeId)));

        // Ensure the size belongs to this product
        if (!size.getProduct().getId().equals(productId)) {
            throw new NotFoundException(
                    String.format(ErrorMessages.PRODUCT_SIZE_NOT_ASSOCIATED_WITH_PRODUCT, sizeId, productId));
        }

        // Update size fields
        productMapper.updateSizeFromDto(sizeRequestDto, size);
        size.validateDiscount();

        productSizeRepository.save(size);

        return productMapper.toDto(product);
    }

    @Override
    public ProductResponseDto deleteProduct(Long id) {
        log.info("Deleting product with ID {}", id);
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        // Create a copy of the product data for the response
        ProductResponseDto responseDto = productMapper.toDto(product);

        // Delete the product
        productRepository.delete(product);
        log.info("Product deleted successfully with ID: {}", id);

        return responseDto;
    }

    @Override
    public ProductResponseDto resetDiscountForProduct(Long productId) {
        log.info("Resetting discount for product with ID {}", productId);
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        // Reset main product discount
        product.resetDiscount();

        // Reset all size discounts
        for (ProductSizeEntity size : product.getSizes()) {
            size.resetDiscount();
            productSizeRepository.save(size);
        }

        // Save updated product
        ProductEntity updatedProduct = productRepository.save(product);
        log.info("All discounts reset for product with ID: {}", productId);
        return productMapper.toDto(updatedProduct);
    }

    // Helper method to get a category that belongs to the user's shop
    private CategoryEntity getUserOwnedCategory(Long categoryId, Long shopId) {
        return categoryRepository.findByIdAndShopId(categoryId, shopId)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found in shop {}", categoryId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId));
                });
    }
}
