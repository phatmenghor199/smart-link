package com.menghor.smart_shop.feature.order.service;

import com.menghor.smart_shop.feature.order.dto.request.CartItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.request.OrderRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;

public interface CartService {
    CartResponseDto addToCart(CartItemRequestDto cartItemRequestDto);
    CartResponseDto removeFromCart(Long cartItemId);
    CartResponseDto clearCart();
    CartResponseDto getCart();
    CartResponseDto setQuantity(Long cartItemId, int quantity);
}
