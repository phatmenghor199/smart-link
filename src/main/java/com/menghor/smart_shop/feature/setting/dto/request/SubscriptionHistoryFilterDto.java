package com.menghor.smart_shop.feature.setting.dto.request;

import com.menghor.smart_shop.enumations.Status;
import com.menghor.smart_shop.enumations.SubscriptionActionType;
import lombok.Data;

@Data
public class SubscriptionHistoryFilterDto {
    private Long userId;
    private Status status;
    private SubscriptionActionType actionType;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}