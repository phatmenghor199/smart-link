package com.menghor.smart_shop.feature.auth.dto.request;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.enumations.Status;
import lombok.Data;

@Data
public class UserFilterDto {
    private String search;
    private Status status;
    private RoleEnum role;
    private Boolean hasActiveSubscription;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}