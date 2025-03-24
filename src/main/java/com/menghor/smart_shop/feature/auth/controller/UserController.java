package com.menghor.smart_shop.feature.auth.controller;

import com.menghor.smart_shop.constants.SuccessMessages;
import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.auth.dto.request.ChangePasswordByAdminRequestDto;
import com.menghor.smart_shop.feature.auth.dto.request.ChangePasswordRequestDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserResponseDto;
import com.menghor.smart_shop.feature.auth.service.UserService;
import com.menghor.smart_shop.utils.pagiantion.PaginationUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ApiResponse<UserResponseDto> getAllUser(
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "search", required = false) String search
    ) {

        PaginationUtils.validatePagination(pageNo, pageSize);

        final UserResponseDto allUser = userService.getAllUser(pageNo - 1, pageSize, search);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.ALL_USERS_FETCHED_SUCCESSFULLY, allUser);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDto> getUserDetail(@PathVariable Long id) {
        final UserDto userById = userService.getUserById(id);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.USER_FETCHED_SUCCESSFULLY, userById);
    }

    @GetMapping("/token")
    public ApiResponse<UserDto> getUserByToken() {
        final UserDto userByToken = userService.getUserByToken();
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.USER_FETCHED_SUCCESSFULLY, userByToken);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<UserDto> deleteUserById(@PathVariable("id") Long userId) {
        final UserDto userDto = userService.deleteUserId(userId);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.USER_FETCHED_SUCCESSFULLY, userDto);
    }

    @PostMapping("change-password")
    public ApiResponse<UserDto> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordDto) {
        final UserDto userDto = userService.changePassword(changePasswordDto);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.PASSWORD_CHANGED_SUCCESSFULLY, userDto);
    }

    @PostMapping("change-password-by-admin")
    public ApiResponse<UserDto> changePasswordByAdmin(@Valid @RequestBody ChangePasswordByAdminRequestDto changePasswordDto) {
        final UserDto userDto = userService.changePasswordByAdmin(changePasswordDto);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.PASSWORD_CHANGED_SUCCESSFULLY, userDto);
    }
}