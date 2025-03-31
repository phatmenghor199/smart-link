package com.menghor.smart_shop.feature.setting.dto.request;

import com.menghor.smart_shop.enumations.Status;
import lombok.Data;

@Data
public class PlanStatusUpdateDto {
    private Status status;
}
