package com.menghor.smart_shop.feature.auth.mapper;

import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserResponseDto;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto mapToDto(UserEntity user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getUsername());

        String roles = user.getRoles().stream()
                .map(role -> role.getName().name()) // Convert RoleEnum to String
                .collect(Collectors.joining(", "));
        userDto.setUserRole(roles);

        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        return userDto;
    }

    public static UserResponseDto mapToListDto(List<UserDto> content, Page<UserEntity> user) {
        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setContent(content);
        userResponse.setPageNo(user.getNumber() + 1);
        userResponse.setPageSize(user.getSize());
        userResponse.setTotalElements(user.getTotalElements());
        userResponse.setTotalPages(user.getTotalPages());
        userResponse.setLast(user.isLast());
        return userResponse;
    }
}
