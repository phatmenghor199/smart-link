package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import lombok.Data;

@Data
public class CategoryRequestDto {
    private String name;
    private StatusData status;
    private ImageRequestDto image;
}