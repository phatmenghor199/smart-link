package com.menghor.smart_shop.feature.order.controller;

import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.order.dto.request.CartItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;
import com.menghor.smart_shop.feature.order.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    public ApiResponse<CartResponseDto> addToCart(@RequestBody CartItemRequestDto cartItemRequestDto) {
        log.info("Received request to add to cart");
        return new ApiResponse<>("Success", "Add to cart successfully", cartService.addToCart(cartItemRequestDto));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartResponseDto> removeFromCart(@PathVariable Long cartItemId) {
        log.info("Received request to remove cart item id");
        return new ApiResponse<>("Success", "Remove cart item id successfully", cartService.removeFromCart(cartItemId));
    }

    @DeleteMapping
    public ApiResponse<CartResponseDto> clearCart() {
        log.info("Received request to clear cart");
        return new ApiResponse<>("Success", "Clear cart item successfully", cartService.clearCart());
    }

    @GetMapping("/shop")
    public ApiResponse<CartResponseDto> getCart() {
        log.info("Received request to get cart by shop item");
        return new ApiResponse<>("Success", "Get cart by shop item successfully", cartService.getCart());
    }

    @PutMapping("/items/{cartItemId}/quantity/{quantity}")
    public ApiResponse<CartResponseDto> setQuantity(@PathVariable Long cartItemId, @PathVariable int quantity) {
        log.info("Received request to update cart item quantity");
        return new ApiResponse<>("Success", "Update cart item successfully", cartService.setQuantity(cartItemId, quantity));
    }
}