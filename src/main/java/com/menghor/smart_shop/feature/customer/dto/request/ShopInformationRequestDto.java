package com.menghor.smart_shop.feature.customer.dto.request;

import lombok.Data;

@Data
public class ShopInformationRequestDto {
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
}