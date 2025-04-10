package com.menghor.smart_shop.feature.order.service.impl;

import com.menghor.smart_shop.exceptions.error.BadRequestException;
import com.menghor.smart_shop.exceptions.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.ProductRepository;
import com.menghor.smart_shop.feature.customer.repository.ProductSizeRepository;
import com.menghor.smart_shop.feature.order.dto.request.CartItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;
import com.menghor.smart_shop.feature.order.mapper.CartMapper;
import com.menghor.smart_shop.feature.order.model.CartEntity;
import com.menghor.smart_shop.feature.order.model.CartItemEntity;
import com.menghor.smart_shop.feature.order.repository.CartItemRepository;
import com.menghor.smart_shop.feature.order.repository.CartRepository;
import com.menghor.smart_shop.feature.order.service.CartService;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;
    private final SecurityUtils securityUtils;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponseDto addToCart(CartItemRequestDto requestDto) {
        log.info("Adding item to cart: {}", requestDto);

        try {
            // Validate request
            validateCartItemRequest(requestDto);

            // Get shop ID for the current user
            Long shopId = securityUtils.getShopIdFromToken();

            // Get or create cart for this shop
            CartEntity cart = getOrCreateCartForShop(shopId);

            // Get product
            ProductEntity product = getProductById(requestDto.getProductId());

            // Check if item already exists in cart (same product and size)
            Optional<CartItemEntity> existingItem = findExistingCartItem(cart, requestDto);

            if (existingItem.isPresent()) {
                // Update existing item quantity
                updateExistingCartItem(existingItem.get(), requestDto);
            } else {
                // Create a new cart item
                CartItemEntity newItem = createNewCartItem(cart, product, requestDto);
            }

            // Save cart and map to response
            CartEntity savedCart = cartRepository.save(cart);
            return cartMapper.toDetailedResponseDto(savedCart);
        } catch (Exception e) {
            log.error("Error adding item to cart: {}", e.getMessage(), e);
            return createEmptyCartResponse("Error adding to cart: " + e.getMessage());
        }
    }

    private void validateCartItemRequest(CartItemRequestDto requestDto) {
        if (requestDto.getProductId() == null) {
            throw new BadRequestException("Product ID is required");
        }

        if (requestDto.getQuantity() == null || requestDto.getQuantity() <= 0) {
            requestDto.setQuantity(1); // Default to 1
        }
    }

    private ProductEntity getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
    }

    private void updateExistingCartItem(CartItemEntity item, CartItemRequestDto requestDto) {
        item.setQuantity(item.getQuantity() + requestDto.getQuantity());
        cartItemRepository.save(item);
        log.info("Updated quantity for existing cart item: {}", item.getId());
    }

    private CartItemEntity createNewCartItem(CartEntity cart, ProductEntity product, CartItemRequestDto requestDto) {
        CartItemEntity newItem = new CartItemEntity();
        newItem.setCart(cart);
        newItem.setProduct(product);
        newItem.setQuantity(requestDto.getQuantity());

        // Handle size if provided
        if (requestDto.getSizeId() != null) {
            ProductSizeEntity size = productSizeRepository.findById(requestDto.getSizeId())
                    .orElseThrow(() -> new NotFoundException("Size not found with ID: " + requestDto.getSizeId()));
            newItem.setSize(size);

            // Set price and discount info from size
            setPriceAndDiscountFromSize(newItem, size);
        } else {
            // Set price and discount info from product
            setPriceAndDiscountFromProduct(newItem, product);
        }

        CartItemEntity savedItem = cartItemRepository.save(newItem);
        cart.getCartItems().add(savedItem);
        log.info("Added new item to cart: {}", savedItem.getId());
        return savedItem;
    }

    private void setPriceAndDiscountFromSize(CartItemEntity item, ProductSizeEntity size) {
        if (size.isPromotionActive()) {
            item.setPrice(size.getFinalPrice());
            item.setDiscountType(size.getDiscountType());
            item.setDiscountValue(size.getDiscountValue());
        } else {
            item.setPrice(size.getPrice());
        }
    }

    private void setPriceAndDiscountFromProduct(CartItemEntity item, ProductEntity product) {
        if (product.isPromotionActive()) {
            item.setPrice(product.getFinalPrice());
            item.setDiscountType(product.getDiscountType());
            item.setDiscountValue(product.getDiscountValue());
        } else {
            item.setPrice(product.getPrice());
        }
    }

    @Override
    @Transactional
    public CartResponseDto removeFromCart(Long cartItemId) {
        log.info("Removing item from cart: {}", cartItemId);

        try {
            // Get shop ID for the current user
            Long shopId = securityUtils.getShopIdFromToken();

            // Get cart for this shop
            CartEntity cart = getCartForShop(shopId);

            // Find and remove cart item
            removeCartItem(cart, cartItemId);

            // Save cart and map to response
            CartEntity updatedCart = cartRepository.save(cart);
            return cartMapper.toDetailedResponseDto(updatedCart);
        } catch (Exception e) {
            log.error("Error removing item from cart: {}", e.getMessage(), e);
            return createEmptyCartResponse("Error removing from cart: " + e.getMessage());
        }
    }

    private void removeCartItem(CartEntity cart, Long cartItemId) {
        CartItemEntity item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found with ID: " + cartItemId));

        // Verify item belongs to this cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to your cart");
        }

        // Remove item
        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);
        log.info("Removed item from cart");
    }

    @Override
    @Transactional
    public CartResponseDto clearCart() {
        log.info("Clearing cart");

        try {
            // Get shop ID for the current user
            Long shopId = securityUtils.getShopIdFromToken();

            // Get cart for this shop
            CartEntity cart = getCartForShop(shopId);

            // Clear all items
            clearCartItems(cart);

            // Save cart and map to response
            CartEntity clearedCart = cartRepository.save(cart);
            return cartMapper.toDetailedResponseDto(clearedCart);
        } catch (Exception e) {
            log.error("Error clearing cart: {}", e.getMessage(), e);
            return createEmptyCartResponse("Error clearing cart: " + e.getMessage());
        }
    }

    private void clearCartItems(CartEntity cart) {
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        log.info("Cleared all items from cart");
    }

    @Override
    public CartResponseDto getCart() {
        log.info("Getting cart");

        try {
            // Get shop ID for the current user
            Long shopId = securityUtils.getShopIdFromToken();

            // Get or create cart for this shop
            CartEntity cart = getOrCreateCartForShop(shopId);

            // Map to response
            return cartMapper.toDetailedResponseDto(cart);
        } catch (Exception e) {
            log.error("Error getting cart: {}", e.getMessage(), e);
            return createEmptyCartResponse("Error getting cart: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CartResponseDto setQuantity(Long cartItemId, int quantity) {
        log.info("Setting quantity for cart item: {}", cartItemId);

        try {
            // Validate quantity
            validateQuantity(quantity);

            // Get shop ID for the current user
            Long shopId = securityUtils.getShopIdFromToken();

            // Get cart for this shop
            CartEntity cart = getCartForShop(shopId);

            // Find and update cart item
            updateCartItemQuantity(cart, cartItemId, quantity);

            // Save cart and map to response
            CartEntity updatedCart = cartRepository.save(cart);
            return cartMapper.toDetailedResponseDto(updatedCart);
        } catch (Exception e) {
            log.error("Error setting quantity: {}", e.getMessage(), e);
            return createEmptyCartResponse("Error setting quantity: " + e.getMessage());
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new BadRequestException("Quantity cannot be negative");
        }
    }

    private void updateCartItemQuantity(CartEntity cart, Long cartItemId, int quantity) {
        CartItemEntity item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("Cart item not found with ID: " + cartItemId));

        // Verify item belongs to this cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to your cart");
        }

        if (quantity == 0) {
            // Remove item if quantity is 0
            cart.getCartItems().remove(item);
            cartItemRepository.delete(item);
            log.info("Removed item from cart due to zero quantity");
        } else {
            // Update quantity
            item.setQuantity(quantity);
            cartItemRepository.save(item);
            log.info("Updated item quantity to: {}", quantity);
        }
    }

    /**
     * Create an empty cart response for error cases
     */
    private CartResponseDto createEmptyCartResponse(String errorMessage) {
        CartResponseDto fallbackResponse = new CartResponseDto();
        fallbackResponse.setCartItems(new ArrayList<>());
        fallbackResponse.setItemCount(0);
        fallbackResponse.setTotalQuantity(0);
        fallbackResponse.setSubtotal(0.0);
        fallbackResponse.setTotalDiscount(0.0);
        fallbackResponse.setTotal(0.0);
        fallbackResponse.setFormattedTimestamp(errorMessage);
        return fallbackResponse;
    }

    /**
     * Get or create cart for a shop
     */
    private CartEntity getOrCreateCartForShop(Long shopId) {
        // Try to find existing cart
        Optional<CartEntity> existingCart = cartRepository.findByShopId(shopId).stream().findFirst();

        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        // Create new cart
        ShopEntity shop = securityUtils.getShopFromToken();
        CartEntity newCart = new CartEntity();
        newCart.setShop(shop);
        log.info("Created new cart for shop: {}", shopId);

        return cartRepository.save(newCart);
    }

    /**
     * Get cart for a specific shop
     */
    private CartEntity getCartForShop(Long shopId) {
        return cartRepository.findByShopId(shopId).stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart not found for shop"));
    }

    /**
     * Find existing cart item
     */
    private Optional<CartItemEntity> findExistingCartItem(CartEntity cart, CartItemRequestDto requestDto) {
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(requestDto.getProductId()) &&
                        ((requestDto.getSizeId() == null && item.getSize() == null) ||
                                (requestDto.getSizeId() != null && item.getSize() != null &&
                                        item.getSize().getId().equals(requestDto.getSizeId()))))
                .findFirst();
    }
}