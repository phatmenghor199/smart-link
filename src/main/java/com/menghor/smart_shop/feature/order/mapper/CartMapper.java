package com.menghor.smart_shop.feature.order.mapper;

import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.order.dto.response.CartItemResponseDto;
import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;
import com.menghor.smart_shop.feature.order.model.CartEntity;
import com.menghor.smart_shop.feature.order.model.CartItemEntity;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Maps a CartEntity to a detailed CartResponseDto with careful handling of LOB data
     */
    public CartResponseDto toDetailedResponseDto(CartEntity cart) {
        if (cart == null) {
            return new CartResponseDto();
        }

        // Create cart response
        CartResponseDto responseDto = new CartResponseDto();
        responseDto.setId(cart.getId());

        // Set shop info
        if (cart.getShop() != null) {
            responseDto.setShopId(cart.getShop().getId());
            responseDto.setShopName(cart.getShop().getName());
        }

        // Set timestamps
        responseDto.setCreatedAt(cart.getCreatedAt());
        responseDto.setUpdatedAt(cart.getUpdatedAt());

        // Format timestamp
        if (cart.getUpdatedAt() != null) {
            responseDto.setFormattedTimestamp("Updated: " + cart.getUpdatedAt().format(DATE_FORMATTER));
        } else if (cart.getCreatedAt() != null) {
            responseDto.setFormattedTimestamp("Created: " + cart.getCreatedAt().format(DATE_FORMATTER));
        }

        // Calculate cart items with careful handling of LOBs
        List<CartItemResponseDto> itemDtos = new ArrayList<>();
        double subtotal = 0.0;
        double totalDiscount = 0.0;
        int totalQuantity = 0;

        if (cart.getCartItems() != null) {
            for (CartItemEntity item : cart.getCartItems()) {
                try {
                    // Map item details
                    CartItemResponseDto itemDto = mapCartItemToDto(item);
                    itemDtos.add(itemDto);

                    // Update totals
                    if (itemDto.getOriginalPrice() != null && itemDto.getQuantity() != null) {
                        subtotal += itemDto.getOriginalPrice() * itemDto.getQuantity();
                    }

                    if (itemDto.getDiscountAmount() != null) {
                        totalDiscount += itemDto.getDiscountAmount();
                    }

                    if (itemDto.getQuantity() != null) {
                        totalQuantity += itemDto.getQuantity();
                    }
                } catch (Exception e) {
                    System.err.println("Error mapping cart item: " + e.getMessage());
                }
            }
        }

        // Set cart totals
        responseDto.setCartItems(itemDtos);
        responseDto.setItemCount(itemDtos.size());
        responseDto.setTotalQuantity(totalQuantity);
        responseDto.setSubtotal(subtotal);
        responseDto.setTotalDiscount(totalDiscount);
        responseDto.setTotal(subtotal - totalDiscount);

        return responseDto;
    }

    /**
     * Maps a CartItemEntity to a CartItemResponseDto with safe handling of LOB data
     */
    private CartItemResponseDto mapCartItemToDto(CartItemEntity item) {
        if (item == null) {
            return null;
        }

        ProductEntity product = item.getProduct();
        ProductSizeEntity size = item.getSize();

        // Calculate prices
        Double originalPrice = 0.0;
        if (size != null && size.getPrice() != null) {
            originalPrice = size.getPrice();
        } else if (product != null && product.getPrice() != null) {
            originalPrice = product.getPrice();
        }

        Double unitPrice = item.getPrice() != null ? item.getPrice() : originalPrice;
        Double totalPrice = unitPrice * item.getQuantity();
        Double discountAmount = (originalPrice - unitPrice) * item.getQuantity();

        // Build response with safe image URL handling
        CartItemResponseDto.CartItemResponseDtoBuilder builder = CartItemResponseDto.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .originalPrice(originalPrice)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .discountType(item.getDiscountType())
                .discountValue(item.getDiscountValue())
                .discountAmount(discountAmount);

        // Add product information with safe handling
        if (product != null) {
            builder.productId(product.getId())
                    .productName(product.getName())
                    .productDescription(product.getDescription());

            // Set product image URL (not the image data)
            if (product.getMainImage() != null) {
                builder.productImageUrl("/api/v1/images/" + product.getMainImage().getId());
            }

            // Set additional image URLs (not the image data)
            if (product.getAdditionalImages() != null && !product.getAdditionalImages().isEmpty()) {
                List<String> imageUrls = product.getAdditionalImages().stream()
                        .map(img -> "/api/v1/images/" + img.getId())
                        .collect(Collectors.toList());
                builder.additionalImageUrls(imageUrls);
            }

            // Set category info if available
            if (product.getCategory() != null) {
                builder.categoryId(product.getCategory().getId())
                        .categoryName(product.getCategory().getName());
            }
        }

        // Add size information if available
        if (size != null) {
            builder.sizeId(size.getId())
                    .sizeName(size.getSize());

            // Set size image URL (not the image data)
            if (size.getMainImage() != null) {
                builder.sizeImageUrl("/api/v1/images/" + size.getMainImage().getId());
            }
        }

        // Create the DTO
        CartItemResponseDto dto = builder.build();

        // Format discount display text
        if (dto.getDiscountType() != null && dto.getDiscountValue() != null) {
            if (dto.getDiscountType().name().equals("PERCENTAGE")) {
                dto.setDiscountDisplay(dto.getDiscountValue().intValue() + "% OFF");
            } else {
                dto.setDiscountDisplay("$" + dto.getDiscountValue() + " OFF");
            }
        } else if (originalPrice > unitPrice) {
            int percentage = (int) Math.round(((originalPrice - unitPrice) / originalPrice) * 100);
            if (percentage > 0) {
                dto.setDiscountDisplay(percentage + "% OFF");
            }
        }

        return dto;
    }

    /**
     * Safely gets image URL without accessing LOB data
     */
    private String getImageUrl(ImageEntity image) {
        if (image == null || image.getId() == null) {
            return null;
        }
        return "/api/v1/images/" + image.getId();
    }
}