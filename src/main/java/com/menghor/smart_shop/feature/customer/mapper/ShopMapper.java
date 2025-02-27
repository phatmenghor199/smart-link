package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.customer.dto.request.ShopRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShopMapper {
    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    ShopEntity toEntity(ShopRequestDto dto);

    // Remove the expression and handle userRole mapping inside the method
    @Mapping(target = "user", source = "user", qualifiedByName = "userToDto")
    ShopResponseDto toDto(ShopEntity shopEntity);

    @Named("userToDto")
    default UserDto userToDto(UserEntity userEntity) {
        // Ensure we handle the roles as they are in a List<Role>
        RoleEnum roleEnum = null;
        if (userEntity.getRoles() != null && !userEntity.getRoles().isEmpty()) {
            // You can adjust this to choose the correct role if there are multiple roles
            roleEnum = RoleEnum.valueOf(userEntity.getRoles().get(0).getName().name()); // Assuming the first role is the one you need
        }

        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .userRole(roleEnum)
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateShopFromDto(ShopRequestDto dto, @MappingTarget ShopEntity entity);

    default CustomPaginationResponseDto<ShopResponseDto> mapToListDto(List<ShopResponseDto> content, Page<ShopEntity> shopEntities) {
        CustomPaginationResponseDto<ShopResponseDto> shopResponse = new CustomPaginationResponseDto<>();
        shopResponse.setContent(content);
        shopResponse.setPageNo(shopEntities.getNumber() + 1); // Convert 0-indexed to 1-indexed page number
        shopResponse.setPageSize(shopEntities.getSize());
        shopResponse.setTotalElements(shopEntities.getTotalElements());
        shopResponse.setTotalPages(shopEntities.getTotalPages());
        shopResponse.setLast(shopEntities.isLast());
        return shopResponse;
    }

}
