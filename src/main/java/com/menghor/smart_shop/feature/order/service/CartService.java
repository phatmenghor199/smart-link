package com.menghor.smart_shop.feature.order.service;

import com.menghor.smart_shop.feature.order.dto.request.CartItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;

/**
 * Service for managing shopping cart operations
 */
public interface CartService {
    /**
     * Add an item to the cart or increase quantity if already exists
     * @param cartItemRequestDto Details of the item to add
     * @return Updated cart information
     */
    CartResponseDto addToCart(CartItemRequestDto cartItemRequestDto);

    /**
     * Remove an item from the cart
     * @param cartItemId ID of the cart item to remove
     * @return Updated cart information
     */
    CartResponseDto removeFromCart(Long cartItemId);

    /**
     * Clear all items from the cart
     * @return Empty cart information
     */
    CartResponseDto clearCart();

    /**
     * Get the current cart for the authenticated shop
     * @return Current cart information
     */
    CartResponseDto getCart();

    /**
     * Update the quantity of an item in the cart
     * @param cartItemId ID of the cart item
     * @param quantity New quantity (0 to remove)
     * @return Updated cart information
     */
    CartResponseDto setQuantity(Long cartItemId, int quantity);
}