package com.menghor.smart_shop.feature.auth.mapper;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserResponseDto;
import com.menghor.smart_shop.feature.auth.models.Role;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "userRole")
    @Mapping(source = "shop", target = "shop")  // Map shop to shop DTO
    UserDto toDto(UserEntity user);


    default UserResponseDto toPageDto(List<UserDto> content, Page<UserEntity> userPage) {
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setContent(content);
        userResponse.setPageNo(userPage.getNumber() + 1); // Convert 0-indexed to 1-indexed page number
        userResponse.setPageSize(userPage.getSize());
        userResponse.setTotalElements(userPage.getTotalElements());
        userResponse.setTotalPages(userPage.getTotalPages());
        userResponse.setLast(userPage.isLast());
        return userResponse;
    }

    default RoleEnum mapRoles(List<Role> roles) {
        // Handle case if there are roles; for example, pick the first role if available
        if (roles != null && !roles.isEmpty()) {
            return RoleEnum.valueOf(roles.get(0).getName().name()); // Convert the first role to RoleEnum
        }
        return null;  // Return null if no roles are present
    }

    default ShopResponseDto mapShopToShopDto(ShopEntity shop) {
        if (shop == null) return null;  // If shop is null, return null
        ShopResponseDto shopDto = new ShopResponseDto();
        shopDto.setId(shop.getId());
        shopDto.setName(shop.getName());
        shopDto.setLocation(shop.getLocation());
        shopDto.setCreatedAt(shop.getCreatedAt());
        shopDto.setUpdatedAt(shop.getUpdatedAt());
        return shopDto;
    }

}
