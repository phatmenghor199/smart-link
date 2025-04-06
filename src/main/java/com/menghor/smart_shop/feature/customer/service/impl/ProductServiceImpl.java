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
import com.menghor.smart_shop.feature.customer.service.ProductService;
import com.menghor.smart_shop.feature.customer.specification.ProductSpecification;
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

import java.time.LocalDate;
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
    private final ProductSizeRepository productSizeRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Creating product for shopId: {} by userId: {}", shopId, userId);

        CategoryEntity category = getUserOwnedCategory(requestDto.getCategoryId(), shopId);
        ProductEntity product = productMapper.toEntity(requestDto);
        product.setShop(shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.error("Shop with id {} not found", shopId);
                    return new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, shopId));
                }));

        product.setCategory(category);

        // Set default status if not provided
        if (product.getStatus() == null) {
            product.setStatus(StatusData.ACTIVE);
        }

        // Set image reference type if image is provided
        if (product.getImage() != null) {
            product.getImage().setReferenceType("product");
            log.info("Setting referenceType to 'product' for new image");
        }

        ProductEntity productEntity = productRepository.saveAndFlush(product);

        if (requestDto.getSizes() != null && !requestDto.getSizes().isEmpty()) {
            List<ProductSizeEntity> sizes = requestDto.getSizes().stream()
                    .map(sizeRequestDto -> {
                        ProductSizeEntity size = productMapper.toSizeEntity(sizeRequestDto);
                        size.setProduct(productEntity); // Ensure the relationship is set
                        size.validateDiscount(); // Validate the discount

                        // Set default status if not provided
                        if (size.getStatus() == null) {
                            size.setStatus(StatusData.ACTIVE);
                        }

                        productSizeRepository.save(size); // Save each size
                        return size;
                    })
                    .toList();

            productEntity.setSizes(sizes);

            // Clear product promotion details if it has sizes
            if (!productEntity.getSizes().isEmpty()) {
                log.info("Clearing promotion details for product with sizes");
                productEntity.resetDiscount();
            }
        }

        // Save the product again to persist the changes with sizes
        ProductEntity savedProduct = productRepository.save(productEntity);

        log.info("Product created successfully in shop {}", shopId);
        return productMapper.toDto(savedProduct);
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
                    size.setProduct(product); // Ensure the relationship is set
                    size.validateDiscount();

                    // Set default status if not provided
                    if (size.getStatus() == null) {
                        size.setStatus(StatusData.ACTIVE);
                    }

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

        // Create pageable with sorting (newest first)
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1, // Convert to 0-based
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute query with specification
        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

        // Map entities to DTOs
        List<ProductResponseDto> productDtos = productPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        CustomPaginationResponseDto<ProductResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(productDtos);
        response.setPageNo(filterDto.getPageNo());
        response.setPageSize(filterDto.getPageSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    @Override
    public List<ProductResponseDto> getProductsByShop() {
        log.info("Getting all products for current shop");

        Long shopId = securityUtils.getShopIdFromToken();
        List<ProductEntity> products = productRepository.findByShopIdAndStatus(shopId, StatusData.ACTIVE);
        List<ProductResponseDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} products for shop ID: {}", products.size(), shopId);
        return productDtos;
    }

    @Override
    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        log.info("Getting products for category ID: {}", categoryId);

        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId));
        }

        // Get products for the category
        List<ProductEntity> products = productRepository.findByCategoryIdAndStatus(categoryId, StatusData.ACTIVE);
        List<ProductResponseDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} products for category ID: {}", products.size(), categoryId);
        return productDtos;
    }

    @Override
    public CustomPaginationResponseDto<ProductResponseDto> getProductsByCategoryWithFilter(
            Long categoryId, ProductFilterDto filterDto) {
        log.info("Getting products for category ID: {} with filter: {}", categoryId, filterDto);

        // Check if category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId));
        }

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterDto.getPageNo() != null ? filterDto.getPageNo() : 1,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10);

        // Force the category ID in the filter
        filterDto.setCategoryId(categoryId);

        // Get shop ID from the token
        Long shopId = securityUtils.getShopIdFromToken();

        // Create specification
        Specification<ProductEntity> spec = ProductSpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                shopId,
                categoryId,
                filterDto.getHasPromotion()
        );

        // Create pageable with sorting (newest first)
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1, // Convert to 0-based
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute query with specification
        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

        // Map entities to DTOs
        List<ProductResponseDto> productDtos = productPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        CustomPaginationResponseDto<ProductResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(productDtos);
        response.setPageNo(filterDto.getPageNo());
        response.setPageSize(filterDto.getPageSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        log.info("Getting all products");
        List<ProductEntity> products = productRepository.findAll();
        List<ProductResponseDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} products", products.size());
        return productDtos;
    }

    @Override
    public CustomPaginationResponseDto<ProductResponseDto> getAllProductsWithFilter(ProductFilterDto filterDto) {
        log.info("Getting all products with filter: {}", filterDto);

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterDto.getPageNo() != null ? filterDto.getPageNo() : 1,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10);

        // Create specification
        Specification<ProductEntity> spec = ProductSpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                null, // No shop filter for admin view
                filterDto.getCategoryId(),
                filterDto.getHasPromotion()
        );

        // Create pageable with sorting (newest first)
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1, // Convert to 0-based
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute query with specification
        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

        // Map entities to DTOs
        List<ProductResponseDto> productDtos = productPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        CustomPaginationResponseDto<ProductResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(productDtos);
        response.setPageNo(filterDto.getPageNo());
        response.setPageSize(filterDto.getPageSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        log.info("Updating product with ID {}", id);

        // Get current shop ID
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, id)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        // Update category if provided
        if (requestDto.getCategoryId() != null &&
                (product.getCategory() == null || !product.getCategory().getId().equals(requestDto.getCategoryId()))) {
            CategoryEntity category = getUserOwnedCategory(requestDto.getCategoryId(), shopId);
            product.setCategory(category);
        }

        // Handle image update
        if (requestDto.getImage() != null) {
            if (product.getImage() == null) {
                // Create new image if none exists
                ImageEntity newImage = new ImageEntity();
                newImage.setImageType(requestDto.getImage().getImageType());
                newImage.setBase64Image(requestDto.getImage().getBase64Image());
                newImage.setReferenceType("product");
                product.setImage(newImage);
                log.info("Created new image with referenceType: product");
            } else {
                // Update existing image instead of replacing it
                product.getImage().setImageType(requestDto.getImage().getImageType());
                product.getImage().setBase64Image(requestDto.getImage().getBase64Image());
                product.getImage().setReferenceType("product"); // Explicitly set referenceType again
                log.info("Updated existing image and set referenceType: product");
            }
        }

        // Update product fields
        productMapper.updateProductFromDto(requestDto, product);

        // IMPORTANT FIX: Re-set the referenceType AFTER the mapper update
        if (product.getImage() != null) {
            product.getImage().setReferenceType("product");
            log.info("Re-applying referenceType 'product' after mapper update");
        }

        // Save product changes
        ProductEntity updatedProduct = productRepository.save(product);

        // Handle sizes if provided
        if (requestDto.getSizes() != null && !requestDto.getSizes().isEmpty()) {
            for (ProductSizeRequestDto sizeRequestDto : requestDto.getSizes()) {
                if (sizeRequestDto.getId() != null) {
                    // Update existing size
                    ProductSizeEntity size = productSizeRepository.findById(sizeRequestDto.getId())
                            .orElseThrow(() -> new NotFoundException(
                                    String.format(ErrorMessages.PRODUCT_SIZE_NOT_FOUND, sizeRequestDto.getId())));

                    // Ensure the size belongs to this product
                    if (!size.getProduct().getId().equals(id)) {
                        throw new NotFoundException(
                                String.format(ErrorMessages.PRODUCT_SIZE_NOT_ASSOCIATED_WITH_PRODUCT,
                                        sizeRequestDto.getId(), id));
                    }

                    productMapper.updateSizeFromDto(sizeRequestDto, size);
                    size.validateDiscount();
                    productSizeRepository.save(size);
                } else {
                    // Add new size
                    ProductSizeEntity newSize = productMapper.toSizeEntity(sizeRequestDto);
                    newSize.setProduct(updatedProduct);
                    newSize.validateDiscount();

                    // Set default status if not provided
                    if (newSize.getStatus() == null) {
                        newSize.setStatus(StatusData.ACTIVE);
                    }

                    productSizeRepository.save(newSize);
                    updatedProduct.getSizes().add(newSize);
                }
            }

            // Clear product promotion details if it has sizes
            if (!updatedProduct.getSizes().isEmpty()) {
                updatedProduct.resetDiscount();
                productRepository.save(updatedProduct);
            }
        }

        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProductSize(Long productId, Long sizeId, ProductSizeRequestDto sizeRequestDto) {
        log.info("Updating product size with ID {} for product with ID {}", sizeId, productId);

        // Get current shop ID
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
    @Transactional
    public ProductResponseDto toggleProductStatus(Long productId) {
        log.info("Toggling status for product with ID {}", productId);

        // Get current shop ID
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        // Toggle status
        product.setStatus(product.getStatus() == StatusData.ACTIVE ? StatusData.INACTIVE : StatusData.ACTIVE);

        ProductEntity updatedProduct = productRepository.save(product);
        log.info("Product status toggled to {} for ID: {}", updatedProduct.getStatus(), productId);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto toggleProductSizeStatus(Long productId, Long sizeId) {
        log.info("Toggling status for size with ID {} of product with ID {}", sizeId, productId);

        // Get current shop ID
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

        // Toggle status
        size.setStatus(size.getStatus() == StatusData.ACTIVE ? StatusData.INACTIVE : StatusData.ACTIVE);

        productSizeRepository.save(size);
        log.info("Product size status toggled to {} for ID: {}", size.getStatus(), sizeId);

        return productMapper.toDto(product);
    }

    @Override
    public ProductResponseDto deleteProduct(Long id) {
        log.info("Deleting product with ID {}", id);

        // Get current shop ID
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

        // Delete the product (will cascade to sizes)
        productRepository.delete(product);
        log.info("Product deleted successfully with ID: {}", id);

        return responseDto;
    }

    @Override
    @Transactional
    public void resetExpiredDiscounts() {
        log.info("Resetting expired discounts for all products");
        LocalDate now = LocalDate.now();

        // Reset expired discounts for main products
        List<ProductEntity> productsWithExpiredDiscounts = productRepository.findByDiscountEndDateBefore(now);

        for (ProductEntity product : productsWithExpiredDiscounts) {
            log.info("Resetting expired discount for product with ID: {}", product.getId());
            product.resetDiscount();
            productRepository.save(product);
        }

        // Reset expired discounts for product sizes
        List<ProductSizeEntity> sizesWithExpiredDiscounts = productSizeRepository.findByDiscountEndDateBefore(now);

        for (ProductSizeEntity size : sizesWithExpiredDiscounts) {
            log.info("Resetting expired discount for product size with ID: {}", size.getId());
            size.resetDiscount();
            productSizeRepository.save(size);
        }

        log.info("Reset {} products and {} sizes with expired discounts",
                productsWithExpiredDiscounts.size(), sizesWithExpiredDiscounts.size());
    }

    @Override
    @Transactional
    public ProductResponseDto resetExpiredDiscountForProduct(Long productId) {
        log.info("Resetting expired discount for product with ID {}", productId);

        // Get current shop ID
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        LocalDate now = LocalDate.now();

        // Check if the main product discount has expired
        if (product.getDiscountEndDate() != null && product.getDiscountEndDate().isBefore(now)) {
            log.info("Resetting expired discount for product with ID: {}", productId);
            product.resetDiscount();
            productRepository.save(product);
        }

        // Check for expired discounts in sizes
        for (ProductSizeEntity size : product.getSizes()) {
            if (size.getDiscountEndDate() != null && size.getDiscountEndDate().isBefore(now)) {
                log.info("Resetting expired discount for product size with ID: {}", size.getId());
                size.resetDiscount();
                productSizeRepository.save(size);
            }
        }

        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductResponseDto resetDiscountForProduct(Long productId) {
        log.info("Resetting discount for product with ID {}", productId);

        // Get current shop ID
        Long shopId = securityUtils.getShopIdFromToken();

        // Find the product
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)));

        // Check if product belongs to current shop
        if (!product.getShop().getId().equals(shopId)) {
            throw new NotFoundException("Product does not belong to your shop");
        }

        // Reset main product discount regardless of expiration
        product.resetDiscount();
        productRepository.save(product);

        // Reset all size discounts
        for (ProductSizeEntity size : product.getSizes()) {
            size.resetDiscount();
            productSizeRepository.save(size);
        }

        log.info("All discounts reset for product with ID: {}", productId);
        return productMapper.toDto(product);
    }

    @Override
    public List<ProductResponseDto> getProductsWithActivePromotions() {
        log.info("Getting all products with active promotions");

        List<ProductEntity> products = productRepository.findAllWithActivePromotions();
        List<ProductResponseDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} products with active promotions", products.size());
        return productDtos;
    }

    @Override
    public CustomPaginationResponseDto<ProductResponseDto> getProductsWithActivePromotionsWithFilter(
            ProductFilterDto filterDto) {
        log.info("Getting all products with active promotions with filter: {}", filterDto);

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterDto.getPageNo() != null ? filterDto.getPageNo() : 1,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10);

        // Force has promotion to true
        filterDto.setHasPromotion(true);

        // Create specification for active promotions
        Specification<ProductEntity> spec = ProductSpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                null, // No shop filter for all promotions view
                filterDto.getCategoryId(),
                true // Has active promotion
        );

        // Create pageable with sorting (newest first)
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1, // Convert to 0-based
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute query with specification
        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

        // Map entities to DTOs
        List<ProductResponseDto> productDtos = productPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        CustomPaginationResponseDto<ProductResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(productDtos);
        response.setPageNo(filterDto.getPageNo());
        response.setPageSize(filterDto.getPageSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    @Override
    public List<ProductResponseDto> getProductsWithActivePromotionsByShop() {
        log.info("Getting products with active promotions for current shop");

        Long shopId = securityUtils.getShopIdFromToken();
        List<ProductEntity> products = productRepository.findAllWithActivePromotionsByShopId(shopId);
        List<ProductResponseDto> productDtos = products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        log.info("Found {} products with active promotions for shop ID: {}", products.size(), shopId);
        return productDtos;
    }

    @Override
    public CustomPaginationResponseDto<ProductResponseDto> getProductsWithActivePromotionsByShopWithFilter(
            ProductFilterDto filterDto) {
        log.info("Getting products with active promotions for current shop with filter: {}", filterDto);

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterDto.getPageNo() != null ? filterDto.getPageNo() : 1,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10);

        // Force has promotion to true
        filterDto.setHasPromotion(true);

        // Get shop ID from the token
        Long shopId = securityUtils.getShopIdFromToken();

        // Create specification for active promotions by shop
        Specification<ProductEntity> spec = ProductSpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                shopId,
                filterDto.getCategoryId(),
                true // Has active promotion
        );

        // Create pageable with sorting (newest first)
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1, // Convert to 0-based
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute query with specification
        Page<ProductEntity> productPage = productRepository.findAll(spec, pageable);

        // Map entities to DTOs
        List<ProductResponseDto> productDtos = productPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        CustomPaginationResponseDto<ProductResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(productDtos);
        response.setPageNo(filterDto.getPageNo());
        response.setPageSize(filterDto.getPageSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    /**
     * Helper method to get a category that belongs to the user's shop
     */
    private CategoryEntity getUserOwnedCategory(Long categoryId, Long shopId) {
        return categoryRepository.findByIdAndShopId(categoryId, shopId)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found in shop {}", categoryId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.CATEGORY_NOT_FOUND, categoryId));
                });
    }
}