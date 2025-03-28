package com.menghor.smart_shop.feature.auth.dto.request;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.enumations.Status;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDto {
    @Email(message = "Email should be valid")
    private String username;
    
    private RoleEnum role;
    
    private Status status;
}