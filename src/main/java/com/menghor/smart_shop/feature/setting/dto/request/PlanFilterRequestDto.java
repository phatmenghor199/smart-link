package com.menghor.smart_shop.feature.setting.dto.request;

import com.menghor.smart_shop.enumations.Status;
import lombok.Data;

/**
 * Request DTO for filtering plans
 */
@Data
public class PlanFilterRequestDto {
    private String name;
    private Status status;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}