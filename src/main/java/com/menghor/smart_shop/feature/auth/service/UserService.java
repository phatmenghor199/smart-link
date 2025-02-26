package com.menghor.smart_shop.feature.auth.service;

import com.menghor.smart_shop.feature.auth.dto.request.ChangePasswordByAdminRequestDto;
import com.menghor.smart_shop.feature.auth.dto.request.ChangePasswordRequestDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserResponseDto;

public interface UserService {
    UserResponseDto getAllUser(int pageNo, int pageSize, String search);
    UserDto getUserById(Long id);
    UserDto getUserByToken();

    UserDto deleteUserId(Long id);
    UserDto changePassword(ChangePasswordRequestDto requestDto);
    UserDto changePasswordByAdmin(ChangePasswordByAdminRequestDto requestDto);
}