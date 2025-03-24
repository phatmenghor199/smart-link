package com.menghor.smart_shop.feature.auth.dto.request;

import com.menghor.smart_shop.enumations.RoleEnum;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Example of migrated DTO using jakarta.validation instead of javax.validation
 */
@Data
public class RegisterDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private RoleEnum role;
}