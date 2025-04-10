package com.menghor.smart_shop.feature.order.service.impl;

import com.menghor.smart_shop.exceptions.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final SecurityUtils securityUtils;
    private final ProductRepository productRepository;
    private final ProductSizeRepository productSizeRepository;

    @Override
    @Transactional
    public CartResponseDto addToCart(CartItemRequestDto cartItemRequestDto) {
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Adding item to cart: {}", cartItemRequestDto);

        Optional<CartEntity> optionalCart = cartRepository.findByShopId(shopId).stream().findFirst();

        CartEntity cart = optionalCart.orElseGet(() -> {
            CartEntity newCart = new CartEntity();
            newCart.setShop(securityUtils.getShopFromToken());
            log.info("Creating new cart for shop: {}", newCart.getShop().getId());
            return cartRepository.save(newCart);
        });

        ProductEntity product = productRepository.findById(cartItemRequestDto.getProductId()).orElseThrow(() -> new NotFoundException("Product not found"));

        // Check if the item already exists in the cart
        Optional<CartItemEntity> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(cartItemRequestDto.getProductId()) &&
                        (cartItemRequestDto.getSizeId() == null || item.getSize().getId().equals(cartItemRequestDto.getSizeId())))
                .findFirst();


        if (existingCartItem.isPresent()) {
            // Update quantity if item already exists
            CartItemEntity cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequestDto.getQuantity());
            log.info("Updating quantity for cart item: {}", cartItem.getId());
            cartItemRepository.save(cartItem);
        } else {
            // Add new item to cart
            log.info("Adding new item to cart");
            CartItemEntity cartItem = new CartItemEntity();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(cartItemRequestDto.getQuantity());

            if (cartItemRequestDto.getSizeId() != null) {
                log.info("Size ID: {}", cartItemRequestDto.getSizeId());
                ProductSizeEntity size = productSizeRepository.findById(cartItemRequestDto.getSizeId()).orElseThrow(() -> new NotFoundException("Size not found"));
                cartItem.setSize(size);
                cartItem.setPrice(size.getFinalPrice());
            } else {
                log.info("No size ID");
                cartItem.setPrice(product.getPrice());
            }

            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        log.info("Returning updated cart");
        return cartMapper.toCartDto(cart);
    }

    @Override
    @Transactional
    public CartResponseDto removeFromCart(Long cartItemId) {
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Removing cart item: {}", cartItemId);
        CartEntity cart = cartRepository.findByShopId(shopId).stream().findFirst().orElseThrow(() -> new NotFoundException("Cart not found"));

        CartItemEntity cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException("Cart item not found"));
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        log.info("Returning updated cart");
        return cartMapper.toCartDto(cart);
    }

    @Transactional
    @Override
    public CartResponseDto clearCart() {
        log.info("Clearing cart");
        Long shopId = securityUtils.getShopIdFromToken();
        CartEntity cart = cartRepository.findByShopId(shopId).stream().findFirst().orElseThrow(() -> new NotFoundException("Cart not found"));

        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();

        CartResponseDto cartResponse = cartMapper.toCartDto(cart);
        log.info("Returning empty cart");
        return cartResponse;
    }

    @Override
    public CartResponseDto getCart() {
        log.info("Getting cart");
        Long shopId = securityUtils.getShopIdFromToken();
        Optional<CartEntity> optionalCart = cartRepository.findByShopId(shopId).stream().findFirst();

        CartEntity cart = optionalCart.orElseGet(() -> {
            CartEntity newCart = new CartEntity();
            newCart.setShop(securityUtils.getShopFromToken());
            return cartRepository.save(newCart);
        });

        log.info("Returning cart");
        return cartMapper.toCartDto(cart);
    }

    @Transactional
    @Override
    public CartResponseDto setQuantity(Long cartItemId, int quantity) {
        log.info("Setting quantity for cart item: {}", cartItemId);
        if (quantity < 0) {
            log.error("Quantity cannot be negative");
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        CartItemEntity cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (quantity == 0) {
            log.info("Removing cart item: {}", cartItemId);
            cartItem.getCart().getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            log.info("Updating quantity for cart item: {}", cartItemId);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        log.info("Returning updated cart");
        return cartMapper.toCartDto(cartItem.getCart());
    }

    private Double calculateTotalAmount(CartEntity cart) {
        return cart.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

}
