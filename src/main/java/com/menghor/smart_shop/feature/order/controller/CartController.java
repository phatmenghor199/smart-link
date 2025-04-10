package com.menghor.smart_shop.feature.order.controller;

import com.menghor.smart_shop.exceptions.response.ApiResponse;
import com.menghor.smart_shop.feature.order.dto.request.CartItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;
import com.menghor.smart_shop.feature.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    /**
     * Add an item to the cart
     */
    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CartResponseDto> addToCart(@Valid @RequestBody CartItemRequestDto cartItemRequestDto) {
        log.info("Received request to add item to cart: {}", cartItemRequestDto);
        try {
            CartResponseDto response = cartService.addToCart(cartItemRequestDto);
            return new ApiResponse<>("Success", "Item added to cart successfully", response);
        } catch (Exception e) {
            log.error("Error adding to cart: {}", e.getMessage(), e);
            return new ApiResponse<>("Error", "Failed to add item to cart: " + e.getMessage(), null);
        }
    }

    /**
     * Remove an item from the cart
     */
    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartResponseDto> removeFromCart(@PathVariable Long cartItemId) {
        log.info("Received request to remove item with ID: {} from cart", cartItemId);
        try {
            CartResponseDto response = cartService.removeFromCart(cartItemId);
            return new ApiResponse<>("Success", "Item removed from cart successfully", response);
        } catch (Exception e) {
            log.error("Error removing from cart: {}", e.getMessage(), e);
            return new ApiResponse<>("Error", "Failed to remove item from cart: " + e.getMessage(), null);
        }
    }

    /**
     * Clear all items from the cart
     */
    @DeleteMapping
    public ApiResponse<CartResponseDto> clearCart() {
        log.info("Received request to clear cart");
        try {
            CartResponseDto response = cartService.clearCart();
            return new ApiResponse<>("Success", "Cart cleared successfully", response);
        } catch (Exception e) {
            log.error("Error clearing cart: {}", e.getMessage(), e);
            return new ApiResponse<>("Error", "Failed to clear cart: " + e.getMessage(), null);
        }
    }

    /**
     * Get the current cart
     */
    @GetMapping
    public ApiResponse<CartResponseDto> getCart() {
        log.info("Received request to get current cart");
        try {
            CartResponseDto response = cartService.getCart();
            return new ApiResponse<>("Success", "Cart retrieved successfully", response);
        } catch (Exception e) {
            log.error("Error getting cart: {}", e.getMessage(), e);
            return new ApiResponse<>("Error", "Failed to retrieve cart: " + e.getMessage(), null);
        }
    }

    /**
     * Update item quantity in the cart
     */
    @PatchMapping("/items/{cartItemId}/quantity/{quantity}")
    public ApiResponse<CartResponseDto> updateQuantity(
            @PathVariable Long cartItemId,
            @PathVariable int quantity) {
        log.info("Received request to update quantity to {} for item ID: {}", quantity, cartItemId);
        try {
            CartResponseDto response = cartService.setQuantity(cartItemId, quantity);
            return new ApiResponse<>("Success", "Item quantity updated successfully", response);
        } catch (Exception e) {
            log.error("Error updating quantity: {}", e.getMessage(), e);
            return new ApiResponse<>("Error", "Failed to update quantity: " + e.getMessage(), null);
        }
    }
}